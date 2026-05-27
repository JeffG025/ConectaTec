package com.conectatec.data.repository;

import com.conectatec.data.model.GrupoEstudiante;
import com.conectatec.data.model.Mensaje;
import com.conectatec.data.model.ResumenEstudiante;
import com.conectatec.data.model.Sala;
import com.conectatec.data.model.TareaEstudiante;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Datos dummy para el módulo Estudiante.
 * 3 grupos · 10 tareas (4 pendientes · 2 entregadas · 3 calificadas · 1 vencida)
 * 4 salas de chat (2 grupales · 2 privadas).
 */
@Singleton
public class EstudianteRepositoryImpl implements EstudianteRepository {

    private static final List<GrupoEstudiante> GRUPOS = new ArrayList<>(Arrays.asList(
            new GrupoEstudiante(1, "Programación Móvil 6A", "Taller de Programación Móvil",
                    "Alfredo Canto", "AC", 18, "01/02/2026"),
            new GrupoEstudiante(2, "Bases de Datos 4B", "Bases de Datos",
                    "María González", "MG", 16, "15/01/2026"),
            new GrupoEstudiante(3, "Cálculo Integral 2A", "Cálculo Integral",
                    "Roberto Sánchez", "RS", 13, "20/01/2026")
    ));

    private static final List<TareaEstudiante> TAREAS = new ArrayList<>(Arrays.asList(
            // Grupo 1 — Programación Móvil
            new TareaEstudiante(1, "Proyecto Final", TareaEstudiante.TIPO_PROYECTO,
                    TareaEstudiante.EST_PENDIENTE, 1, "Programación Móvil 6A",
                    "Taller de Programación Móvil", "Alfredo Canto",
                    2, "Bloque 2", "30/05/2026", null, null),
            new TareaEstudiante(2, "Práctica 3: Fragments", TareaEstudiante.TIPO_TAREA,
                    TareaEstudiante.EST_PENDIENTE, 1, "Programación Móvil 6A",
                    "Taller de Programación Móvil", "Alfredo Canto",
                    3, "Bloque 3", "28/05/2026", null, null),
            new TareaEstudiante(3, "Examen Parcial 1", TareaEstudiante.TIPO_EXAMEN,
                    TareaEstudiante.EST_CALIFICADA, 1, "Programación Móvil 6A",
                    "Taller de Programación Móvil", "Alfredo Canto",
                    1, "Bloque 1", "28/02/2026", 95.0,
                    "Excelente trabajo, dominas los conceptos."),
            new TareaEstudiante(4, "Práctica 1: Layouts", TareaEstudiante.TIPO_TAREA,
                    TareaEstudiante.EST_ENTREGADA, 1, "Programación Móvil 6A",
                    "Taller de Programación Móvil", "Alfredo Canto",
                    1, "Bloque 1", "10/05/2026", null, null),
            new TareaEstudiante(5, "Trabajo de investigación", TareaEstudiante.TIPO_TRABAJO,
                    TareaEstudiante.EST_PENDIENTE, 1, "Programación Móvil 6A",
                    "Taller de Programación Móvil", "Alfredo Canto",
                    3, "Bloque 3", "25/05/2026", null, null),

            // Grupo 2 — Bases de Datos
            new TareaEstudiante(6, "Tarea 1: Modelo ER", TareaEstudiante.TIPO_TAREA,
                    TareaEstudiante.EST_CALIFICADA, 2, "Bases de Datos 4B",
                    "Bases de Datos", "María González",
                    1, "Bloque 1", "05/02/2026", 88.0,
                    "Muy bien estructurado el diagrama."),
            new TareaEstudiante(7, "Examen parcial 2", TareaEstudiante.TIPO_EXAMEN,
                    TareaEstudiante.EST_PENDIENTE, 2, "Bases de Datos 4B",
                    "Bases de Datos", "María González",
                    2, "Bloque 2", "12/05/2026", null, null),

            // Grupo 3 — Cálculo Integral
            new TareaEstudiante(8, "Tarea 5: Integrales", TareaEstudiante.TIPO_TAREA,
                    TareaEstudiante.EST_VENCIDA, 3, "Cálculo Integral 2A",
                    "Cálculo Integral", "Roberto Sánchez",
                    1, "Bloque 1", "20/04/2026", 0.0,
                    "No se recibió la entrega antes del plazo."),
            new TareaEstudiante(9, "Tarea 6: Series", TareaEstudiante.TIPO_TAREA,
                    TareaEstudiante.EST_ENTREGADA, 3, "Cálculo Integral 2A",
                    "Cálculo Integral", "Roberto Sánchez",
                    2, "Bloque 2", "08/05/2026", null, null),
            new TareaEstudiante(10, "Examen Parcial 1", TareaEstudiante.TIPO_EXAMEN,
                    TareaEstudiante.EST_CALIFICADA, 3, "Cálculo Integral 2A",
                    "Cálculo Integral", "Roberto Sánchez",
                    1, "Bloque 1", "15/03/2026", 75.0,
                    "Repasa el método de sustitución.")
    ));

