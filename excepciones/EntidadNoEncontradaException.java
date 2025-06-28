/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package excepciones;

/**
 * Excepción que se lanza cuando no se encuentra una entidad esperada en la base de datos.
 * Por ejemplo: curso, estudiante, materia, actividad, etc.
 * 
 * Esta excepción ayuda a mostrar mensajes personalizados al usuario.
 * 
 * @author Yonatan
 */
public class EntidadNoEncontradaException extends Exception {

    /**
     * Constructor con mensaje personalizado.
     * @param mensaje Detalle del elemento no encontrado
     */
    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}

