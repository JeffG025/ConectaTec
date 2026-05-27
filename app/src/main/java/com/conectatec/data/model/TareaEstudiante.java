package com.conectatec.data.model;

/**
 * Representación de una tarea desde el punto de vista del alumno.
 * Estado refleja la situación del alumno (no la de la tarea en general).
 */
public class TareaEstudiante {

    public static final String TIPO_TAREA    = "TAREA";
    public static final String TIPO_TRABAJO  = "TRABAJO";
    public static final String TIPO_EXAMEN   = "EXAMEN";
    public static final String TIPO_PROYECTO = "PROYECTO";

    /** Estados del alumno respecto a una tarea. */
    public static final String EST_PENDIENTE  = "PENDIENTE";
    public static final String EST_ENTREGADA  = "ENTREGADA";
    public static final String EST_CALIFICADA = "CALIFICADA";
    public static final String EST_VENCIDA    = "VENCIDA";

    public final int id;
    public final String titulo;
    public final String tipo;
    public final String estado;
    public final int grupoId;
    public final String nombreGrupo;
    public final String materia;
    public final String docente;
    public final int bloqueId;
    public final String nombreBloque;
    public final String fechaVence;
    /** null si no calificada. */
    public final Double calificacion;
    /** null si no calificada. */
    public final String retroalimentacion;

    public TareaEstudiante(int id, String titulo, String tipo, String estado,
                           int grupoId, String nombreGrupo, String materia, String docente,
                           int bloqueId, String nombreBloque, String fechaVence,
                           Double calificacion, String retroalimentacion) {
        this.id = id;
        this.titulo = titulo;
        this.tipo = tipo;
        this.estado = estado;
        this.grupoId = grupoId;
        this.nombreGrupo = nombreGrupo;
        this.materia = materia;
        this.docente = docente;
        this.bloqueId = bloqueId;
        this.nombreBloque = nombreBloque;
        this.fechaVence = fechaVence;
        this.calificacion = calificacion;
        this.retroalimentacion = retroalimentacion;
    }
}
