package vista;

import modelo.Usuario;
import controlador.ControladorUsuario;
import gestionSIGE.SIGEAppSwing;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del rol Asesor en el sistema SIGE.
 * Muestra las funcionalidades disponibles para este perfil.
 * Esta versión mantiene lógica original pero mejora visualmente.
 * 
 * @author Yonatan
 */
public class VentanaAsesor extends JFrame {

    private final Usuario usuario;

    /**
     * Constructor principal.
     * 
     * @param usuario Usuario autenticado con rol Asesor
     */
    public VentanaAsesor(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Panel del Asesor - SIGE");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        inicializarComponentes();
        setVisible(true);
    }

    /**
     * Inicializa la estructura visual del menú.
     */
    private void inicializarComponentes() {
        Font fuenteTitulo = new Font("Arial", Font.BOLD, 28);
        Font fuenteBoton = new Font("Arial", Font.PLAIN, 18);

        // Título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(30, 100, 10, 100));

        JLabel titulo = new JLabel("Panel del Asesor");
        titulo.setFont(fuenteTitulo);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Bienvenido, " + usuario.getNombre());
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        panelTitulo.add(titulo);
        panelTitulo.add(subtitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Botones centrales
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 250, 20, 250));

       
        agregarBoton(panelBotones, "Ver planilla por curso y materia", () -> new VentanaVerCalificacionesPorCursoGeneral(usuario).setVisible(true), fuenteBoton);
        agregarBoton(panelBotones, "Ver asistencias", () -> new VentanaVerAsistenciasPorCurso(usuario).setVisible(true), fuenteBoton);
        agregarBoton(panelBotones, "Solicitar reunión", () -> new VentanaSolicitarReunion(usuario).setVisible(true), fuenteBoton);
        agregarBoton(panelBotones, "Bitácora del estudiante", () -> new VentanaBitacoraEstudiante().setVisible(true), fuenteBoton);

        if (usuario.getRoles().size() > 1) {
            agregarBoton(panelBotones, "Volver al menú general", () -> {
                dispose();
                ControladorUsuario.redirigirSegunRoles(usuario);
            }, fuenteBoton);
        }

        agregarBoton(panelBotones, "Cerrar sesión", () -> {
            dispose();
            SIGEAppSwing.mostrarLogin();
        }, fuenteBoton);

        add(panelBotones, BorderLayout.CENTER);
    }

    /**
     * Agrega un botón estilizado al panel.
     * 
     * @param panel Panel donde se agrega
     * @param texto Texto del botón
     * @param accion Acción asociada
     * @param fuente Fuente del botón
     */
    private void agregarBoton(JPanel panel, String texto, Runnable accion, Font fuente) {
        JButton boton = new JButton(texto);
        boton.setFont(fuente);
        boton.setMaximumSize(new Dimension(400, 45));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.addActionListener(e -> accion.run());
        panel.add(boton);
        panel.add(Box.createVerticalStrut(15));
    }
}
