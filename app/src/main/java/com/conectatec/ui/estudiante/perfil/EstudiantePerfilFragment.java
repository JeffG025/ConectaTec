package com.conectatec.ui.estudiante.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.conectatec.R;
import com.conectatec.databinding.FragmentEstudiantePerfilBinding;
import com.conectatec.ui.auth.LoginActivity;
import com.conectatec.ui.common.EntradaAnimator;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EstudiantePerfilFragment extends Fragment {

    private FragmentEstudiantePerfilBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstudiantePerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        EntradaAnimator.animar(
                binding.cardHeroPerfilEstudiante,
                binding.cardInfoAcademicaEstudiante,
                binding.cardConfigEstudiante,
                binding.btnCerrarSesionEstudiante
        );
    }

    private void setupListeners() {
        binding.rowNotificacionesEstudiante.setOnClickListener(v -> { /* placeholder */ });

        binding.rowTemaEstudiante.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_estudiante_perfil_to_ajustes));

        binding.rowIdiomaEstudiante.setOnClickListener(v -> { /* placeholder */ });

        binding.btnCerrarSesionEstudiante.setOnClickListener(v -> cerrarSesion());
    }

    private void cerrarSesion() {
        // TODO: llamar a SessionService.cerrarSesion()
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().overridePendingTransition(
                android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
