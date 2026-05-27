package com.conectatec.ui.estudiante.tareas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.conectatec.data.model.TareaEstudiante;
import com.conectatec.data.repository.EstudianteRepository;
import com.conectatec.ui.common.UiState;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EstudianteTareasViewModel extends ViewModel {

    private final EstudianteRepository repository;
    private final MutableLiveData<UiState<List<TareaEstudiante>>> state = new MutableLiveData<>();
    private static final Executor BG = Executors.newSingleThreadExecutor();

    @Inject
    public EstudianteTareasViewModel(EstudianteRepository repository) {
        this.repository = repository;
    }

    public LiveData<UiState<List<TareaEstudiante>>> getState() { return state; }

    public void cargarTareas() {
        if (state.getValue() instanceof UiState.Loading) return;
        state.setValue(UiState.Loading.get());
        BG.execute(() -> {
            try {
                List<TareaEstudiante> tareas = repository.getTareas();
                state.postValue(new UiState.Success<>(tareas));
            } catch (Exception e) {
                state.postValue(new UiState.Error<>(e.getMessage() != null
                        ? e.getMessage() : "Error al cargar tareas"));
            }
        });
    }
}
