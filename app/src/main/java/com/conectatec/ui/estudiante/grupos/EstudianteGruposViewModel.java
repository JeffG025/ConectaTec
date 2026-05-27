package com.conectatec.ui.estudiante.grupos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.conectatec.data.model.GrupoEstudiante;
import com.conectatec.data.repository.EstudianteRepository;
import com.conectatec.ui.common.UiState;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EstudianteGruposViewModel extends ViewModel {

    private final EstudianteRepository repository;
    private final MutableLiveData<UiState<List<GrupoEstudiante>>> state = new MutableLiveData<>();
    private final MutableLiveData<UiState<Boolean>> unirseState = new MutableLiveData<>();
    private static final Executor BG = Executors.newSingleThreadExecutor();

    @Inject
    public EstudianteGruposViewModel(EstudianteRepository repository) {
        this.repository = repository;
    }

    public LiveData<UiState<List<GrupoEstudiante>>> getState() { return state; }
    public LiveData<UiState<Boolean>> getUnirseState() { return unirseState; }

    public void cargarGrupos() {
        if (state.getValue() instanceof UiState.Loading) return;
        state.setValue(UiState.Loading.get());
        BG.execute(() -> {
            try {
                List<GrupoEstudiante> grupos = repository.getGrupos();
                state.postValue(new UiState.Success<>(grupos));
            } catch (Exception e) {
                state.postValue(new UiState.Error<>(e.getMessage() != null
                        ? e.getMessage() : "Error al cargar grupos"));
            }
        });
    }

    public void unirseAGrupo(String codigo) {
        unirseState.setValue(UiState.Loading.get());
        BG.execute(() -> {
            try {
                boolean ok = repository.unirseAGrupo(codigo);
                if (ok) {
                    unirseState.postValue(new UiState.Success<>(true));
                } else {
                    unirseState.postValue(new UiState.Error<>("Código inválido"));
                }
            } catch (Exception e) {
                unirseState.postValue(new UiState.Error<>(e.getMessage() != null
                        ? e.getMessage() : "Error al unirse al grupo"));
            }
        });
    }
}
