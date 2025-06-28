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
 * Clase que representa la calificación de un estudiante
 * en una actividad determinada dentro del sistema SIGE.
 */
public class Calificacion {

    private int idCalificacion;
    private String valor; // Puede ser un número (como "8") o un concepto (como "MB", "B", "R")
    private int idEstudiante;
    private int idActividad;

    /**
     * Constructor vacío para instanciación flexible.
     */
    public Calificacion() {}

    /**
     * Constructor completo con todos los campos.
     * @param idCalificacion identificador único de la calificación
     * @param valor nota o concepto asignado (según el tipo de actividad)
     * @param idEstudiante ID del estudiante calificado
     * @param idActividad ID de la actividad correspondiente
     */
    public Calificacion(int idCalificacion, String valor, int idEstudiante, int idActividad) {
        this.idCalificacion = idCalificacion;
        this.valor = valor;
        this.idEstudiante = idEstudiante;
        this.idActividad = idActividad;
    }

    // Getters y setters

    public int getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(int idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    /**
     * Representación legible de la calificación.
     */
    @Override
    public String toString() {
        return "Estudiante " + idEstudiante + " - Actividad " + idActividad + " - Nota: " + valor;
    }
}
