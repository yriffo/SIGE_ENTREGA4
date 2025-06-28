package vista;

import controlador.ControladorConsulta;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Bitácora del estudiante – Rediseñada visualmente.
 * Permite al Preceptor, Asesor o Directivo consultar datos del estudiante seleccionado,
 * incluyendo asistencias, calificaciones, reuniones y datos personales.
 *
 * Lógica original preservada. Solo se reorganizó el diseño visual.
 *
 * @author Yonatan
 */
public class VentanaBitacoraEstudiante extends JFrame {

    private JComboBox<String> comboCurso;
    private JComboBox<String> comboEstudiante;
    private List<Curso> cursos;
    private List<Estudiante> estudiantesCurso;

    private final ControladorConsulta controladorConsulta;

    public VentanaBitacoraEstudiante() {
        controladorConsulta = new ControladorConsulta();

        setTitle("Bitácora del Estudiante");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        Font fuenteTitulo = new Font("Arial", Font.BOLD, 26);
        Font fuenteTexto = new Font("Arial", Font.PLAIN, 18);
        Font fuenteBoton = new Font("Arial", Font.PLAIN, 18);

        // Panel título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));

        JLabel lblTitulo = new JLabel("Bitácora del Estudiante");
        lblTitulo.setFont(fuenteTitulo);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Seleccione un curso y un estudiante para consultar su historial");
        lblSubtitulo.setFont(fuenteTexto);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(10));
        panelTitulo.add(lblSubtitulo);

        add(panelTitulo, BorderLayout.NORTH);

        // Panel central agrupado (curso + estudiante + botones)
        JPanel panelCentral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Selector de curso
        JLabel lblCurso = new JLabel("Curso:");
        lblCurso.setFont(fuenteTexto);
        comboCurso = new JComboBox<>();
        comboCurso.setFont(fuenteTexto);
        cursos = controladorConsulta.obtenerCursos();
        for (Curso c : cursos) {
            comboCurso.addItem(c.getNombre());
        }
        comboCurso.addActionListener(e -> cargarEstudiantes());

        // Selector de estudiante
        JLabel lblEstudiante = new JLabel("Estudiante:");
        lblEstudiante.setFont(fuenteTexto);
        comboEstudiante = new JComboBox<>();
        comboEstudiante.setFont(fuenteTexto);

        // Botones
        JButton btnDatos = new JButton("Ver Datos del Estudiante");
        JButton btnAsistencias = new JButton("Ver Asistencias");
        JButton btnCalificaciones = new JButton("Ver Calificaciones");
        JButton btnReuniones = new JButton("Ver Reuniones");
        JButton btnObservaciones = new JButton("Ver Observaciones");
        


        for (JButton btn : new JButton[]{btnDatos, btnAsistencias, btnCalificaciones, btnReuniones}) {
            btn.setFont(fuenteBoton);
            btn.setPreferredSize(new Dimension(280, 40));
        }

        btnDatos.addActionListener(e -> abrirSubventana("datos"));
        btnAsistencias.addActionListener(e -> abrirSubventana("asistencias"));
        btnCalificaciones.addActionListener(e -> abrirSubventana("calificaciones"));
        btnReuniones.addActionListener(e -> abrirSubventana("reuniones"));
        btnObservaciones.setFont(fuenteBoton);
        btnObservaciones.setPreferredSize(new Dimension(280, 40));
        btnObservaciones.addActionListener(e -> abrirSubventana("observaciones"));
        
        // Agregado al panel central
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCentral.add(lblCurso, gbc);
        gbc.gridx = 1;
        panelCentral.add(comboCurso, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelCentral.add(lblEstudiante, gbc);
        gbc.gridx = 1;
        panelCentral.add(comboEstudiante, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelCentral.add(Box.createVerticalStrut(20), gbc);

        gbc.gridy++;
        panelCentral.add(btnDatos, gbc);
        gbc.gridy++;
        panelCentral.add(btnAsistencias, gbc);
        gbc.gridy++;
        panelCentral.add(btnCalificaciones, gbc);
        gbc.gridy++;
        panelCentral.add(btnReuniones, gbc);

        add(panelCentral, BorderLayout.CENTER);
        
        gbc.gridy++;
        panelCentral.add(btnObservaciones, gbc);

        
        // Carga inicial
        if (!cursos.isEmpty()) {
            comboCurso.setSelectedIndex(0);
            cargarEstudiantes();
        }

        setVisible(true);
    }

    /**
     * Carga el combo de estudiantes según el curso seleccionado.
     */
    private void cargarEstudiantes() {
        int index = comboCurso.getSelectedIndex();
        comboEstudiante.removeAllItems();

        if (index >= 0) {
            Curso curso = cursos.get(index);
            estudiantesCurso = controladorConsulta.obtenerEstudiantesPorCurso(curso.getIdCurso());

            for (Estudiante e : estudiantesCurso) {
                comboEstudiante.addItem(e.getApellido() + ", " + e.getNombre());
            }

            if (!estudiantesCurso.isEmpty()) {
                comboEstudiante.setSelectedIndex(0);
            }
        }
    }

    /**
     * Lanza la subventana correspondiente según lo seleccionado.
     */
    private void abrirSubventana(String tipo) {
        int index = comboEstudiante.getSelectedIndex();
        if (index < 0 || estudiantesCurso == null || estudiantesCurso.isEmpty()) return;

        Estudiante est = estudiantesCurso.get(index);

        switch (tipo) {
            case "datos" -> new SubventanaDatosEstudiante(est, this);
            case "asistencias" -> new SubventanaAsistenciasEstudiante(est, this);
            case "calificaciones" -> new SubventanaCalificacionesEstudiante(est, this);
            case "reuniones" -> new SubventanaReunionesEstudiante(est, this);
            case "observaciones" -> new SubventanaObservacionesEstudiante(est, this);

        }
    }
}
