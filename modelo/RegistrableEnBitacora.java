package modelo;

/**
 * Interfaz que define el comportamiento para registrar eventos en la bitácora del estudiante.
 * Puede ser implementada por cualquier clase que necesite dejar trazabilidad de acciones,
 * como registrar una calificación, una inasistencia o una reunión.
 * 
 * Esta interfaz permite cumplir con los principios de Programación Orientada a Objetos,
 * aplicando polimorfismo .
 * 
 * @author Yonatan
 */
public interface RegistrableEnBitacora {

    /**
     * Registra un evento en la bitácora asociado a un estudiante.
     * Este método se implementa en los controladores que interactúan con la bitácora.
     * 
     * @param idEstudiante ID del estudiante afectado
     * @param descripcion  Texto descriptivo del evento a registrar
     */
    void registrarEnBitacora(int idEstudiante, String descripcion);
}
