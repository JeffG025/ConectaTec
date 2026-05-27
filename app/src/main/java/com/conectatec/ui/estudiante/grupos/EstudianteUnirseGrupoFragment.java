package com.conectatec.ui.estudiante.grupos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.conectatec.R;
import com.conectatec.databinding.FragmentEstudianteUnirseGrupoBinding;
import com.conectatec.ui.common.UiState;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EstudianteUnirseGrupoFragment extends Fragment {

    private FragmentEstudianteUnirseGrupoBinding binding;
    private EstudianteGruposViewModel viewModel;
    private ActivityResultLauncher<String> permisoCamara;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permisoCamara = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                concedido -> {
                    if (concedido) {
                        irAEscaneo();
                    } else if (binding != null) {
                        Snackbar.make(binding.getRoot(),
                                "Necesitas conceder permiso a la cámara para escanear el QR",
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstudianteUnirseGrupoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(EstudianteGruposViewModel.class);

        binding.btnVolverUnirseEst.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        binding.btnIrAEscanearQrEst.setOnClickListener(v -> solicitarCamaraOIr());

        binding.btnConfirmarCodigoEst.setOnClickListener(v -> {
            String codigo = binding.etCodigoInvitacionEst.getText() != null
                    ? binding.etCodigoInvitacionEst.getText().toString().trim() : "";
            if (codigo.isEmpty()) {
                Snackbar.make(binding.getRoot(),
                        "Ingresa el código de invitación", Snackbar.LENGTH_SHORT).show();
                return;
            }
            viewModel.unirseAGrupo(codigo);
        });

        viewModel.getUnirseState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UiState.Loading) {
                binding.btnConfirmarCodigoEst.setEnabled(false);
            } else if (state instanceof UiState.Success) {
                binding.btnConfirmarCodigoEst.setEnabled(true);
                Snackbar.make(binding.getRoot(),
                        "Te uniste al grupo", Snackbar.LENGTH_SHORT).show();
                viewModel.cargarGrupos();
                NavHostFragment.findNavController(this).popBackStack();
            } else if (state instanceof UiState.Error) {
                binding.btnConfirmarCodigoEst.setEnabled(true);
                String msg = ((UiState.Error<?>) state).mensaje;
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void solicitarCamaraOIr() {
        int estado = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA);
        if (estado == PackageManager.PERMISSION_GRANTED) {
            irAEscaneo();
        } else {
            permisoCamara.launch(Manifest.permission.CAMERA);
        }
    }

    private void irAEscaneo() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_estudiante_unirse_to_escanear_qr);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
