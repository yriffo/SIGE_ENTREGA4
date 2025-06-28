/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package excepciones;

/**
 *
 * @author Yonatan
 */


/**
 * Excepción lanzada cuando el nombre de un curso no cumple con el formato esperado.
 * Por ejemplo, se espera algo como "1A", "2B", etc.
 */
public class NombreCursoInvalidoException extends Exception {

    /**
     * Constructor que recibe el nombre ingresado.
     *
     * @param nombreIngresado el valor ingresado como nombre de curso
     */
    public NombreCursoInvalidoException(String nombreIngresado) {
        super("Nombre de curso inválido: '" + nombreIngresado + "'. Se espera un formato como '1A', '2B', etc.");
    }
}

