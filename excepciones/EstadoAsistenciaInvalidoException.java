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
 * Excepción lanzada cuando el estado ingresado en una asistencia
 * no coincide con los valores válidos permitidos por el sistema.
 *
 * Valores válidos esperados: "Presente", "Ausente", "Retirado".
 */
public class EstadoAsistenciaInvalidoException extends Exception {

    /**
     * Constructor que genera un mensaje detallado con el estado inválido.
     *
     * @param estadoIngresado el estado que fue ingresado y no es válido
     */
    public EstadoAsistenciaInvalidoException(String estadoIngresado) {
        super("Estado de asistencia inválido: '" + estadoIngresado +
              "'. Se esperaba uno de los siguientes valores: Presente, Ausente, Retirado.");
    }
}