    private static final List<Sala> SALAS = new ArrayList<>(Arrays.asList(
            new Sala(1, "Programación Móvil 6A", "GRUPO",   "PM",
                    "Prof. Canto: Recuerden la entrega del viernes", "10:32", 2),
            new Sala(2, "Bases de Datos 4B",     "GRUPO",   "BD",
                    "Ana López: ¿Hay duda sobre el ER?",            "Ayer",  0),
            new Sala(3, "Prof. Alfredo Canto",   "PRIVADO", "AC",
                    "Revisé tu entrega, todo bien",                 "Mar",   1),
            new Sala(4, "Ana López",             "PRIVADO", "AL",
                    "¿Tienes apuntes del lunes?",                   "Lun",   0)
    ));

    @Inject
    public EstudianteRepositoryImpl() {}

    @Override
    public ResumenEstudiante getResumen() throws Exception {
        Thread.sleep(300);
        int pendientes = 0, entregadas = 0, calificadas = 0;
        double sumaCalif = 0;
        int countCalif = 0;
        List<TareaEstudiante> proximas = new ArrayList<>();
        for (TareaEstudiante t : TAREAS) {
            switch (t.estado) {
                case TareaEstudiante.EST_PENDIENTE:
                    pendientes++;
                    if (proximas.size() < 3) proximas.add(t);
                    break;
                case TareaEstudiante.EST_ENTREGADA:  entregadas++;  break;
                case TareaEstudiante.EST_CALIFICADA: calificadas++;
                    if (t.calificacion != null) { sumaCalif += t.calificacion; countCalif++; }
                    break;
                case TareaEstudiante.EST_VENCIDA:
                    if (t.calificacion != null) { sumaCalif += t.calificacion; countCalif++; }
                    break;
            }
        }
        double promedio = countCalif > 0 ? sumaCalif / countCalif : 0;
        return new ResumenEstudiante(GRUPOS.size(), pendientes, entregadas, calificadas, promedio, proximas);
    }

    @Override
    public List<GrupoEstudiante> getGrupos() throws Exception {
        Thread.sleep(200);
        return new ArrayList<>(GRUPOS);
    }

    @Override
    public GrupoEstudiante getGrupoDetalle(int grupoId) throws Exception {
        Thread.sleep(150);
        for (GrupoEstudiante g : GRUPOS) {
            if (g.id == grupoId) return g;
        }
        throw new Exception("Grupo no encontrado");
    }

    @Override
    public List<TareaEstudiante> getTareas() throws Exception {
        Thread.sleep(250);
        return new ArrayList<>(TAREAS);
    }

    @Override
    public List<TareaEstudiante> getTareasPorGrupo(int grupoId) throws Exception {
        Thread.sleep(250);
        List<TareaEstudiante> result = new ArrayList<>();
        for (TareaEstudiante t : TAREAS) {
            if (t.grupoId == grupoId) result.add(t);
        }
        return result;
    }

    @Override
    public TareaEstudiante getTareaDetalle(int tareaId) throws Exception {
        Thread.sleep(150);
        for (TareaEstudiante t : TAREAS) {
            if (t.id == tareaId) return t;
        }
        throw new Exception("Tarea no encontrada");
    }

    @Override
    public boolean unirseAGrupo(String codigoInvitacion) throws Exception {
        Thread.sleep(400);
        // Dummy: cualquier código no vacío de al menos 4 caracteres se acepta
        return codigoInvitacion != null && codigoInvitacion.trim().length() >= 4;
    }

    @Override
    public void entregarTarea(int tareaId, String respuestaTexto) throws Exception {
        Thread.sleep(500);
        // Dummy: no persistimos nada, solo simulamos latencia
    }

    @Override
    public List<Sala> getSalas() throws Exception {
        Thread.sleep(250);
        return new ArrayList<>(SALAS);
    }

    @Override
    public List<Mensaje> getMensajes(int salaId) throws Exception {
        Thread.sleep(200);
        switch (salaId) {
            case 1: return new ArrayList<>(Arrays.asList(
                    new Mensaje(1, "Buenos días, profe.",              "09:01", true,  "Yo",          "ES", false),
                    new Mensaje(2, "Buenos días.",                     "09:02", false, "Prof. Canto", "AC", false),
                    new Mensaje(3, "Recuerden la entrega del viernes", "10:32", false, "Prof. Canto", "AC", false)
            ));
            case 2: return new ArrayList<>(Arrays.asList(
                    new Mensaje(1, "¿Hay duda sobre el ER?", "Ayer", false, "Ana López", "AL", false),
                    new Mensaje(2, "Yo tengo una.",          "Ayer", true,  "Yo",        "ES", false)
            ));
            case 3: return new ArrayList<>(Arrays.asList(
                    new Mensaje(1, "Profe, una consulta sobre la práctica", "Lun", true,  "Yo",          "ES", false),
                    new Mensaje(2, "Claro, dime.",                          "Lun", false, "Prof. Canto", "AC", false),
                    new Mensaje(3, "Revisé tu entrega, todo bien",          "Mar", false, "Prof. Canto", "AC", false)
            ));
            case 4: return new ArrayList<>(Arrays.asList(
                    new Mensaje(1, "Hola",                       "Lun", false, "Ana López", "AL", false),
                    new Mensaje(2, "¿Tienes apuntes del lunes?", "Lun", false, "Ana López", "AL", false)
            ));
            default: return new ArrayList<>();
        }
    }
}
