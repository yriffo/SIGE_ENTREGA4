/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Yonatan
 */


/**
 * Clase que representa una asignación de materia a un curso por parte de un docente.
 * Compatible con la base de datos y con datos simulados.
 */
public class MateriaAsignada {

    private int idAsignacion;
    private int idMateria;
    private int idCurso;
    private int idDocente;

    public MateriaAsignada() {
    }

    public MateriaAsignada(int idAsignacion, int idMateria, int idCurso, int idDocente) {
        this.idAsignacion = idAsignacion;
        this.idMateria = idMateria;
        this.idCurso = idCurso;
        this.idDocente = idDocente;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    @Override
    public String toString() {
        return "Asignación: Materia " + idMateria + " - Curso " + idCurso + " - Docente " + idDocente;
    }
}
