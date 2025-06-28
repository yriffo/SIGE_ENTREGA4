package excepciones;

/**
 * Excepción que se lanza cuando un campo obligatorio no fue completado.
 * Se utiliza para validar formularios de carga y edición en el sistema.
 * 
 * @author Yonatan
 */
public class CampoVacioException extends Exception {

    /**
     * Constructor con mensaje personalizado.
     * @param mensaje Mensaje a mostrar al usuario
     */
    public CampoVacioException(String mensaje) {
        super(mensaje);
    }
}
