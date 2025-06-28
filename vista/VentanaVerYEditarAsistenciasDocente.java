package vista;

import modelo.*;
import controlador.ControladorAsistencia;
import controlador.ControladorConsulta;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Ventana que permite al Docente ver y editar las asistencias que él mismo tomó.
 * Muestra las asistencias tomadas y permite modificar el estado de asistencia por estudiante.
 * Funciona con base de datos a través de ControladorAsistencia.
 * 
 * @author Yonatan
 */
public class VentanaVerYEditarAsistenciasDocente extends JFrame {

    private final Usuario docente;
    private JComboBox<String> comboAsistencias;
    private JPanel panelEstudiantes;
    private final Map<String, Asistencia> mapaAsistencias = new LinkedHashMap<>();
    private final Map<Integer, JComboBox<String>> combosPorEstudiante = new HashMap<>();
    private List<Asistencia> asistencias;

    public VentanaVerYEditarAsistenciasDocente(Usuario docente) {
        this.docente = docente;

        setTitle("Ver y Editar Asistencias Tomadas");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // pantalla completa
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        comboAsistencias = new JComboBox<>();
        comboAsistencias.setFont(new Font("Arial", Font.PLAIN, 18));
        comboAsistencias.addActionListener(e -> mostrarDetalle());

        panelEstudiantes = new JPanel();
        panelEstudiantes.setLayout(new BoxLayout(panelEstudiantes, BoxLayout.Y_AXIS));
        panelEstudiantes.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JScrollPane scroll = new JScrollPane(panelEstudiantes);
        scroll.setBorder(BorderFactory.createTitledBorder("Estudiantes"));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JButton botonGuardar = new JButton("Guardar Cambios");
        botonGuardar.setFont(new Font("Arial", Font.BOLD, 18));
        botonGuardar.addActionListener(e -> guardarCambios());

        // Panel superior con título y combo
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BoxLayout(panelTop, BoxLayout.Y_AXIS));
        panelTop.setBorder(BorderFactory.createEmptyBorder(30, 100, 10, 100));

        JLabel titulo = new JLabel("Ver y Editar Asistencias Tomadas");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Seleccione una asistencia registrada por usted:");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        comboAsistencias.setMaximumSize(new Dimension(600, 35));
        comboAsistencias.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelTop.add(titulo);
        panelTop.add(subtitulo);
        panelTop.add(comboAsistencias);

        // Panel inferior con botón
        JPanel panelBoton = new JPanel(new FlowLayout());
        panelBoton.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        panelBoton.add(botonGuardar);

        add(panelTop, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);

        cargarAsistencias();
        setVisible(true);
    }

    private void cargarAsistencias() {
        asistencias = new ControladorAsistencia().obtenerAsistenciasPorDocente(docente.getIdUsuario());
        comboAsistencias.removeAllItems();
        mapaAsistencias.clear();

        if (asistencias.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay asistencias registradas por este docente.", "Sin datos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ControladorConsulta consulta = new ControladorConsulta();

        for (Asistencia a : asistencias) {
            Curso curso = consulta.buscarCursoPorId(a.getIdCurso());
            Materia materia = (a.getIdMateria() > 0) ? consulta.buscarMateriaPorId(a.getIdMateria()) : null;

            String fecha = formatter.format(a.getFecha());
            String clave = fecha + " - Curso: " + (curso != null ? curso.getNombre() : "Desconocido");

            if (materia != null) {
                clave += " - Materia: " + materia.getNombre();
            } else {
                clave += " - (General)";
            }

            comboAsistencias.addItem(clave);
            mapaAsistencias.put(clave, a);
        }
    }

    private void mostrarDetalle() {
        panelEstudiantes.removeAll();
        combosPorEstudiante.clear();

        String clave = (String) comboAsistencias.getSelectedItem();
        if (clave == null) return;

        Asistencia asistenciaSeleccionada = mapaAsistencias.get(clave);
        if (asistenciaSeleccionada == null) return;

        List<AsistenciaDetalle> detalles = new ControladorAsistencia()
                .obtenerDetallesPorAsistencia(asistenciaSeleccionada.getIdAsistencia());
        asistenciaSeleccionada.setDetalles(detalles);

        List<Estudiante> estudiantes = new ControladorConsulta()
                .obtenerEstudiantesPorCurso(asistenciaSeleccionada.getIdCurso());

        Map<Integer, String> estados = new HashMap<>();
        for (AsistenciaDetalle detalle : asistenciaSeleccionada.getDetalles()) {
            estados.put(detalle.getIdEstudiante(), detalle.getEstado());
        }

        for (Estudiante e : estudiantes) {
            JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));
            fila.setMaximumSize(new Dimension(800, 30));

            JLabel lbl = new JLabel(e.getApellido() + ", " + e.getNombre());
            lbl.setFont(new Font("Arial", Font.PLAIN, 16));
            lbl.setPreferredSize(new Dimension(350, 25));
            fila.add(lbl);

            JComboBox<String> comboEstado = new JComboBox<>(new String[]{
                "Presente", "Ausente", "Ausente Justificado", "Retirado"
            });
            comboEstado.setFont(new Font("Arial", Font.PLAIN, 16));

            if (estados.containsKey(e.getIdEstudiante())) {
                comboEstado.setSelectedItem(estados.get(e.getIdEstudiante()));
            }

            combosPorEstudiante.put(e.getIdEstudiante(), comboEstado);
            fila.add(comboEstado);
            panelEstudiantes.add(fila);
        }

        panelEstudiantes.revalidate();
        panelEstudiantes.repaint();
    }

    private void guardarCambios() {
        String clave = (String) comboAsistencias.getSelectedItem();
        if (clave == null) return;

        Asistencia asistencia = mapaAsistencias.get(clave);
        if (asistencia == null) return;

        Map<Integer, String> nuevosEstados = new HashMap<>();
        for (Map.Entry<Integer, JComboBox<String>> entry : combosPorEstudiante.entrySet()) {
            nuevosEstados.put(entry.getKey(), (String) entry.getValue().getSelectedItem());
        }

        new ControladorAsistencia().editarAsistencia(asistencia.getIdAsistencia(), nuevosEstados);
        JOptionPane.showMessageDialog(this, "Asistencia actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
