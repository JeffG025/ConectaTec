package com.conectatec.ui.estudiante.chat;

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
import com.conectatec.databinding.FragmentEstudianteChatBinding;
import com.conectatec.ui.common.UiState;
import com.conectatec.ui.estudiante.chat.adapter.SalaEstudianteAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EstudianteChatFragment extends Fragment {

    private FragmentEstudianteChatBinding binding;
    private EstudianteChatViewModel viewModel;
    private SalaEstudianteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstudianteChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EstudianteChatViewModel.class);

        adapter = new SalaEstudianteAdapter(sala -> {
            android.os.Bundle args = new android.os.Bundle();
            args.putInt("salaId", sala.id);
            args.putString("nombreSala", sala.nombre);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_estudiante_chat_to_conversacion, args);
        });
        binding.rvSalasEstudiante.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSalasEstudiante.setAdapter(adapter);

        binding.chipGroupChatEst.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == binding.chipChatGruposEst.getId()) {
                adapter.setFiltroTipo("GRUPO");
            } else if (id == binding.chipChatPrivadosEst.getId()) {
                adapter.setFiltroTipo("PRIVADO");
            } else {
                adapter.setFiltroTipo(null);
            }
        });

        viewModel.getState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UiState.Success) {
                List<com.conectatec.data.model.Sala> data =
                        ((UiState.Success<List<com.conectatec.data.model.Sala>>) state).data;
                adapter.setLista(data);
                boolean vacio = data.isEmpty();
                binding.rvSalasEstudiante.setVisibility(vacio ? View.GONE : View.VISIBLE);
                binding.tvSinSalasEstudiante.setVisibility(vacio ? View.VISIBLE : View.GONE);
            } else if (state instanceof UiState.Error) {
                String msg = ((UiState.Error<?>) state).mensaje;
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.cargarSalas();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
