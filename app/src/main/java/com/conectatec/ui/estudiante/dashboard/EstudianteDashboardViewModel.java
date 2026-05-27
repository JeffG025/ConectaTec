package com.conectatec.ui.estudiante.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.conectatec.data.model.ResumenEstudiante;
import com.conectatec.data.repository.EstudianteRepository;
import com.conectatec.ui.common.UiState;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EstudianteDashboardViewModel extends ViewModel {

    private final EstudianteRepository repository;
    private final MutableLiveData<UiState<ResumenEstudiante>> state = new MutableLiveData<>();
    private static final Executor BG = Executors.newSingleThreadExecutor();

    @Inject
    public EstudianteDashboardViewModel(EstudianteRepository repository) {
        this.repository = repository;
    }

    public LiveData<UiState<ResumenEstudiante>> getState() { return state; }

    public void cargarDatos() {
        if (state.getValue() instanceof UiState.Loading) return;
        state.setValue(UiState.Loading.get());
        BG.execute(() -> {
            try {
                ResumenEstudiante resumen = repository.getResumen();
                state.postValue(new UiState.Success<>(resumen));
            } catch (Exception e) {
                state.postValue(new UiState.Error<>(e.getMessage() != null
                        ? e.getMessage() : "Error al cargar el dashboard"));
            }
        });
    }
}
