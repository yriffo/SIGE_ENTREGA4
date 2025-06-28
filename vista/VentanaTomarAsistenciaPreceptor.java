package vista;

import modelo.*;
import controlador.ControladorAsistencia;
import controlador.ControladorConsulta;
import excepciones.FechaInvalidaException;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;

public class VentanaTomarAsistenciaPreceptor extends JFrame {

    private final Usuario usuario;

    private JComboBox<String> comboCurso;
    private JPanel panelEstudiantes;
    private JSpinner spinnerFecha;

    private List<Curso> cursos;
    private List<Estudiante> estudiantesCurso;
    private Map<Integer, JComboBox<String>> combosEstado;

    private final String[] estados = {
        "Presente", "Ausente", "Ausente Justificado", "Retirado"
    };

    private final ControladorConsulta controladorConsulta;
    private final ControladorAsistencia controladorAsistencia;

    /**
     * Constructor que inicializa la ventana para tomar asistencia.
     * Se configura toda la interfaz y se cargan los cursos disponibles.
     */
    public VentanaTomarAsistenciaPreceptor(Usuario usuario) {
        this.usuario = usuario;
        this.controladorConsulta = new ControladorConsulta();
        this.controladorAsistencia = new ControladorAsistencia();

        setTitle("Tomar Asistencia - Preceptor");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // ventana maximizada
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        combosEstado = new HashMap<>();

        // Panel de título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(30, 80, 10, 80));

        JLabel lblTitulo = new JLabel("Registro de Asistencia");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Seleccione la fecha, el curso y luego el estado de cada estudiante.");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(5));
        panelTitulo.add(lblSub);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel de selección de fecha y curso
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Datos de asistencia"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(new Font("Arial", Font.PLAIN, 16));
        spinnerFecha = new JSpinner(new SpinnerDateModel());
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy"));
        spinnerFecha.setFont(new Font("Arial", Font.PLAIN, 16));
        spinnerFecha.setPreferredSize(new Dimension(150, 28));

        JLabel lblCurso = new JLabel("Curso:");
        lblCurso.setFont(new Font("Arial", Font.PLAIN, 16));
        comboCurso = new JComboBox<>();
        comboCurso.setFont(new Font("Arial", Font.PLAIN, 16));
        comboCurso.setPreferredSize(new Dimension(300, 28));
        cursos = controladorConsulta.obtenerCursos();
        for (Curso c : cursos) {
            comboCurso.addItem(c.getNombre());
        }
        comboCurso.addActionListener(e -> cargarEstudiantes());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelSuperior.add(lblFecha, gbc);
        gbc.gridx = 1;
        panelSuperior.add(spinnerFecha, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelSuperior.add(lblCurso, gbc);
        gbc.gridx = 1;
        panelSuperior.add(comboCurso, gbc);

        add(panelSuperior, BorderLayout.BEFORE_FIRST_LINE);

        // Panel con los estudiantes del curso
        panelEstudiantes = new JPanel();
        panelEstudiantes.setLayout(new BoxLayout(panelEstudiantes, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(panelEstudiantes);
        scroll.setBorder(BorderFactory.createTitledBorder("Estudiantes del curso"));
        add(scroll, BorderLayout.CENTER);

        // Panel inferior con botones de acción
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));

        JButton btnGuardar = new JButton("Registrar asistencia");
        btnGuardar.setFont(new Font("Arial", Font.PLAIN, 16));
        btnGuardar.addActionListener(e -> registrarAsistencia());
        panelInferior.add(btnGuardar);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 16));
        btnVolver.addActionListener(e -> {
            dispose();
            new VentanaPreceptor(usuario, "Preceptor").setVisible(true);
        });
        panelInferior.add(btnVolver);

        add(panelInferior, BorderLayout.SOUTH);

        // Si hay cursos, se carga el primero por defecto
        if (!cursos.isEmpty()) {
            comboCurso.setSelectedIndex(0);
            cargarEstudiantes();
        }

        setVisible(true);
    }

    /**
     * Carga los estudiantes del curso seleccionado y prepara el panel con sus estados de asistencia.
     */
    private void cargarEstudiantes() {
        panelEstudiantes.removeAll();
        combosEstado.clear();

        int index = comboCurso.getSelectedIndex();
        if (index >= 0) {
            Curso curso = cursos.get(index);
            estudiantesCurso = controladorConsulta.obtenerEstudiantesPorCurso(curso.getIdCurso());

            for (Estudiante est : estudiantesCurso) {
                JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
                fila.setMaximumSize(new Dimension(1000, 40));

                JLabel lblNombre = new JLabel(est.getApellido() + ", " + est.getNombre());
                lblNombre.setFont(new Font("Arial", Font.PLAIN, 15));
                lblNombre.setPreferredSize(new Dimension(350, 25));

                JComboBox<String> comboEstado = new JComboBox<>(estados);
                comboEstado.setFont(new Font("Arial", Font.PLAIN, 15));
                comboEstado.setSelectedIndex(0);

                fila.add(lblNombre);
                fila.add(comboEstado);

                panelEstudiantes.add(fila);
                combosEstado.put(est.getIdEstudiante(), comboEstado);
            }

            panelEstudiantes.revalidate();
            panelEstudiantes.repaint();
        }
    }

    /**
     * Toma los datos seleccionados y registra la asistencia a través del controlador.
     */
    private void registrarAsistencia() {
        int cursoIndex = comboCurso.getSelectedIndex();
        if (cursoIndex < 0 || estudiantesCurso == null || estudiantesCurso.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un curso válido.");
            return;
        }

        Date fechaSeleccionada = (Date) spinnerFecha.getValue();
        LocalDate fecha = fechaSeleccionada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Curso curso = cursos.get(cursoIndex);
        Map<Integer, String> estadosPorEstudiante = new HashMap<>();

        for (Estudiante est : estudiantesCurso) {
            JComboBox<String> combo = combosEstado.get(est.getIdEstudiante());
            if (combo != null) {
                estadosPorEstudiante.put(est.getIdEstudiante(), (String) combo.getSelectedItem());
            }
        }

        try {
            controladorAsistencia.registrarAsistencia(usuario, curso.getIdCurso(), fecha, estadosPorEstudiante);
            JOptionPane.showMessageDialog(this, "Asistencia registrada correctamente.");
            dispose();
            new VentanaPreceptor(usuario, "Preceptor").setVisible(true);
        } catch (FechaInvalidaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
