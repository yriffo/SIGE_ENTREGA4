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
 * Subclase de Usuario que representa al rol Preceptor en el sistema SIGE.
 */
public class Preceptor extends Usuario {

    public Preceptor(int idUsuario, String dni, String nombre, String apellido,
                     String telefono, String email, String contrasenia) {
        super(idUsuario, dni, nombre, apellido, telefono, email, contrasenia);
    }

    @Override
    public String getRol() {
        return "Preceptor";
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
