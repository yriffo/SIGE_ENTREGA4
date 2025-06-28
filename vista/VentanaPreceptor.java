package vista;

import controlador.ControladorAsistencia;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Ventana principal del rol Preceptor.
 * Diseño mejorado, lógica 100% respetada.
 * 
 * @author Yonatan
 */
public class VentanaPreceptor extends JFrame {

    private Usuario usuario;
    private String nombreRol;

    private ControladorAsistencia controladorAsistencia;

    public VentanaPreceptor(Usuario usuario, String nombreRol) {
        this.usuario = usuario;
        this.nombreRol = nombreRol;

        this.controladorAsistencia = new ControladorAsistencia();

        setTitle("Menú - Rol Preceptor");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inicializarComponentes();
        setVisible(true);
    }

    private void inicializarComponentes() {
        Font fuenteTitulo = new Font("Arial", Font.BOLD, 28);
        Font fuenteBoton = new Font("Arial", Font.PLAIN, 18);

        // Título superior
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(30, 100, 10, 100));

        JLabel titulo = new JLabel("Panel del Preceptor");
        titulo.setFont(fuenteTitulo);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Seleccione una funcionalidad disponible para su rol.");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        panelTitulo.add(titulo);
        panelTitulo.add(subtitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Botones principales
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 200, 20, 200));

        agregarBoton(panelBotones, "Tomar asistencia general",
                e -> controladorAsistencia.abrirVentanaTomarAsistenciaPreceptor(usuario), fuenteBoton);

        agregarBoton(panelBotones, "Ver asistencias por curso",
                e -> controladorAsistencia.abrirVentanaVerAsistenciasPorCurso(usuario), fuenteBoton);

        agregarBoton(panelBotones, "Editar asistencia",
                e -> controladorAsistencia.abrirVentanaEditarAsistencia(usuario), fuenteBoton);

        agregarBoton(panelBotones, "Ver calificaciones por curso",
                e -> new VentanaVerCalificacionesPorCursoGeneral(usuario).setVisible(true), fuenteBoton);

        agregarBoton(panelBotones, "Confirmar reuniones",
                e -> new VentanaConfirmarReunion().setVisible(true), fuenteBoton);

        agregarBoton(panelBotones, "Ver reuniones",
                e -> new VentanaVerReunionesPreceptor().setVisible(true), fuenteBoton);

        agregarBoton(panelBotones, "Ver reuniones por estudiante o curso",
                e -> new VentanaVerReunionesPorEstudiantePreceptor().setVisible(true), fuenteBoton);

        agregarBoton(panelBotones, "Ver bitácora de un estudiante",
                e -> new VentanaBitacoraEstudiante().setVisible(true), fuenteBoton);

        add(panelBotones, BorderLayout.CENTER);

        // Panel de navegación
        JPanel panelInferior = new JPanel(new FlowLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));

        JButton btnVolver = new JButton("Volver al menú general");
        btnVolver.setFont(fuenteBoton);
        btnVolver.addActionListener(e -> {
            dispose();
            new SelectorDeRoles(usuario).setVisible(true);
        });

        JButton btnCerrarSesion = new JButton("Cerrar sesión");
        btnCerrarSesion.setFont(fuenteBoton);
        btnCerrarSesion.addActionListener(e -> {
            dispose();
            new VentanaLogin().setVisible(true);
        });

        panelInferior.add(btnVolver);
        panelInferior.add(btnCerrarSesion);
        add(panelInferior, BorderLayout.SOUTH);
    }

    /**
     * Agrega un botón estilizado al panel de funcionalidades.
     *
     * @param panel Panel donde se agrega
     * @param texto Texto del botón
     * @param action Acción a ejecutar
     * @param fuente Fuente a aplicar
     */
    private void agregarBoton(JPanel panel, String texto, ActionListener action, Font fuente) {
        JButton boton = new JButton(texto);
        boton.setFont(fuente);
        boton.setMaximumSize(new Dimension(400, 40));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.addActionListener(action);
        panel.add(boton);
        panel.add(Box.createVerticalStrut(15));
    }
}
