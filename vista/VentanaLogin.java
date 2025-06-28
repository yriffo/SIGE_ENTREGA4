package vista;

import modelo.Usuario;
import controlador.ControladorUsuario;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de inicio de sesión del sistema SIGE.
 * Se presenta en pantalla completa y permite iniciar sesión
 * presionando Enter o haciendo clic en el botón.
 * Valida usuario y contraseña mediante la base de datos.
 * Redirige automáticamente según la cantidad de roles.
 * 
 * @author Yonatan
 */
public class VentanaLogin extends JFrame {

    private JTextField campoEmail;
    private JPasswordField campoContrasenia;

    /**
     * Constructor que arma la interfaz de login con diseño central y responsivo.
     */
    public VentanaLogin() {
        setTitle("SIGE - Inicio de Sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        setLayout(new BorderLayout());

        // ================= PANEL CENTRAL =================
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(100, 300, 100, 300)); // Margen visual

        JLabel titulo = new JLabel("Bienvenido al Sistema SIGE");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(new Font("Arial", Font.BOLD, 36));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        panelPrincipal.add(titulo);

        // ========== PANEL DE CAMPOS ==========

        JPanel panelCampos = new JPanel(new GridLayout(2, 2, 20, 20));
        panelCampos.setMaximumSize(new Dimension(600, 120));

        JLabel labelEmail = new JLabel("Email:");
        labelEmail.setFont(new Font("Arial", Font.PLAIN, 20));
        campoEmail = new JTextField();
        campoEmail.setFont(new Font("Arial", Font.PLAIN, 20));

        JLabel labelContrasenia = new JLabel("Contraseña:");
        labelContrasenia.setFont(new Font("Arial", Font.PLAIN, 20));
        campoContrasenia = new JPasswordField();
        campoContrasenia.setFont(new Font("Arial", Font.PLAIN, 20));

        panelCampos.add(labelEmail);
        panelCampos.add(campoEmail);
        panelCampos.add(labelContrasenia);
        panelCampos.add(campoContrasenia);

        panelPrincipal.add(panelCampos);

        // ========== BOTÓN DE INGRESO ==========
        JButton botonIngresar = new JButton("Ingresar");
        botonIngresar.setFont(new Font("Arial", Font.BOLD, 22));
        botonIngresar.setPreferredSize(new Dimension(200, 50));
        botonIngresar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonIngresar.addActionListener(e -> autenticar());
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 40)));
        panelPrincipal.add(botonIngresar);

        // 🔑 Permite iniciar sesión con tecla Enter
        getRootPane().setDefaultButton(botonIngresar);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    /**
     * Valida el email y contraseña ingresados, y redirige según los roles del usuario.
     */
    private void autenticar() {
        String email = campoEmail.getText().trim();
        String contrasenia = new String(campoContrasenia.getPassword()).trim();

        if (email.isEmpty() || contrasenia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete ambos campos.",
                    "Campos obligatorios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = ControladorUsuario.autenticarUsuario(email, contrasenia);

        if (usuario != null) {
            dispose(); // Cierra la ventana de login

            if (usuario.getRoles().size() == 1) {
                // Redirige automáticamente si tiene un solo rol
                ControladorUsuario.redirigirSegunRoles(usuario);
            } else {
                // Si tiene varios roles, muestra selector
                new SelectorDeRoles(usuario).setVisible(true);
            }

        } else {
            JOptionPane.showMessageDialog(this,
                    "Email o contraseña incorrectos.",
                    "Error de autenticación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
