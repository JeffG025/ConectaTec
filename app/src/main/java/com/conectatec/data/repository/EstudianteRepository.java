package com.conectatec.data.repository;

import com.conectatec.data.model.GrupoEstudiante;
import com.conectatec.data.model.Mensaje;
import com.conectatec.data.model.ResumenEstudiante;
import com.conectatec.data.model.Sala;
import com.conectatec.data.model.TareaEstudiante;

import java.util.List;

public interface EstudianteRepository {
    ResumenEstudiante getResumen() throws Exception;
    List<GrupoEstudiante> getGrupos() throws Exception;
    GrupoEstudiante getGrupoDetalle(int grupoId) throws Exception;
    List<TareaEstudiante> getTareas() throws Exception;
    List<TareaEstudiante> getTareasPorGrupo(int grupoId) throws Exception;
    TareaEstudiante getTareaDetalle(int tareaId) throws Exception;
    /** Devuelve true si el alumno pudo unirse al grupo con el código dado. */
    boolean unirseAGrupo(String codigoInvitacion) throws Exception;
    /** Registra una entrega de tarea (dummy). */
    void entregarTarea(int tareaId, String respuestaTexto) throws Exception;

    List<Sala> getSalas() throws Exception;
    List<Mensaje> getMensajes(int salaId) throws Exception;
}
