package vista;

import modelo.*;
import controlador.ControladorConsulta;
import controlador.ControladorAsistencia;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Ventana para que el Docente registre asistencia por materia.
 * Incluye rediseño visual: fuentes grandes, componentes amplios y diseño en pantalla completa.
 * Conexión a la base de datos real mediante los controladores del sistema SIGE.
 * 
 * @author Yonatan
 */
public class VentanaRegistrarAsistencia extends JFrame {

    private final Usuario docente;

    // Componentes principales
    private JComboBox<Curso> comboCursos;
    private JComboBox<Materia> comboMaterias;
    private JSpinner campoFecha;
    private JPanel panelEstudiantes;

    private Curso cursoSeleccionado;
    private Materia materiaSeleccionada;

    // Mapa para guardar los combos de asistencia por estudiante
    private final Map<Estudiante, JComboBox<String>> asistenciaPorEstudiante = new HashMap<>();

    // Controlador para obtener cursos, materias, estudiantes
    private final ControladorConsulta controladorConsulta = new ControladorConsulta();

    /**
     * Constructor principal. Recibe al usuario con rol docente.
     */
    public VentanaRegistrarAsistencia(Usuario docente) {
        this.docente = docente;

        setTitle("Registro de Asistencia - Docente");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // =================== PANEL SUPERIOR (titulo + selección) ===================
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(30, 100, 10, 100));

        // Título grande
        JLabel titulo = new JLabel("Registro de Asistencia por Materia");
        titulo.setFont(new Font("Arial", Font.BOLD, 30));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelSuperior.add(titulo);

        // Subtítulo con nombre del docente
        JLabel saludo = new JLabel("Docente: " + docente.getNombre() + " " + docente.getApellido());
        saludo.setFont(new Font("Arial", Font.PLAIN, 20));
        saludo.setAlignmentX(Component.CENTER_ALIGNMENT);
        saludo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panelSuperior.add(saludo);

        // Panel de selección (fecha, curso, materia)
        JPanel panelSeleccion = new JPanel(new GridLayout(3, 2, 20, 15));
        panelSeleccion.setMaximumSize(new Dimension(700, 130));

        // ==== Campo fecha ====
        JLabel labelFecha = new JLabel("Fecha:");
        labelFecha.setFont(new Font("Arial", Font.PLAIN, 18));
        panelSeleccion.add(labelFecha);

        campoFecha = new JSpinner(new SpinnerDateModel());
        campoFecha.setValue(new Date());
        campoFecha.setEditor(new JSpinner.DateEditor(campoFecha, "dd/MM/yyyy"));
        campoFecha.setFont(new Font("Arial", Font.PLAIN, 18));
        panelSeleccion.add(campoFecha);

        // ==== Combo cursos ====
        JLabel labelCurso = new JLabel("Curso:");
        labelCurso.setFont(new Font("Arial", Font.PLAIN, 18));
        panelSeleccion.add(labelCurso);

        comboCursos = new JComboBox<>();
        comboCursos.setFont(new Font("Arial", Font.PLAIN, 18));
        comboCursos.setPreferredSize(new Dimension(300, 35));
        List<Curso> cursos = controladorConsulta.obtenerCursosPorDocente(docente.getIdUsuario());
        for (Curso c : cursos) comboCursos.addItem(c);
        panelSeleccion.add(comboCursos);

        // ==== Combo materias ====
        JLabel labelMateria = new JLabel("Materia:");
        labelMateria.setFont(new Font("Arial", Font.PLAIN, 18));
        panelSeleccion.add(labelMateria);

        comboMaterias = new JComboBox<>();
        comboMaterias.setFont(new Font("Arial", Font.PLAIN, 18));
        comboMaterias.setPreferredSize(new Dimension(300, 35));
        panelSeleccion.add(comboMaterias);

        // Listeners: carga dinámica de materias y estudiantes
        comboCursos.addActionListener(e -> cargarMaterias());
        comboMaterias.addActionListener(e -> mostrarEstudiantes());

