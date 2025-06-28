/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDate;
/**
 *
 * @author Yonatan
 */


/**
 * Clase que representa un evento registrado en la bitácora de un estudiante.
 * Estos eventos reflejan intervenciones pedagógicas, cambios de estado u observaciones,
 * y se asocian a un curso, materia, docente y fecha concreta.
 */
public class Bitacora {

    private int idBitacora;
    private int idEstudiante;
    private int idCurso;
    private int idMateria;
    private int idDocente;
    private LocalDate fecha;
    private String motivo;

    /**
     * Constructor vacío requerido por frameworks y utilidad general.
     */
    public Bitacora() {
    }

    /**
     * Constructor completo de la clase Bitacora.
     * @param idBitacora identificador único del evento
     * @param idEstudiante ID del estudiante involucrado
     * @param idCurso ID del curso correspondiente
     * @param idMateria ID de la materia correspondiente
     * @param idDocente ID del docente que registra el evento
     * @param fecha fecha del evento
     * @param motivo descripción del evento o intervención
     */
    public Bitacora(int idBitacora, int idEstudiante, int idCurso, int idMateria,
                    int idDocente, LocalDate fecha, String motivo) {
        this.idBitacora = idBitacora;
        this.idEstudiante = idEstudiante;
        this.idCurso = idCurso;
        this.idMateria = idMateria;
        this.idDocente = idDocente;
        this.fecha = fecha;
        this.motivo = motivo;
    }

    // Getters y setters

    public int getIdBitacora() {
        return idBitacora;
    }

    public void setIdBitacora(int idBitacora) {
        this.idBitacora = idBitacora;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}

