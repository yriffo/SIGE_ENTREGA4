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
 * Clase que representa una materia dictada en un curso del sistema SIGE.
 */
public class Materia {

    private int idMateria;
    private String nombre;
    

    /**
     * Constructor vacío requerido para instancias flexibles.
     */
    public Materia() {}

    private int idCurso;
    
    /**
     * Constructor completo.
     * @param idMateria identificador único de la materia
     * @param nombre nombre de la materia (por ejemplo, "Matemática", "Informática")
     */
    public Materia(int idMateria, String nombre) {
        this.idMateria = idMateria;
        this.nombre = nombre;
        
    }

    // Getters y setters

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

        // Getter
    public int getIdCurso() {
        return idCurso;
    }

    // Setter
    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }
    /**
     * Representación legible de la materia.
     */
    @Override
    public String toString() {
        return nombre;
    }
}
