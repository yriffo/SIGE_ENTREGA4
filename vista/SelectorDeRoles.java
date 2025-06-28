package vista;

import modelo.Usuario;
import controlador.ControladorUsuario;

import javax.swing.*;
import java.awt.*;

/**
 * Selector de roles cuando un usuario tiene múltiples perfiles asignados.
 * Permite al usuario elegir con qué rol desea operar en el sistema.
 * Esta ventana se abre desde el login si el usuario tiene más de un rol.
 * 
 * @author Yonatan
 */
public class SelectorDeRoles extends JFrame {

    private final Usuario usuario;

    /**
     * Constructor principal del selector.
     *
     * @param usuario Usuario logueado con múltiples roles asignados
     */
    public SelectorDeRoles(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Seleccionar Rol");
        setSize(500, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ========== PANEL PRINCIPAL ==========

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        
        // Mensaje de bienvenida personalizado
        String saludo = "Bienvenido/a a SIGE, " + usuario.getNombre() + " " + usuario.getApellido() ;
        JLabel labelSaludo = new JLabel(saludo);
        labelSaludo.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelSaludo.setFont(new Font("Arial", Font.PLAIN, 18));
        labelSaludo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(labelSaludo);
        
        // Instrucción de selección de rol
        JLabel label = new JLabel("Seleccione el rol con el que desea operar:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 17));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panel.add(label);

        JComboBox<String> comboRoles = new JComboBox<>();
        comboRoles.setFont(new Font("Arial", Font.PLAIN, 15));
        for (String rol : usuario.getRoles()) {
            comboRoles.addItem(rol);
        }
        comboRoles.setMaximumSize(new Dimension(300, 35));
        comboRoles.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(comboRoles);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnConfirmar = new JButton("Continuar");
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 18));
        btnConfirmar.setPreferredSize(new Dimension(150, 40));
        btnConfirmar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Acción del botón
        btnConfirmar.addActionListener(e -> {
            String rolSeleccionado = (String) comboRoles.getSelectedItem();
            if (rolSeleccionado != null) {
                usuario.setRolActivo(rolSeleccionado);
                dispose(); // Cierra esta ventana
                ControladorUsuario.redirigirMenuPorRol(usuario);
            }
        });

        // Permitir continuar con Enter
        getRootPane().setDefaultButton(btnConfirmar);

        panel.add(btnConfirmar);
        add(panel);
    }
}
