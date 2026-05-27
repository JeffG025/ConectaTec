package com.conectatec.ui.estudiante.grupos.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.conectatec.data.model.GrupoEstudiante;
import com.conectatec.databinding.ItemGrupoEstudianteBinding;

import java.util.ArrayList;
import java.util.List;

public class GrupoEstudianteAdapter
        extends RecyclerView.Adapter<GrupoEstudianteAdapter.ViewHolder> {

    public interface OnGrupoClickListener {
        void onGrupoClick(GrupoEstudiante grupo);
    }

    private final List<GrupoEstudiante> lista;
    private final OnGrupoClickListener listener;

    public GrupoEstudianteAdapter(List<GrupoEstudiante> lista, OnGrupoClickListener listener) {
        this.lista = new ArrayList<>(lista);
        this.listener = listener;
    }

    public void setLista(List<GrupoEstudiante> nuevaLista) {
        lista.clear();
        lista.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGrupoEstudianteBinding b = ItemGrupoEstudianteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        GrupoEstudiante g = lista.get(position);
        h.binding.tvInicialGrupoEst.setText(extraerIniciales(g.nombre));
        h.binding.tvNombreGrupoEst.setText(g.nombre);
        h.binding.tvMateriaGrupoEst.setText(g.materia);
        h.binding.tvDocenteGrupoEst.setText("Prof. " + g.docente);
        h.binding.tvFechaUnionEst.setText("Te uniste el " + g.fechaUnion);
        h.binding.tvBadgeAlumnosEst.setText(g.totalAlumnos + " alumnos");
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onGrupoClick(g);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    private static String extraerIniciales(String nombre) {
        String[] partes = nombre.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, partes.length); i++) {
            if (!partes[i].isEmpty()) sb.append(partes[i].charAt(0));
        }
        return sb.toString().toUpperCase();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemGrupoEstudianteBinding binding;

        ViewHolder(ItemGrupoEstudianteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
