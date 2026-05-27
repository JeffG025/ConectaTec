package com.conectatec.ui.estudiante.tareas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.conectatec.R;
import com.conectatec.data.model.TareaEstudiante;
import com.conectatec.data.repository.EstudianteRepository;
import com.conectatec.databinding.FragmentEstudianteEntregarTareaBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EstudianteEntregarTareaFragment extends Fragment {

    private FragmentEstudianteEntregarTareaBinding binding;
    private static final Executor BG = Executors.newSingleThreadExecutor();
    private int tareaId;
    private final List<String> adjuntos = new ArrayList<>();

    @Inject EstudianteRepository repository;

    private ActivityResultLauncher<String> selectorArchivo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Selector de archivo (cualquier tipo). Para foto en el momento se usaría TakePicture.
        selectorArchivo = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && binding != null) {
                        adjuntos.add(nombreDesdeUri(uri));
                        agregarFilaAdjunto(uri);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstudianteEntregarTareaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tareaId = (getArguments() != null) ? getArguments().getInt("tareaId", 0) : 0;

        binding.btnVolverEntregarEst.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        binding.btnAdjuntarArchivoEst.setOnClickListener(v ->
                selectorArchivo.launch("*/*"));

        binding.btnTomarFotoEst.setOnClickListener(v ->
                Snackbar.make(binding.getRoot(),
                        "Cámara para entrega — próximamente",
                        Snackbar.LENGTH_SHORT).show());

        binding.btnConfirmarEntregaEst.setOnClickListener(v -> onConfirmarEntrega());

        cargarTitulo();
    }

    private void cargarTitulo() {
        BG.execute(() -> {
            try {
                final TareaEstudiante t = repository.getTareaDetalle(tareaId);
                if (getView() == null) return;
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    binding.tvTituloEntregarEst.setText(t.titulo);
                    binding.tvFechaLimiteEntregarEst.setText("Vence: " + t.fechaVence);
                });
            } catch (Exception ignored) {
            }
        });
    }

    private void agregarFilaAdjunto(Uri uri) {
        TextView fila = new TextView(requireContext());
        fila.setText("📎  " + nombreDesdeUri(uri));
        fila.setTextSize(13f);
        fila.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnSurface));
        fila.setPadding(16, 12, 16, 12);
        fila.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSurface));
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = 8;
        fila.setLayoutParams(lp);
        binding.containerAdjuntosEst.addView(fila);
    }

    private static String nombreDesdeUri(Uri uri) {
        String s = uri.getLastPathSegment();
        return s != null ? s : "archivo";
    }

    private void onConfirmarEntrega() {
        String respuesta = binding.etRespuestaEntregarEst.getText() != null
                ? binding.etRespuestaEntregarEst.getText().toString().trim() : "";
        if (respuesta.isEmpty() && adjuntos.isEmpty()) {
            Snackbar.make(binding.getRoot(),
                    "Agrega una respuesta o un adjunto", Snackbar.LENGTH_SHORT).show();
            return;
        }

        binding.btnConfirmarEntregaEst.setEnabled(false);
        BG.execute(() -> {
            try {
                repository.entregarTarea(tareaId, respuesta);
                if (getView() == null) return;
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    Snackbar.make(binding.getRoot(),
                            "Entrega registrada", Snackbar.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).popBackStack();
                });
            } catch (Exception e) {
                if (getView() == null) return;
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    binding.btnConfirmarEntregaEst.setEnabled(true);
                    Snackbar.make(binding.getRoot(),
                            e.getMessage() != null ? e.getMessage() : "Error al entregar",
                            Snackbar.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
