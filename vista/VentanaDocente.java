package vista;

import gestionSIGE.SIGEAppSwing;
import modelo.Usuario;
import controlador.ControladorUsuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Ventana principal del rol Docente en el sistema SIGE.
 * Muestra las funcionalidades disponibles con diseño moderno,
 * botones grandes y bienvenida personalizada.
 * 
 * @author Yonatan
 */
public class VentanaDocente extends JFrame {

    private final Usuario usuario;

    /**
     * Constructor de la ventana para el rol Docente.
     * 
     * @param usuario Usuario autenticado con rol activo "Docente"
     */
    public VentanaDocente(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Menú Docente - " + usuario.getNombre() + " " + usuario.getApellido());
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ========== PANEL PRINCIPAL ==========
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 300, 50, 300));

        // ========== TÍTULO Y BIENVENIDA ==========
        JLabel saludo = new JLabel("Bienvenido/a, " + usuario.getNombre() + " " + usuario.getApellido());
        saludo.setFont(new Font("Arial", Font.BOLD, 30));
        saludo.setAlignmentX(Component.CENTER_ALIGNMENT);
        saludo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panel.add(saludo);

        JLabel subtitulo = new JLabel("Seleccione una opción:");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 22));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panel.add(subtitulo);

        // ========== BOTONES DE FUNCIONALIDAD ==========
        agregarBoton(panel, "Tomar asistencia", e -> new VentanaRegistrarAsistencia(usuario).setVisible(true));
        agregarBoton(panel, "Ver y Editar Asistencias", e -> new VentanaVerYEditarAsistenciasDocente(usuario).setVisible(true));
        agregarBoton(panel, "Registrar Actividad", e -> new VentanaRegistrarActividad(usuario).setVisible(true));
        agregarBoton(panel, "Ver Actividades", e -> new VentanaVerActividades(usuario).setVisible(true));
        agregarBoton(panel, "Calificar Actividad", e -> new VentanaCalificarActividad(usuario).setVisible(true));
        agregarBoton(panel, "Ver Calificaciones", e -> new VentanaVerCalificacionesPorCursoGeneral(usuario).setVisible(true));
        agregarBoton(panel, "Solicitar Reunión", e -> new VentanaSolicitarReunion(usuario).setVisible(true));
        agregarBoton(panel, "Registrar observación pedagógica", e -> new VentanaRegistrarObservacionDocente(usuario).setVisible(true));
        agregarBoton(panel, "Ver mis observaciones", e -> new VentanaVerMisObservacionesDocente(usuario).setVisible(true));

        
        // ========== BOTONES FINALES ==========
        if (usuario.getRoles().size() > 1) {
            agregarBoton(panel, "Volver al menú general", e -> {
                dispose();
                ControladorUsuario.redirigirSegunRoles(usuario);
            });
        }

        agregarBoton(panel, "Cerrar sesión", e -> {
            dispose();
            SIGEAppSwing.mostrarLogin();
        });

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    /**
     * Crea un botón estilizado y lo agrega al panel principal.
     * 
     * @param panel Panel donde agregar el botón
     * @param texto Texto del botón
     * @param listener Acción al hacer clic
     */
    private void agregarBoton(JPanel panel, String texto, ActionListener listener) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.PLAIN, 20));
        boton.setMaximumSize(new Dimension(400, 50));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.addActionListener(listener);
        panel.add(boton);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Espacio entre botones
    }
}
