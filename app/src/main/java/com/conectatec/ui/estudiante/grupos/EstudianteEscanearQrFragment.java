package com.conectatec.ui.estudiante.grupos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.conectatec.databinding.FragmentEstudianteEscanearQrBinding;
import com.conectatec.ui.common.UiState;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Escaneo de QR con CameraX + ML Kit Barcode.
 * Al detectar un QR válido, llama a viewModel.unirseAGrupo(codigo).
 */
@AndroidEntryPoint
public class EstudianteEscanearQrFragment extends Fragment {

    private FragmentEstudianteEscanearQrBinding binding;
    private EstudianteGruposViewModel viewModel;
    private ExecutorService cameraExecutor;
    private BarcodeScanner scanner;
    private boolean yaDetectado = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstudianteEscanearQrBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(EstudianteGruposViewModel.class);
        cameraExecutor = Executors.newSingleThreadExecutor();

        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        scanner = BarcodeScanning.getClient(options);

        binding.btnVolverEscanearEst.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        viewModel.getUnirseState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UiState.Success) {
                if (binding == null) return;
                Snackbar.make(binding.getRoot(),
                        "Te uniste al grupo", Snackbar.LENGTH_SHORT).show();
                viewModel.cargarGrupos();
                NavHostFragment.findNavController(this).popBackStack();
            } else if (state instanceof UiState.Error) {
                yaDetectado = false; // permitir volver a intentar
                if (binding == null) return;
                Snackbar.make(binding.getRoot(),
                        ((UiState.Error<?>) state).mensaje, Snackbar.LENGTH_LONG).show();
            }
        });

        iniciarCamara();
    }

    private void iniciarCamara() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                vincularUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                if (binding != null) {
                    Snackbar.make(binding.getRoot(),
                            "No se pudo inicializar la cámara", Snackbar.LENGTH_LONG).show();
                }
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void vincularUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(binding.previewEscanearQr.getSurfaceProvider());

        ImageAnalysis analysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        analysis.setAnalyzer(cameraExecutor, this::analizarFrame);

        CameraSelector selector = CameraSelector.DEFAULT_BACK_CAMERA;
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(getViewLifecycleOwner(), selector, preview, analysis);
    }

    @SuppressLint("UnsafeOptInUsageError")
    @ExperimentalGetImage
    private void analizarFrame(@NonNull ImageProxy proxy) {
        if (yaDetectado) {
            proxy.close();
            return;
        }
        if (proxy.getImage() == null) {
            proxy.close();
            return;
        }
        InputImage image = InputImage.fromMediaImage(
                proxy.getImage(), proxy.getImageInfo().getRotationDegrees());

        Task<List<Barcode>> task = scanner.process(image);
        task.addOnSuccessListener(barcodes -> {
            for (Barcode b : barcodes) {
                String raw = b.getRawValue();
                if (raw != null && !raw.isEmpty()) {
                    yaDetectado = true;
                    if (binding != null) {
                        binding.tvInstruccionEscanear.setText("QR detectado: " + raw);
                        viewModel.unirseAGrupo(raw);
                    }
                    break;
                }
            }
        });
        task.addOnCompleteListener(t -> proxy.close());
    }

    @Override
    public void onDestroyView() {
        if (cameraExecutor != null) cameraExecutor.shutdown();
        if (scanner != null) scanner.close();
        super.onDestroyView();
        binding = null;
    }
}
