package vista;

import modelo.Usuario;
import controlador.ControladorUsuario;
import gestionSIGE.SIGEAppSwing;

import javax.swing.*;
import java.awt.*;

/**
 * Interfaz gráfica principal del rol Directivo en el sistema SIGE.
 * Muestra las funcionalidades disponibles de forma visualmente clara y ordenada.
 * No se modifica la lógica, solo el diseño de presentación.
 * 
 * @author Yonatan
 */
public class VentanaDirectivo extends JFrame {

    private final Usuario usuario;

    /**
     * Constructor principal.
     *
     * @param usuario Usuario autenticado con rol Directivo
     */
    public VentanaDirectivo(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Panel del Directivo - SIGE");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        inicializarComponentes();
        setVisible(true);
    }

    /**
     * Carga todos los componentes visuales del menú del Directivo.
     */
    private void inicializarComponentes() {
        Font fuenteTitulo = new Font("Arial", Font.BOLD, 28);
        Font fuenteBoton = new Font("Arial", Font.PLAIN, 18);

        // Panel superior con saludo
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(30, 100, 10, 100));

        JLabel titulo = new JLabel("Panel del Directivo");
        titulo.setFont(fuenteTitulo);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Bienvenido, " + usuario.getNombre());
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        panelTitulo.add(titulo);
        panelTitulo.add(subtitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel central con botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 250, 20, 250));

        agregarBoton(panelBotones, "Ver Calificaciones por Curso", () -> new VentanaVerCalificacionesPorCursoGeneral(usuario).setVisible(true), fuenteBoton);
        agregarBoton(panelBotones, "Ver Asistencias por Curso", () -> new VentanaVerAsistenciasPorCurso(usuario).setVisible(true), fuenteBoton);
        agregarBoton(panelBotones, "Ver Reuniones", () -> new VentanaVerReunionesPorEstudiantePreceptor().setVisible(true), fuenteBoton);
        agregarBoton(panelBotones, "Solicitar Reunión", () -> new VentanaSolicitarReunion(usuario).setVisible(true), fuenteBoton);
        agregarBoton(panelBotones, "Ver Bitácora del Estudiante", () -> new VentanaBitacoraEstudiante().setVisible(true), fuenteBoton);

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
     * Crea un botón estilizado y lo agrega al panel correspondiente.
     *
     * @param panel Panel donde se agrega
     * @param texto Texto que mostrará el botón
     * @param accion Acción a ejecutar al presionar
     * @param fuente Fuente que se aplicará al botón
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
