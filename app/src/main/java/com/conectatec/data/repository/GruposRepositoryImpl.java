package com.conectatec.data.repository;

import com.conectatec.data.model.AlumnoPerfil;
import com.conectatec.data.model.Grupo;
import com.conectatec.data.model.Miembro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GruposRepositoryImpl implements GruposRepository {

    private static final Miembro[][] MIEMBROS_POR_GRUPO = {
            {
                new Miembro(101, "Ana García",     "AG", "ana.garcia@tec.mx",     "L20010001"),
                new Miembro(102, "Luis Martínez",  "LM", "luis.martinez@tec.mx",  "L20010002"),
                new Miembro(103, "Sofía López",    "SL", "sofia.lopez@tec.mx",    "L20010003"),
                new Miembro(104, "Carlos Ramírez", "CR", "carlos.ramirez@tec.mx", "L20010004"),
                new Miembro(105, "María Torres",   "MT", "maria.torres@tec.mx",   "L20010005"),
                new Miembro(106, "Pedro Sánchez",  "PS", "pedro.sanchez@tec.mx",  "L20010006"),
            },
            {
                new Miembro(201, "Laura Mendoza",  "LM", "laura.mendoza@tec.mx",  "L20020001"),
                new Miembro(202, "Jorge Herrera",  "JH", "jorge.herrera@tec.mx",  "L20020002"),
                new Miembro(203, "Elena Castro",   "EC", "elena.castro@tec.mx",   "L20020003"),
                new Miembro(204, "Andrés Ruiz",    "AR", "andres.ruiz@tec.mx",    "L20020004"),
                new Miembro(205, "Valeria Díaz",   "VD", "valeria.diaz@tec.mx",   "L20020005"),
                new Miembro(206, "Roberto Vargas", "RV", "roberto.vargas@tec.mx", "L20020006"),
            },
            {
                new Miembro(301, "Daniela Flores", "DF", "daniela.flores@tec.mx", "L20030001"),
                new Miembro(302, "Miguel Ortega",  "MO", "miguel.ortega@tec.mx",  "L20030002"),
                new Miembro(303, "Paola Reyes",    "PR", "paola.reyes@tec.mx",    "L20030003"),
                new Miembro(304, "Iván Morales",   "IM", "ivan.morales@tec.mx",   "L20030004"),
                new Miembro(305, "Alejandra Gil",  "AG", "alejandra.gil@tec.mx",  "L20030005"),
                new Miembro(306, "Fernando Cruz",  "FC", "fernando.cruz@tec.mx",  "L20030006"),
            }
    };

    private static final String[] SEMESTRES = {
            "6° Semestre", "6° Semestre", "6° Semestre",
            "6° Semestre", "6° Semestre", "6° Semestre",
            "4° Semestre", "4° Semestre", "4° Semestre",
            "4° Semestre", "4° Semestre", "4° Semestre",
            "2° Semestre", "2° Semestre", "2° Semestre",
            "2° Semestre", "2° Semestre", "2° Semestre"
    };

    private static final String[] CARRERAS = {
            "Ing. en Sistemas Computacionales",
            "Ing. en Software",
            "Ing. en Tecnologías de la Información",
            "Ing. en Sistemas Computacionales",
            "Ing. en Software",
            "Ing. en Tecnologías de la Información"
    };

    private static final String[] FECHAS_INSCRIPCION = {
            "01/02/2026", "15/01/2026", "20/01/2026",
            "01/02/2026", "15/01/2026", "20/01/2026"
    };

    @Inject
    public GruposRepositoryImpl() {}

    @Override
    public List<Grupo> getGrupos() throws Exception {
        Thread.sleep(300);
        return grupos();
    }

    @Override
    public Grupo getGrupoDetalle(int grupoId) throws Exception {
        Thread.sleep(200);
        for (Grupo g : grupos()) {
            if (g.id == grupoId) return g;
        }
        throw new Exception("Grupo no encontrado");
    }

    @Override
    public List<Miembro> getMiembros(int grupoId) throws Exception {
        Thread.sleep(300);
        int idx = Math.max(0, Math.min(grupoId - 1, MIEMBROS_POR_GRUPO.length - 1));
        return new ArrayList<>(Arrays.asList(MIEMBROS_POR_GRUPO[idx]));
    }

    @Override
    public AlumnoPerfil getAlumnoPerfil(int alumnoId, String nombre, String iniciales,
                                         String correo, String matricula) throws Exception {
        Thread.sleep(200);
        String semestre       = SEMESTRES[alumnoId % SEMESTRES.length];
        String carrera        = CARRERAS[alumnoId % CARRERAS.length];
        String fechaInscrito  = FECHAS_INSCRIPCION[alumnoId % FECHAS_INSCRIPCION.length];
        int total      = 18;
        int entregadas = Math.min(total, 10 + (alumnoId % 8));
        int calificadas = entregadas / 2;
        int pendientes  = total - entregadas;
        return new AlumnoPerfil(alumnoId, nombre, iniciales, correo, matricula,
                semestre, carrera, fechaInscrito, total, entregadas, calificadas, pendientes);
    }
    // ── helpers ──────────────────────────────────────────────────────
    private static List<Grupo> grupos() {
        List<Grupo> lista = new ArrayList<>();
        lista.add(new Grupo(1, "Programación Móvil 6A", "Programación Móvil", 18, "01/02/2026", "TC-9X4P", true));
        lista.add(new Grupo(2, "Bases de Datos 4B",     "Bases de Datos",     16, "15/01/2026", "TC-K3M2", true));
        lista.add(new Grupo(3, "Cálculo Integral 2A",   "Cálculo Integral",   13, "20/01/2026", "TC-7Z8R", true));
        return lista;
    }
}
