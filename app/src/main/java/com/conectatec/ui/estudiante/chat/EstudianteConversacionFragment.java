package com.conectatec.ui.estudiante.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.conectatec.data.model.Mensaje;
import com.conectatec.data.repository.EstudianteRepository;
import com.conectatec.databinding.FragmentEstudianteConversacionBinding;
import com.conectatec.ui.docente.chat.adapter.MensajeDocenteAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EstudianteConversacionFragment extends Fragment {

    private FragmentEstudianteConversacionBinding binding;
    private MensajeDocenteAdapter adapter;
    private static final Executor BG = Executors.newSingleThreadExecutor();
    private int salaId;
    @Nullable private String nombreSala;

    @Inject EstudianteRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstudianteConversacionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        salaId = args != null ? args.getInt("salaId", 0) : 0;
        nombreSala = args != null ? args.getString("nombreSala") : null;

        if (nombreSala != null) binding.tvNombreSalaConvEst.setText(nombreSala);

        adapter = new MensajeDocenteAdapter(new ArrayList<>());
        LinearLayoutManager lm = new LinearLayoutManager(requireContext());
        lm.setStackFromEnd(true);
        binding.rvMensajesConvEst.setLayoutManager(lm);
        binding.rvMensajesConvEst.setAdapter(adapter);

        binding.btnBackConversacionEst.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        binding.btnEnviarConvEst.setOnClickListener(v -> onEnviar());

        binding.btnAdjuntarConvEst.setOnClickListener(v ->
                Snackbar.make(binding.getRoot(),
                        "Adjuntos — próximamente", Snackbar.LENGTH_SHORT).show());

        cargarMensajes();
    }

    private void cargarMensajes() {
        BG.execute(() -> {
            try {
                final List<Mensaje> mensajes = repository.getMensajes(salaId);
                if (getView() == null) return;
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    adapter = new MensajeDocenteAdapter(mensajes);
                    binding.rvMensajesConvEst.setAdapter(adapter);
                    if (adapter.getItemCount() > 0) {
                        binding.rvMensajesConvEst.scrollToPosition(adapter.getItemCount() - 1);
                    }
                });
            } catch (Exception e) {
                if (getView() == null) return;
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    Snackbar.make(binding.getRoot(),
                            e.getMessage() != null ? e.getMessage() : "Error al cargar mensajes",
                            Snackbar.LENGTH_LONG).show();
                });
            }
        });
    }

    private void onEnviar() {
        CharSequence raw = binding.etMensajeConvEst.getText();
        if (raw == null) return;
        String texto = raw.toString().trim();
        if (TextUtils.isEmpty(texto)) return;

        String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        int nuevoId = adapter.getItemCount() + 1;
        Mensaje nuevo = new Mensaje(nuevoId, texto, hora, true, "Yo", "ES", false);

        adapter.agregarMensaje(nuevo);
        binding.rvMensajesConvEst.smoothScrollToPosition(adapter.getItemCount() - 1);
        binding.etMensajeConvEst.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
