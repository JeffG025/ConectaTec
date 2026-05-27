package com.conectatec.ui.estudiante.tareas;

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
import com.conectatec.data.model.TareaEstudiante;
import com.conectatec.databinding.FragmentEstudianteTareasBinding;
import com.conectatec.ui.common.UiState;
import com.conectatec.ui.estudiante.tareas.adapter.TareaEstudianteAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EstudianteTareasFragment extends Fragment {

    private FragmentEstudianteTareasBinding binding;
    private EstudianteTareasViewModel viewModel;
    private TareaEstudianteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstudianteTareasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EstudianteTareasViewModel.class);

        adapter = new TareaEstudianteAdapter(new ArrayList<>(), tarea -> {
            android.os.Bundle args = new android.os.Bundle();
            args.putInt("tareaId", tarea.id);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_estudiante_tareas_to_detalle, args);
        });
        binding.rvTareasEstudiante.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTareasEstudiante.setAdapter(adapter);

        binding.chipGroupTareasEst.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == binding.chipTareasPendientesEst.getId()) {
                adapter.filtrarPorEstado(TareaEstudiante.EST_PENDIENTE);
            } else if (id == binding.chipTareasEntregadasEst.getId()) {
                adapter.filtrarPorEstado(TareaEstudiante.EST_ENTREGADA);
            } else if (id == binding.chipTareasCalificadasEst.getId()) {
                adapter.filtrarPorEstado(TareaEstudiante.EST_CALIFICADA);
            } else if (id == binding.chipTareasVencidasEst.getId()) {
                adapter.filtrarPorEstado(TareaEstudiante.EST_VENCIDA);
            } else {
                adapter.filtrarPorEstado(null);
            }
            actualizarEstadoVacio();
        });

        viewModel.getState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UiState.Success) {
                List<TareaEstudiante> data =
                        ((UiState.Success<List<TareaEstudiante>>) state).data;
                adapter.setLista(data);
                actualizarEstadoVacio();
            } else if (state instanceof UiState.Error) {
                String msg = ((UiState.Error<?>) state).mensaje;
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.cargarTareas();
    }

    private void actualizarEstadoVacio() {
        boolean vacio = adapter.conteo() == 0;
        binding.rvTareasEstudiante.setVisibility(vacio ? View.GONE : View.VISIBLE);
        binding.tvSinTareasEstudiante.setVisibility(vacio ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