        panelSuperior.add(panelSeleccion);
        add(panelSuperior, BorderLayout.NORTH);

        // =================== PANEL CENTRAL (estudiantes) ===================
        panelEstudiantes = new JPanel();
        panelEstudiantes.setLayout(new BoxLayout(panelEstudiantes, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(panelEstudiantes);
        scroll.setBorder(BorderFactory.createTitledBorder("Listado de estudiantes"));
        add(scroll, BorderLayout.CENTER);

        // =================== PANEL INFERIOR (botón guardar) ===================
        JButton btnGuardar = new JButton("Guardar Asistencia");
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 20));
        btnGuardar.setPreferredSize(new Dimension(250, 45));
        btnGuardar.addActionListener(e -> guardarAsistencia());

        JPanel panelBoton = new JPanel();
        panelBoton.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        panelBoton.add(btnGuardar);
        add(panelBoton, BorderLayout.SOUTH);
    }

    /**
     * Carga las materias asignadas al docente en el curso seleccionado.
     */
    private void cargarMaterias() {
        comboMaterias.removeAllItems();
        cursoSeleccionado = (Curso) comboCursos.getSelectedItem();

        if (cursoSeleccionado != null) {
            List<Materia> materias = controladorConsulta.obtenerMateriasPorDocenteYCurso(
                docente.getIdUsuario(), cursoSeleccionado.getIdCurso()
            );
            for (Materia m : materias) comboMaterias.addItem(m);
        }
    }

    /**
     * Muestra los estudiantes del curso para marcar estado de asistencia.
     */
    private void mostrarEstudiantes() {
        panelEstudiantes.removeAll();
        asistenciaPorEstudiante.clear();

        materiaSeleccionada = (Materia) comboMaterias.getSelectedItem();
        if (cursoSeleccionado == null || materiaSeleccionada == null) return;

        List<Estudiante> estudiantes = controladorConsulta.obtenerEstudiantesPorCurso(cursoSeleccionado.getIdCurso());

        for (Estudiante est : estudiantes) {
            JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));
            fila.setMaximumSize(new Dimension(800, 30));

            JLabel lblNombre = new JLabel(est.getApellido() + ", " + est.getNombre());
            lblNombre.setPreferredSize(new Dimension(350, 25));
            lblNombre.setFont(new Font("Arial", Font.PLAIN, 16));
            fila.add(lblNombre);

            JComboBox<String> comboEstado = new JComboBox<>(new String[] {
                "Presente", "Ausente", "Ausente Justificado", "Retirado"
            });
            comboEstado.setFont(new Font("Arial", Font.PLAIN, 16));
            fila.add(comboEstado);

            asistenciaPorEstudiante.put(est, comboEstado);
            panelEstudiantes.add(fila);
        }

        panelEstudiantes.revalidate();
        panelEstudiantes.repaint();
    }

    /**
     * Guarda en la base de datos la asistencia registrada.
     * Valida fecha, curso, materia y datos cargados.
     */
    private void guardarAsistencia() {
        Date fechaSeleccionada = (Date) campoFecha.getValue();
        LocalDate fecha = fechaSeleccionada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (cursoSeleccionado == null || materiaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar curso y materia.", "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (asistenciaPorEstudiante.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay estudiantes cargados.", "Sin datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<Integer, String> mapaAsistencias = new HashMap<>();
        for (Map.Entry<Estudiante, JComboBox<String>> entry : asistenciaPorEstudiante.entrySet()) {
            mapaAsistencias.put(entry.getKey().getIdEstudiante(), (String) entry.getValue().getSelectedItem());
        }

        boolean exito = ControladorAsistencia.guardarAsistenciaPorMateria(
            docente.getIdUsuario(),
            cursoSeleccionado.getIdCurso(),
            materiaSeleccionada.getIdMateria(),
            fecha,
            mapaAsistencias
        );

        if (exito) {
            JOptionPane.showMessageDialog(this, "Asistencia registrada correctamente.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Hubo un error al guardar la asistencia.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
