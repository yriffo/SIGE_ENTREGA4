package vista;

import controlador.ControladorConsulta;
import controlador.ControladorCalificacion;
import modelo.Curso;
import modelo.Estudiante;
import modelo.Materia;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ventana que permite al Docente consultar las calificaciones
 * de los estudiantes en todas las actividades de una materia,
 * organizadas por curso y materia.
 * 
 * Utiliza una planilla donde:
 * - cada fila es un estudiante
 * - cada columna una actividad
 * - cada celda muestra la nota correspondiente
 * 
 * Todos los datos se obtienen desde la base real (MySQL) vía JDBC.
 * 
 * @author Yonatan
 */
public class VentanaConsultarCalificaciones extends JFrame {

    private Usuario docente;

    // Combos para seleccionar curso, materia y actividad
    private JComboBox<String> comboCursos;
    private JComboBox<String> comboMaterias;
    private JComboBox<ItemActividad> comboActividades;

    private JTextArea areaResultados;

    // Listas y mapas de datos
    private List<Curso> cursos;
    private List<Materia> materiasCurso;
    private Curso cursoSeleccionado;
    private Materia materiaSeleccionada;
    private Map<String, Integer> mapaActividades;

    // Controladores de acceso a datos
    private ControladorConsulta controladorConsulta;
    private ControladorCalificacion controladorCalificacion;

    /**
     * Clase auxiliar para representar una actividad con nombre visible e ID interno.
     */
    private static class ItemActividad {
        private String descripcion;
        private int id;

        public ItemActividad(String descripcion, int id) {
            this.descripcion = descripcion;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return descripcion;
        }
    }

    /**
     * Constructor principal. Recibe el docente autenticado.
     */
    public VentanaConsultarCalificaciones(Usuario docente) {
        this.docente = docente;
        this.controladorConsulta = new ControladorConsulta();
        this.controladorCalificacion = new ControladorCalificacion();
        this.mapaActividades = new LinkedHashMap<>();

        setTitle("Consultar Calificaciones por Actividad");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior con filtros
        JPanel panelSeleccion = new JPanel(new GridLayout(4, 2, 10, 10));
        comboCursos = new JComboBox<>();
        comboMaterias = new JComboBox<>();
        comboActividades = new JComboBox<>();
        areaResultados = new JTextArea();
        areaResultados.setEditable(false);

        // Cargar cursos del docente
        cursos = controladorConsulta.obtenerCursosPorDocente(docente.getIdUsuario());
        for (Curso c : cursos) {
            comboCursos.addItem(c.getNombre());
        }

        // Acciones al cambiar selección
        comboCursos.addActionListener(e -> cargarMaterias());
        comboMaterias.addActionListener(e -> cargarActividades());
        comboActividades.addActionListener(e -> mostrarCalificaciones());

        // Armado del panel de selección
        panelSeleccion.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelSeleccion.add(new JLabel("Curso:"));
        panelSeleccion.add(comboCursos);
        panelSeleccion.add(new JLabel("Materia:"));
        panelSeleccion.add(comboMaterias);
        panelSeleccion.add(new JLabel("Actividad:"));
        panelSeleccion.add(comboActividades);

        add(panelSeleccion, BorderLayout.NORTH);
        add(new JScrollPane(areaResultados), BorderLayout.CENTER);

        // Inicialización inicial si hay cursos disponibles
        if (!cursos.isEmpty()) {
            comboCursos.setSelectedIndex(0);
            cargarMaterias();
        }

        setVisible(true);
    }

    /**
     * Carga las materias correspondientes al curso seleccionado.
     */
    private void cargarMaterias() {
        int indexCurso = comboCursos.getSelectedIndex();
        if (indexCurso < 0 || indexCurso >= cursos.size()) return;

        cursoSeleccionado = cursos.get(indexCurso);
        comboMaterias.removeAllItems();

        materiasCurso = controladorConsulta.obtenerMateriasPorDocenteYCurso(
                docente.getIdUsuario(), cursoSeleccionado.getIdCurso());

        for (Materia m : materiasCurso) {
            comboMaterias.addItem(m.getNombre());
        }

        if (!materiasCurso.isEmpty()) {
            comboMaterias.setSelectedIndex(0);
            cargarActividades();
        } else {
            areaResultados.setText("No hay materias asignadas para este curso.");
        }
    }

    /**
     * Carga todas las actividades del docente para el curso y materia seleccionados.
     */
    private void cargarActividades() {
        comboActividades.removeAllItems();
        mapaActividades.clear();

        int indexMateria = comboMaterias.getSelectedIndex();
        if (indexMateria < 0 || indexMateria >= materiasCurso.size()) return;

        materiaSeleccionada = materiasCurso.get(indexMateria);

        mapaActividades = controladorCalificacion.obtenerActividadesPorDocenteYMateria(
                docente.getIdUsuario(),
                cursoSeleccionado.getIdCurso(),
                materiaSeleccionada.getIdMateria()
        );

        for (Map.Entry<String, Integer> entry : mapaActividades.entrySet()) {
            comboActividades.addItem(new ItemActividad(entry.getKey(), entry.getValue()));
        }

        if (mapaActividades.isEmpty()) {
            areaResultados.setText("No hay actividades registradas.");
        }
    }

    /**
     * Muestra la planilla de calificaciones por actividad,
     * donde cada fila es un estudiante y cada columna una actividad.
     */
    private void mostrarCalificaciones() {
        if (comboCursos.getSelectedIndex() < 0 || comboMaterias.getSelectedIndex() < 0) {
            areaResultados.setText("Seleccione curso y materia.");
            return;
        }

        List<Estudiante> estudiantes = controladorConsulta.obtenerEstudiantesPorCurso(
                cursoSeleccionado.getIdCurso());

        if (estudiantes.isEmpty()) {
            areaResultados.setText("No hay estudiantes registrados en este curso.");
            return;
        }

        if (mapaActividades.isEmpty()) {
            areaResultados.setText("No hay actividades registradas para esta materia.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Curso: ").append(cursoSeleccionado.getNombre()).append("\n");
        sb.append("Materia: ").append(materiaSeleccionada.getNombre()).append("\n\n");

        // Cabecera de actividades
        sb.append(String.format("%-25s", "Estudiante"));
        for (String act : mapaActividades.keySet()) {
            sb.append(String.format(" | %-30s", act));
        }
        sb.append("\n").append("-".repeat(30 * (mapaActividades.size() + 1))).append("\n");

        // Fila por estudiante
        for (Estudiante est : estudiantes) {
            sb.append(String.format("%-25s", est.getApellido() + ", " + est.getNombre()));
            for (String act : mapaActividades.keySet()) {
                int idActividad = mapaActividades.get(act);
                String nota = controladorCalificacion.obtenerNotaPorActividad(est.getIdEstudiante(), idActividad);
                sb.append(String.format(" | %-30s", nota));
            }
            sb.append("\n");
        }

        areaResultados.setText(sb.toString());
    }
}
