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
import com.conectatec.data.model.GrupoEstudiante;
import com.conectatec.data.model.TareaEstudiante;
import com.conectatec.data.repository.EstudianteRepository;
import com.conectatec.databinding.FragmentEstudianteGrupoDetalleBinding;
import com.conectatec.ui.estudiante.tareas.adapter.TareaEstudianteAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EstudianteGrupoDetalleFragment extends Fragment {

    private FragmentEstudianteGrupoDetalleBinding binding;
    private TareaEstudianteAdapter adapter;
    private static final Executor BG = Executors.newSingleThreadExecutor();
    private int grupoId;

    @Inject EstudianteRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstudianteGrupoDetalleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        grupoId = (getArguments() != null) ? getArguments().getInt("grupoId", 0) : 0;

        binding.btnVolverGrupoDetEst.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        adapter = new TareaEstudianteAdapter(new ArrayList<>(), tarea -> {
            Bundle args = new Bundle();
            args.putInt("tareaId", tarea.id);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_estudiante_grupo_detalle_to_tarea_detalle, args);
        });
        binding.rvTareasGrupoDetEst.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTareasGrupoDetEst.setAdapter(adapter);

        cargar();
    }

    private void cargar() {
        BG.execute(() -> {
            try {
                final GrupoEstudiante grupo = repository.getGrupoDetalle(grupoId);
                final List<TareaEstudiante> tareas = repository.getTareasPorGrupo(grupoId);

                if (getView() == null) return;
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    binding.tvNombreGrupoDetEst.setText(grupo.nombre);
                    binding.tvMateriaGrupoDetEst.setText(grupo.materia);
                    binding.tvDocenteGrupoDetEst.setText("Prof. " + grupo.docente);
                    binding.tvFechaUnionDetEst.setText(grupo.fechaUnion);
                    binding.tvAlumnosGrupoDetEst.setText(grupo.totalAlumnos + " inscritos");

                    adapter.setLista(tareas);
                    boolean vacio = tareas.isEmpty();
                    binding.rvTareasGrupoDetEst.setVisibility(vacio ? View.GONE : View.VISIBLE);
                    binding.tvSinTareasGrupoDetEst.setVisibility(vacio ? View.VISIBLE : View.GONE);
                });
            } catch (Exception e) {
                if (getView() == null) return;
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    Snackbar.make(binding.getRoot(),
                            e.getMessage() != null ? e.getMessage() : "Error al cargar el grupo",
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
