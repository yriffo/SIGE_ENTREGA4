// SIGEAppSwing.java
package gestionSIGE;

import modelo.Usuario;
import vista.VentanaLogin;

import javax.swing.*;

/**
 * Clase principal del sistema SIGE.
 * Inicia el sistema y mantiene la sesión activa del usuario.
 * 
 * @author Yonatan
 */
public class SIGEAppSwing {

    private static Usuario usuarioActual;

    public static void main(String[] args) {
       
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo aplicar el estilo visual.");
        }

        SwingUtilities.invokeLater(() -> mostrarLogin());
    }

    public static void mostrarLogin() {
        usuarioActual = null;
        JFrame login = new VentanaLogin();
        login.setVisible(true);
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
}
