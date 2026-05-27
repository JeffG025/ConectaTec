package com.conectatec.ui.estudiante.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.conectatec.data.model.Sala;
import com.conectatec.data.repository.EstudianteRepository;
import com.conectatec.ui.common.UiState;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EstudianteChatViewModel extends ViewModel {

    private final EstudianteRepository repository;
    private final MutableLiveData<UiState<List<Sala>>> state = new MutableLiveData<>();
    private static final Executor BG = Executors.newSingleThreadExecutor();

    @Inject
    public EstudianteChatViewModel(EstudianteRepository repository) {
        this.repository = repository;
    }

    public LiveData<UiState<List<Sala>>> getState() { return state; }

    public void cargarSalas() {
        if (state.getValue() instanceof UiState.Loading) return;
        state.setValue(UiState.Loading.get());
        BG.execute(() -> {
            try {
                List<Sala> salas = repository.getSalas();
                state.postValue(new UiState.Success<>(salas));
            } catch (Exception e) {
                state.postValue(new UiState.Error<>(e.getMessage() != null
                        ? e.getMessage() : "Error al cargar conversaciones"));
            }
        });
    }
}
