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
 * Excepción lanzada cuando la fecha ingresada no tiene el formato correcto
 * o representa una fecha inválida (por ejemplo, mal tipeada).
 */
public class FechaInvalidaException extends Exception {

    /**
     * Constructor que recibe la fecha ingresada como texto.
     *
     * @param fechaIngresada valor que no pudo ser interpretado como fecha válida
     */
    public FechaInvalidaException(String fechaIngresada) {
        super("Fecha inválida: '" + fechaIngresada + "'. Asegúrese de usar el formato dd/MM/yyyy.");
    }
}
