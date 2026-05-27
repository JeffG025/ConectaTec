package com.conectatec.ui.estudiante.grupos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.conectatec.R;
import com.conectatec.databinding.FragmentEstudianteGruposBinding;
import com.conectatec.ui.common.UiState;
import com.conectatec.ui.estudiante.grupos.adapter.GrupoEstudianteAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EstudianteGruposFragment extends Fragment {

    private FragmentEstudianteGruposBinding binding;
    private EstudianteGruposViewModel viewModel;
    private GrupoEstudianteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstudianteGruposBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EstudianteGruposViewModel.class);

        adapter = new GrupoEstudianteAdapter(new ArrayList<>(), grupo -> {
            android.os.Bundle args = new android.os.Bundle();
            args.putInt("grupoId", grupo.id);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_estudiante_grupos_to_detalle, args);
        });
        binding.rvGruposEstudiante.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvGruposEstudiante.setAdapter(adapter);

        binding.btnUnirseGrupo.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_estudiante_grupos_to_unirse));

        viewModel.getState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UiState.Success) {
                java.util.List<com.conectatec.data.model.GrupoEstudiante> data =
                        ((UiState.Success<java.util.List<com.conectatec.data.model.GrupoEstudiante>>) state).data;
                adapter.setLista(data);
                boolean vacio = data.isEmpty();
                binding.rvGruposEstudiante.setVisibility(vacio ? View.GONE : View.VISIBLE);
                binding.tvSinGruposEstudiante.setVisibility(vacio ? View.VISIBLE : View.GONE);
            } else if (state instanceof UiState.Error) {
                binding.rvGruposEstudiante.setVisibility(View.GONE);
                binding.tvSinGruposEstudiante.setVisibility(View.VISIBLE);
                String msg = ((UiState.Error<?>) state).mensaje;
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.cargarGrupos();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
