package com.conectatec.ui.estudiante.tareas.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.conectatec.R;
import com.conectatec.data.model.TareaEstudiante;
import com.conectatec.databinding.ItemTareaEstudianteBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TareaEstudianteAdapter
        extends RecyclerView.Adapter<TareaEstudianteAdapter.ViewHolder> {

    public interface OnTareaClickListener {
        void onTareaClick(TareaEstudiante tarea);
    }

    private final List<TareaEstudiante> listaOriginal;
    private List<TareaEstudiante> listaFiltrada;
    private final OnTareaClickListener listener;

    public TareaEstudianteAdapter(List<TareaEstudiante> lista, OnTareaClickListener listener) {
        this.listaOriginal = new ArrayList<>(lista);
        this.listaFiltrada = new ArrayList<>(lista);
        this.listener = listener;
    }

    public void setLista(List<TareaEstudiante> nuevaLista) {
        listaOriginal.clear();
        listaOriginal.addAll(nuevaLista);
        listaFiltrada = new ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

    /** Filtra por estado. null o "TODOS" muestra todas. */
    public void filtrarPorEstado(String estado) {
        listaFiltrada = new ArrayList<>();
        if (estado == null || "TODOS".equals(estado)) {
            listaFiltrada.addAll(listaOriginal);
        } else {
            for (TareaEstudiante t : listaOriginal) {
                if (estado.equals(t.estado)) listaFiltrada.add(t);
            }
        }
        notifyDataSetChanged();
    }

    public int conteo() { return listaFiltrada.size(); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTareaEstudianteBinding b = ItemTareaEstudianteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        TareaEstudiante t = listaFiltrada.get(position);

        h.binding.tvTipoTareaEst.setText(t.tipo);
        h.binding.tvTipoTareaEst.setBackgroundResource(drawablePorTipo(t.tipo));

        h.binding.tvEstadoTareaEst.setText(etiquetaEstado(t.estado));
        h.binding.tvEstadoTareaEst.setBackgroundResource(drawablePorEstado(t.estado));

        h.binding.tvTituloTareaEst.setText(t.titulo);
        h.binding.tvGrupoTareaEst.setText(t.nombreGrupo + " · Prof. " + t.docente);
        h.binding.tvFechaVenceTareaEst.setText("Vence: " + t.fechaVence);

        if (t.calificacion != null) {
            h.binding.tvCalifTareaEst.setVisibility(View.VISIBLE);
            h.binding.tvCalifTareaEst.setText(String.format(Locale.getDefault(),
                    "%.0f / 100", t.calificacion));
        } else {
            h.binding.tvCalifTareaEst.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTareaClick(t);
        });
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
    public int getItemCount() {
        return listaFiltrada.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemTareaEstudianteBinding binding;

        ViewHolder(ItemTareaEstudianteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
