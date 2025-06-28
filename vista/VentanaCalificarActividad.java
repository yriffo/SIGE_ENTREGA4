package vista;

import controlador.ControladorCalificacion;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ventana para que el Docente pueda calificar a sus estudiantes
 * en actividades previamente registradas.
 * Rediseñada visualmente. Lógica original intacta.
 * 
 * @author Yonatan
 */
public class VentanaCalificarActividad extends JFrame {

    private final Usuario docente;
    private final ControladorCalificacion controlador;

    private JComboBox<String> comboCurso;
    private JComboBox<String> comboMateria;
    private JComboBox<ItemActividad> comboActividad;

    private JPanel panelEstudiantes;
    private Map<String, Integer> mapaCursos;
    private Map<String, Integer> mapaMaterias;
    private Map<String, Integer> mapaActividades;
    private Map<String, Integer> mapaEstudiantes;
    private Map<Integer, JComboBox<String>> camposNotas;

    private static class ItemActividad {
        private final String descripcion;
        private final int id;

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

    public VentanaCalificarActividad(Usuario docente) {
        this.docente = docente;
        this.controlador = new ControladorCalificacion();
        this.camposNotas = new LinkedHashMap<>();

        setTitle("Calificar Actividad - SIGE");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // pantalla completa
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        inicializarComponentes();
        cargarCursos();

        comboCurso.addActionListener(e -> cargarMaterias());
        comboMateria.addActionListener(e -> cargarActividades());
        comboActividad.addActionListener(e -> cargarEstudiantes());

        setVisible(true);
    }

    private void inicializarComponentes() {
        // Panel superior: título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(30, 100, 10, 100));

        JLabel titulo = new JLabel("Calificar Actividades");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Seleccione curso, materia y actividad para calificar");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        panelTitulo.add(titulo);
        panelTitulo.add(subtitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel filtros (curso, materia, actividad)
        JPanel panelFiltros = new JPanel(new GridBagLayout());
        panelFiltros.setBorder(BorderFactory.createEmptyBorder(20, 100, 10, 100));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;

        Font fuente = new Font("Arial", Font.PLAIN, 18);

        // Curso
        panelFiltros.add(new JLabel("Curso:"), gbc);
        comboCurso = new JComboBox<>();
        comboCurso.setFont(fuente);
        comboCurso.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        panelFiltros.add(comboCurso, gbc);

        // Materia
        gbc.gridx = 0; gbc.gridy = 1;
        panelFiltros.add(new JLabel("Materia:"), gbc);
        comboMateria = new JComboBox<>();
        comboMateria.setFont(fuente);
        comboMateria.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        panelFiltros.add(comboMateria, gbc);

        // Actividad
        gbc.gridx = 0; gbc.gridy = 2;
        panelFiltros.add(new JLabel("Actividad:"), gbc);
        comboActividad = new JComboBox<>();
        comboActividad.setFont(fuente);
        comboActividad.setPreferredSize(new Dimension(400, 30));
        gbc.gridx = 1;
        panelFiltros.add(comboActividad, gbc);

        add(panelFiltros, BorderLayout.WEST);

        // Panel estudiantes
        panelEstudiantes = new JPanel();
        panelEstudiantes.setLayout(new BoxLayout(panelEstudiantes, BoxLayout.Y_AXIS));
        panelEstudiantes.setBorder(BorderFactory.createTitledBorder("Estudiantes a calificar"));

        JScrollPane scroll = new JScrollPane(panelEstudiantes);
        scroll.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        add(scroll, BorderLayout.CENTER);

        // Botón guardar
        JButton botonGuardar = new JButton("Guardar Calificaciones");
        botonGuardar.setFont(new Font("Arial", Font.BOLD, 18));
        botonGuardar.addActionListener(e -> guardarCalificaciones());

        JPanel panelBoton = new JPanel(new FlowLayout());
        panelBoton.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        panelBoton.add(botonGuardar);
        add(panelBoton, BorderLayout.SOUTH);
    }

    private void cargarCursos() {
        comboCurso.removeAllItems();
        mapaCursos = controlador.obtenerCursosPorDocente(docente.getIdUsuario());
        for (String nombre : mapaCursos.keySet()) {
            comboCurso.addItem(nombre);
        }
    }

    private void cargarMaterias() {
        String cursoSeleccionado = (String) comboCurso.getSelectedItem();
        if (cursoSeleccionado == null) return;

        int idCurso = mapaCursos.get(cursoSeleccionado);
        mapaMaterias = controlador.obtenerMateriasPorCursoYDocente(docente.getIdUsuario(), idCurso);

        comboMateria.removeAllItems();
        for (String nombre : mapaMaterias.keySet()) {
            comboMateria.addItem(nombre);
        }
    }

    private void cargarActividades() {
        comboActividad.removeAllItems();
        camposNotas.clear();
        panelEstudiantes.removeAll();

        String curso = (String) comboCurso.getSelectedItem();
        String materia = (String) comboMateria.getSelectedItem();

        if (curso != null && materia != null) {
            int idCurso = mapaCursos.get(curso);
            int idMateria = mapaMaterias.get(materia);
            mapaActividades = controlador.obtenerActividades(docente.getIdUsuario(), idCurso, idMateria);

            for (Map.Entry<String, Integer> entry : mapaActividades.entrySet()) {
                comboActividad.addItem(new ItemActividad(entry.getKey(), entry.getValue()));
            }
        }
    }

    private void cargarEstudiantes() {
        camposNotas.clear();
        panelEstudiantes.removeAll();

        String curso = (String) comboCurso.getSelectedItem();
        ItemActividad item = (ItemActividad) comboActividad.getSelectedItem();
        if (curso == null || item == null) return;

        int idCurso = mapaCursos.get(curso);
        int idActividad = item.getId();

        String tipo = controlador.obtenerTipoDeActividad(idActividad);

        String[] opciones = ("Conceptual".equalsIgnoreCase(tipo))
                ? new String[]{"Ausente", "Ausente Justificado", "NE", "Mal", "Regular", "Bien", "Muy Bien", "Excelente"}
                : new String[]{"Ausente", "Ausente Justificado", "NE", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        mapaEstudiantes = controlador.obtenerEstudiantesDelCurso(idCurso);

        for (String nombreEst : mapaEstudiantes.keySet()) {
            JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));
            fila.setMaximumSize(new Dimension(800, 35));

            JLabel lbl = new JLabel(nombreEst);
            lbl.setPreferredSize(new Dimension(350, 25));
            lbl.setFont(new Font("Arial", Font.PLAIN, 16));

            JComboBox<String> comboNota = new JComboBox<>(opciones);
            comboNota.setFont(new Font("Arial", Font.PLAIN, 16));

            fila.add(lbl);
            fila.add(comboNota);
            panelEstudiantes.add(fila);

            int idEst = mapaEstudiantes.get(nombreEst);
            camposNotas.put(idEst, comboNota);
        }

        panelEstudiantes.revalidate();
        panelEstudiantes.repaint();
    }

    private void guardarCalificaciones() {
        ItemActividad item = (ItemActividad) comboActividad.getSelectedItem();
        if (item == null) return;

        int idActividad = item.getId();

        for (Map.Entry<Integer, JComboBox<String>> entrada : camposNotas.entrySet()) {
            int idEst = entrada.getKey();
            String nota = (String) entrada.getValue().getSelectedItem();
            controlador.guardarCalificacion(idActividad, idEst, nota);
        }

        JOptionPane.showMessageDialog(this,
                "Calificaciones guardadas correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
