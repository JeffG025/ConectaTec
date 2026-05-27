package com.conectatec.data.model;

/** Representación de un grupo desde el punto de vista del alumno. */
public class GrupoEstudiante {
    public final int id;
    public final String nombre;
    public final String materia;
    public final String docente;
    public final String inicialesDocente;
    public final int totalAlumnos;
    public final String fechaUnion;

    public GrupoEstudiante(int id, String nombre, String materia, String docente,
                           String inicialesDocente, int totalAlumnos, String fechaUnion) {
        this.id = id;
        this.nombre = nombre;
        this.materia = materia;
        this.docente = docente;
        this.inicialesDocente = inicialesDocente;
        this.totalAlumnos = totalAlumnos;
        this.fechaUnion = fechaUnion;
    }
}
