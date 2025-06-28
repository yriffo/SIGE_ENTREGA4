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
 * Subclase del modelo Usuario que representa al rol Docente en el sistema SIGE.
 * Hereda los atributos comunes y puede incorporar lógica o métodos específicos del docente.
 */
public class Docente extends Usuario {

    /**
     * Constructor con todos los atributos del docente.
     * Llama al constructor de Usuario para inicializar los campos comunes.
     */
    public Docente(int idUsuario, String dni, String nombre, String apellido,
                   String telefono, String email, String contrasenia) {
        super(idUsuario, dni, nombre, apellido, telefono, email, contrasenia);
    }

    /**
     * Implementación del método abstracto de Usuario.
     * @return el nombre del rol: "Docente"
     */
    @Override
    public String getRol() {
        return "Docente";
    }

    /**
     * Representación legible del docente para consola o logs.
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
