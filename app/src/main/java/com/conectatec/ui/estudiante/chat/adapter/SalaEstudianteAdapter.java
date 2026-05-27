package com.conectatec.ui.estudiante.chat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.conectatec.R;
import com.conectatec.data.model.Sala;

import java.util.ArrayList;
import java.util.List;

public class SalaEstudianteAdapter
        extends RecyclerView.Adapter<SalaEstudianteAdapter.SalaViewHolder> {

    public interface OnSalaClickListener {
        void onSalaClick(Sala sala);
    }

    private final List<Sala> dataset = new ArrayList<>();
    private final List<Sala> visible = new ArrayList<>();
    private final OnSalaClickListener listener;
    @Nullable private String filtroTipo;

    public SalaEstudianteAdapter(OnSalaClickListener listener) {
        this.listener = listener;
    }

    public void setLista(List<Sala> salas) {
        dataset.clear();
        dataset.addAll(salas);
        aplicarFiltro();
    }

    public void setFiltroTipo(@Nullable String tipoOrNull) {
        this.filtroTipo = tipoOrNull;
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        visible.clear();
        if (filtroTipo == null) {
            visible.addAll(dataset);
        } else {
            for (Sala s : dataset) {
                if (filtroTipo.equals(s.tipo)) visible.add(s);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SalaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sala_chat_docente, parent, false);
        return new SalaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SalaViewHolder h, int position) {
        Sala s = visible.get(position);
        h.tvAvatar.setText(s.avatarIniciales);
        h.tvNombre.setText(s.nombre);
        h.tvUltimoMensaje.setText(s.ultimoMensaje);
        h.tvHora.setText(s.fechaUltimo);
        if (s.noLeidos > 0) {
            h.tvBadge.setVisibility(View.VISIBLE);
            h.tvBadge.setText(String.valueOf(s.noLeidos));
        } else {
            h.tvBadge.setVisibility(View.GONE);
        }
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSalaClick(s);
        });
    }

    @Override
    public int getItemCount() {
        return visible.size();
    }

    static class SalaViewHolder extends RecyclerView.ViewHolder {
        final TextView tvAvatar;
        final TextView tvNombre;
        final TextView tvUltimoMensaje;
        final TextView tvHora;
        final TextView tvBadge;

        SalaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar        = itemView.findViewById(R.id.tvAvatarSalaChat);
            tvNombre        = itemView.findViewById(R.id.tvNombreSalaChat);
            tvUltimoMensaje = itemView.findViewById(R.id.tvUltimoMensajeSalaChat);
            tvHora          = itemView.findViewById(R.id.tvHoraSalaChat);
            tvBadge         = itemView.findViewById(R.id.tvBadgeNoLeidosSalaChat);
        }
    }
}
