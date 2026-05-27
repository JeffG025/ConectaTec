package com.conectatec.data.model;

import java.util.List;

/** Datos agregados para el dashboard del Estudiante. */
public class ResumenEstudiante {
    public final int totalGrupos;
    public final int tareasPendientes;
    public final int tareasEntregadas;
    public final int tareasCalificadas;
    public final double promedioGeneral;
    public final List<TareaEstudiante> proximasTareas;

    public ResumenEstudiante(int totalGrupos, int tareasPendientes, int tareasEntregadas,
                             int tareasCalificadas, double promedioGeneral,
                             List<TareaEstudiante> proximasTareas) {
        this.totalGrupos = totalGrupos;
        this.tareasPendientes = tareasPendientes;
        this.tareasEntregadas = tareasEntregadas;
        this.tareasCalificadas = tareasCalificadas;
        this.promedioGeneral = promedioGeneral;
        this.proximasTareas = proximasTareas;
    }
}
