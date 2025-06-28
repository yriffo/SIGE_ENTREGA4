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
 * Excepción lanzada cuando se ingresa una calificación que no es válida
 * según el tipo definido para la actividad (Numérica o Conceptual).
 */
public class CalificacionInvalidaException extends Exception {

    /**
     * Constructor que recibe el valor ingresado y el tipo esperado.
     *
     * @param valor calificación ingresada por el usuario
     * @param tipoEsperado tipo de actividad ("Numérica" o "Conceptual")
     */
    public CalificacionInvalidaException(String valor, String tipoEsperado) {
        super("Calificación inválida: '" + valor + "'. Se esperaba un valor del tipo: " + tipoEsperado + ".");
    }
}
