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
 * Clase que representa una actividad evaluativa de una materia
 * en un curso determinado del sistema SIGE.
 */
public class Actividad {

    private int idActividad;
    private String titulo;
    private String tipo; // "Numérica" o "Conceptual"
    private LocalDate fecha;
    private int idCurso;
    private int idMateria;
    

    /**
     * Constructor vacío.
     */
    public Actividad() {
    }

    
    /**
     * Constructor completo con todos los atributos.
     * @param idActividad identificador único de la actividad
     * @param titulo título o nombre de la actividad (por ejemplo, "Prueba Unidad 2")
     * @param tipo tipo de calificación: "Numérica" o "Conceptual"
     * @param fecha fecha en que se realizará la actividad
     * @param idCurso ID del curso al que pertenece
     * @param idMateria ID de la materia a la que pertenece
     */
    public Actividad(int idActividad, String titulo, String tipo, LocalDate fecha, int idCurso, int idMateria) {
        this.idActividad = idActividad;
        this.titulo = titulo;
        this.tipo = tipo;
        this.fecha = fecha;
        this.idCurso = idCurso;
        this.idMateria = idMateria;
    }

    // Getters y setters

    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
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

    /**
     * Muestra la actividad.
     */
    @Override
    public String toString() {
        return titulo + " (" + tipo + ") - " + fecha;
    }
}
