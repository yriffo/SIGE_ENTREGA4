package vista;

import controlador.ControladorConsulta;
import controlador.ControladorReunion;
import modelo.Curso;
import modelo.Estudiante;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana para solicitar una reunión (Docente, Asesor o Directivo).
 * Diseño mejorado: selección de curso activa un combo de estudiantes.
 * Lógica intacta. Evita mostrar lista completa de 30+ alumnos.
 * 
 * @author Yonatan
 */
public class VentanaSolicitarReunion extends JFrame {

    private Usuario solicitante;

    private JComboBox<String> comboCurso;
    private JComboBox<String> comboEstudiante;
    private DefaultListModel<String> modeloSeleccionados;
    private JList<String> listaSeleccionados;
    private JTextArea campoMotivo;
    private JTextField campoDisponibilidad;

    private List<Curso> cursos;
    private List<Estudiante> estudiantesCurso;
    private List<Estudiante> estudiantesSeleccionados;

    private ControladorConsulta controladorConsulta;
    private ControladorReunion controladorReunion;

    public VentanaSolicitarReunion(Usuario solicitante) {
        this.solicitante = solicitante;
        controladorConsulta = new ControladorConsulta();
        controladorReunion = new ControladorReunion();

        setTitle("Solicitar Reunión");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        inicializarComponentes();
        setVisible(true);
    }

    private void inicializarComponentes() {
        Font fuente = new Font("Arial", Font.PLAIN, 18);
        estudiantesSeleccionados = new ArrayList<>();

        // ====== TÍTULO ======
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(30, 100, 10, 100));

        JLabel lblTitulo = new JLabel("Solicitud de Reunión");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Seleccione un curso, elija estudiantes y complete el motivo.");
        lblSubtitulo.setFont(fuente);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        panelTitulo.add(lblTitulo);
        panelTitulo.add(lblSubtitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // ====== SELECCIÓN CURSO Y ESTUDIANTES ======
        JPanel panelSeleccion = new JPanel();
        panelSeleccion.setLayout(new BoxLayout(panelSeleccion, BoxLayout.Y_AXIS));
        panelSeleccion.setBorder(BorderFactory.createEmptyBorder(20, 150, 20, 150));

        // Combo curso
        JPanel filaCurso = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblCurso = new JLabel("Curso:");
        lblCurso.setFont(fuente);
        comboCurso = new JComboBox<>();
        comboCurso.setFont(fuente);
        comboCurso.setPreferredSize(new Dimension(300, 30));
        cursos = controladorConsulta.obtenerCursos();
        for (Curso c : cursos) {
            comboCurso.addItem(c.getNombre());
        }
        comboCurso.addActionListener(e -> cargarEstudiantes());
        filaCurso.add(lblCurso);
        filaCurso.add(comboCurso);
        panelSeleccion.add(filaCurso);

        // Combo estudiante + botón agregar
        JPanel filaEstudiante = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblEstudiante = new JLabel("Estudiante:");
        lblEstudiante.setFont(fuente);
        comboEstudiante = new JComboBox<>();
        comboEstudiante.setFont(fuente);
        comboEstudiante.setPreferredSize(new Dimension(300, 30));

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setFont(fuente);
        btnAgregar.addActionListener(e -> agregarEstudianteSeleccionado());

        filaEstudiante.add(lblEstudiante);
        filaEstudiante.add(comboEstudiante);
        filaEstudiante.add(btnAgregar);
        panelSeleccion.add(filaEstudiante);

        // Lista de seleccionados
        modeloSeleccionados = new DefaultListModel<>();
        listaSeleccionados = new JList<>(modeloSeleccionados);
        listaSeleccionados.setFont(fuente);
        JScrollPane scrollSeleccionados = new JScrollPane(listaSeleccionados);
        scrollSeleccionados.setPreferredSize(new Dimension(500, 100));
        scrollSeleccionados.setBorder(BorderFactory.createTitledBorder("Estudiantes seleccionados"));
        panelSeleccion.add(scrollSeleccionados);

        add(panelSeleccion, BorderLayout.WEST);

        // ====== MOTIVO Y DISPONIBILIDAD ======
        JPanel panelTexto = new JPanel(new GridLayout(4, 1, 10, 10));
        panelTexto.setBorder(BorderFactory.createTitledBorder("Motivo y disponibilidad"));
        panelTexto.setPreferredSize(new Dimension(700, 200));

        campoMotivo = new JTextArea(3, 30);
        campoMotivo.setFont(fuente);
        campoMotivo.setLineWrap(true);
        campoMotivo.setWrapStyleWord(true);

        campoDisponibilidad = new JTextField();
        campoDisponibilidad.setFont(fuente);

        panelTexto.add(new JLabel("Motivo de la reunión:"));
        panelTexto.add(new JScrollPane(campoMotivo));
        panelTexto.add(new JLabel("Disponibilidad horaria (ej: Lunes 10hs o Martes 14–16):"));
        panelTexto.add(campoDisponibilidad);

        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        panelCentro.add(panelTexto);

        add(panelCentro, BorderLayout.CENTER);

        // ====== BOTÓN ENVIAR ======
        JButton btnEnviar = new JButton("Solicitar Reunión");
        btnEnviar.setFont(new Font("Arial", Font.BOLD, 18));
        btnEnviar.addActionListener(e -> solicitarReunion());

        JPanel panelBoton = new JPanel();
        panelBoton.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        panelBoton.add(btnEnviar);
        add(panelBoton, BorderLayout.SOUTH);

        if (!cursos.isEmpty()) {
            comboCurso.setSelectedIndex(0);
            cargarEstudiantes();
        }
    }

