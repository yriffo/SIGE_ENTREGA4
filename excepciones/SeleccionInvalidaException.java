/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package excepciones;

/**
 * Excepción que se lanza cuando el usuario no realiza una selección válida
 * en combos, listas u opciones de formularios del sistema SIGE.
 * 
 * @author Yonatan
 */
public class SeleccionInvalidaException extends Exception {

    /**
     * Constructor con mensaje personalizado.
     * @param mensaje Mensaje informativo sobre la selección errónea
     */
    public SeleccionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
