package com.conectatec.ui.estudiante.tareas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.conectatec.R;
import com.conectatec.data.model.TareaEstudiante;
import com.conectatec.data.repository.EstudianteRepository;
import com.conectatec.databinding.FragmentEstudianteTareaDetalleBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EstudianteTareaDetalleFragment extends Fragment {

    private FragmentEstudianteTareaDetalleBinding binding;
    private static final Executor BG = Executors.newSingleThreadExecutor();
    private int tareaId;
    @Nullable private TareaEstudiante tareaCargada;

    @Inject EstudianteRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstudianteTareaDetalleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tareaId = (getArguments() != null) ? getArguments().getInt("tareaId", 0) : 0;

        binding.btnVolverTareaDetEst.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        binding.btnEntregarTareaEst.setOnClickListener(v -> {
            if (tareaCargada == null) return;
            Bundle args = new Bundle();
            args.putInt("tareaId", tareaCargada.id);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_estudiante_tarea_detalle_to_entregar, args);
        });

        cargar();
    }

    private void cargar() {
        BG.execute(() -> {
            try {
                final TareaEstudiante t = repository.getTareaDetalle(tareaId);
                if (getView() == null) return;
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    tareaCargada = t;
                    pintar(t);
                });
            } catch (Exception e) {
                if (getView() == null) return;
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    Snackbar.make(binding.getRoot(),
                            e.getMessage() != null ? e.getMessage() : "Error al cargar la tarea",
                            Snackbar.LENGTH_LONG).show();
                });
            }
        });
    }

    private void pintar(TareaEstudiante t) {
        binding.tvTipoTareaDetEst.setText(t.tipo);
        binding.tvTipoTareaDetEst.setBackgroundResource(drawablePorTipo(t.tipo));

        binding.tvEstadoTareaDetEst.setText(etiquetaEstado(t.estado));
        binding.tvEstadoTareaDetEst.setBackgroundResource(drawablePorEstado(t.estado));

        binding.tvTituloTareaDetEst.setText(t.titulo);
        binding.tvGrupoTareaDetEst.setText(t.nombreGrupo + " · " + t.materia);
        binding.tvDocenteTareaDetEst.setText("Prof. " + t.docente);
        binding.tvBloqueTareaDetEst.setText(t.nombreBloque);
        binding.tvFechaVenceTareaDetEst.setText(t.fechaVence);

        boolean tieneCalif = t.calificacion != null;
        binding.cardCalifTareaDetEst.setVisibility(tieneCalif ? View.VISIBLE : View.GONE);
        if (tieneCalif) {
            binding.tvCalifTareaDetEst.setText(String.format(Locale.getDefault(),
                    "%.0f / 100", t.calificacion));
            binding.tvRetroTareaDetEst.setText(
                    t.retroalimentacion != null ? t.retroalimentacion : "—");
        }

        binding.btnEntregarTareaEst.setVisibility(
                TareaEstudiante.EST_PENDIENTE.equals(t.estado) ? View.VISIBLE : View.GONE);
    }

    private static int drawablePorTipo(String tipo) {
        switch (tipo) {
            case TareaEstudiante.TIPO_TAREA:    return R.drawable.bg_chip_docente;
            case TareaEstudiante.TIPO_TRABAJO:  return R.drawable.bg_chip_estudiante;
            case TareaEstudiante.TIPO_EXAMEN:   return R.drawable.bg_chip_admin;
            case TareaEstudiante.TIPO_PROYECTO: return R.drawable.bg_chip_pendiente;
            default: return R.drawable.bg_chip_docente;
        }
    }

    private static int drawablePorEstado(String estado) {
        switch (estado) {
            case TareaEstudiante.EST_PENDIENTE:  return R.drawable.bg_chip_pendiente;
            case TareaEstudiante.EST_ENTREGADA:  return R.drawable.bg_chip_docente;
            case TareaEstudiante.EST_CALIFICADA: return R.drawable.bg_chip_estudiante;
            case TareaEstudiante.EST_VENCIDA:    return R.drawable.bg_chip_admin;
            default: return R.drawable.bg_chip_pendiente;
        }
    }

    private static String etiquetaEstado(String estado) {
        switch (estado) {
            case TareaEstudiante.EST_PENDIENTE:  return "PENDIENTE";
            case TareaEstudiante.EST_ENTREGADA:  return "ENTREGADA";
            case TareaEstudiante.EST_CALIFICADA: return "CALIFICADA";
            case TareaEstudiante.EST_VENCIDA:    return "VENCIDA";
            default: return estado;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