    private void cargarEstudiantes() {
        estudiantesCurso = new ArrayList<>();
        comboEstudiante.removeAllItems();
        modeloSeleccionados.clear();
        estudiantesSeleccionados.clear();

        int indexCurso = comboCurso.getSelectedIndex();
        if (indexCurso < 0 || indexCurso >= cursos.size()) return;

        Curso cursoSeleccionado = cursos.get(indexCurso);
        estudiantesCurso = controladorConsulta.obtenerEstudiantesPorCurso(cursoSeleccionado.getIdCurso());

        for (Estudiante e : estudiantesCurso) {
            comboEstudiante.addItem(e.getApellido() + ", " + e.getNombre());
        }
    }

    private void agregarEstudianteSeleccionado() {
        int index = comboEstudiante.getSelectedIndex();
        if (index >= 0 && index < estudiantesCurso.size()) {
            Estudiante nuevo = estudiantesCurso.get(index);

            // Comparar por ID para evitar duplicados
            boolean yaAgregado = estudiantesSeleccionados.stream()
                    .anyMatch(est -> est.getIdEstudiante() == nuevo.getIdEstudiante());

            if (!yaAgregado) {
                estudiantesSeleccionados.add(nuevo);
                modeloSeleccionados.addElement(nuevo.getApellido() + ", " + nuevo.getNombre());
            }
        }
    }

    private void solicitarReunion() {
        int indexCurso = comboCurso.getSelectedIndex();
        if (indexCurso < 0 || indexCurso >= cursos.size()) return;

        Curso cursoSeleccionado = cursos.get(indexCurso);
        List<Integer> idsEstudiantesSeleccionados = new ArrayList<>();

        for (Estudiante e : estudiantesSeleccionados) {
            idsEstudiantesSeleccionados.add(e.getIdEstudiante());
        }

        if (idsEstudiantesSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un estudiante.");
            return;
        }

        String motivo = campoMotivo.getText().trim();
        String disponibilidad = campoDisponibilidad.getText().trim();

        if (motivo.isEmpty() || disponibilidad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar el motivo y la disponibilidad.");
            return;
        }

        boolean exito = controladorReunion.solicitarReunion(
                solicitante.getIdUsuario(),
                cursoSeleccionado.getIdCurso(),
                idsEstudiantesSeleccionados,
                motivo,
                disponibilidad
        );

        if (exito) {
            JOptionPane.showMessageDialog(this, "Solicitud enviada correctamente.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la solicitud.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
