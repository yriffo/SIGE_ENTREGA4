package vista;

import controlador.ControladorAsistencia;
import controlador.ControladorConsulta;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Ventana que permite al Preceptor editar asistencias ya registradas.
 * Usa controladores y valida que existan datos antes de editar.
 * Rediseñada para pantalla completa y estilo visual uniforme con el sistema SIGE.
 * 
 * @author Yonatan
 */
public class VentanaEditarAsistencia extends JFrame {

    private Usuario usuario;
    private JComboBox<String> comboAsistencias;
    private JPanel panelEstudiantes;
    private JButton botonGuardar;

    private Map<String, Asistencia> mapaAsistencias;
    private Map<AsistenciaDetalle, JComboBox<String>> nuevosEstados;

    private ControladorAsistencia controladorAsistencia;
    private ControladorConsulta controladorConsulta;

    private final String[] estados = {
        "Presente", "Ausente", "Ausente Justificado", "Retirado"
    };

    public VentanaEditarAsistencia(Usuario usuario) {
        this.usuario = usuario;
        this.controladorAsistencia = new ControladorAsistencia();
        this.controladorConsulta = new ControladorConsulta();

        setTitle("Editar Asistencia Registrada");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // pantalla completa
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        mapaAsistencias = new LinkedHashMap<>();
        nuevosEstados = new HashMap<>();

        // Combo de asistencias
        comboAsistencias = new JComboBox<>();
        comboAsistencias.setFont(new Font("Arial", Font.PLAIN, 18));
        comboAsistencias.addActionListener(e -> cargarDetalles());

        // Panel de estudiantes con scroll
        panelEstudiantes = new JPanel();
        panelEstudiantes.setLayout(new BoxLayout(panelEstudiantes, BoxLayout.Y_AXIS));
        panelEstudiantes.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JScrollPane scroll = new JScrollPane(panelEstudiantes);
        scroll.setBorder(BorderFactory.createTitledBorder("Listado de estudiantes"));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Botones
        botonGuardar = new JButton("Guardar cambios");
        botonGuardar.setFont(new Font("Arial", Font.BOLD, 18));
        botonGuardar.addActionListener(e -> guardarCambios());

        JButton botonVolver = new JButton("Volver");
        botonVolver.setFont(new Font("Arial", Font.PLAIN, 18));
        botonVolver.addActionListener(e -> {
            dispose();
            new VentanaPreceptor(usuario, "Preceptor").setVisible(true);
        });

        // Panel superior
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(30, 100, 10, 100));

        JLabel titulo = new JLabel("Editar Asistencia Registrada");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Seleccione una asistencia para editar:");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        comboAsistencias.setMaximumSize(new Dimension(600, 35));
        comboAsistencias.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelSuperior.add(titulo);
        panelSuperior.add(subtitulo);
        panelSuperior.add(comboAsistencias);

        // Panel inferior con botones
        JPanel panelInferior = new JPanel(new FlowLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        panelInferior.add(botonGuardar);
        panelInferior.add(botonVolver);

        // Agregar todo al frame
        add(panelSuperior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        cargarAsistencias();
        setVisible(true);
    }

    private void cargarAsistencias() {
        comboAsistencias.removeAllItems();
        mapaAsistencias.clear();

        List<Curso> cursos = controladorConsulta.obtenerCursos();
        List<Asistencia> asistencias = controladorAsistencia.obtenerAsistencias();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Asistencia a : asistencias) {
            for (Curso c : cursos) {
                if (a.getIdCurso() == c.getIdCurso()) {
                    String clave = formatter.format(a.getFecha()) + " - " + c.getNombre();
                    if (a.getIdMateria() != -1) {
                        Materia m = controladorConsulta.buscarMateriaPorId(a.getIdMateria());
                        if (m != null) clave += " - " + m.getNombre();
                    }
                    comboAsistencias.addItem(clave);
                    mapaAsistencias.put(clave, a);
                }
            }
        }

        if (mapaAsistencias.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay asistencias disponibles para editar.");
            dispose();
            new VentanaPreceptor(usuario, "Preceptor").setVisible(true);
        }
    }

    private void cargarDetalles() {
        panelEstudiantes.removeAll();
        nuevosEstados.clear();

        String seleccion = (String) comboAsistencias.getSelectedItem();
        if (seleccion == null || !mapaAsistencias.containsKey(seleccion)) return;

        Asistencia asistencia = mapaAsistencias.get(seleccion);

        for (AsistenciaDetalle detalle : asistencia.getDetalles()) {
            Estudiante est = controladorConsulta.buscarEstudiantePorId(detalle.getIdEstudiante());
            if (est == null) continue;

            JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));
            fila.setMaximumSize(new Dimension(800, 30));

            JLabel lbl = new JLabel(est.getApellido() + ", " + est.getNombre());
            lbl.setFont(new Font("Arial", Font.PLAIN, 16));
            lbl.setPreferredSize(new Dimension(350, 25));

            JComboBox<String> comboEstado = new JComboBox<>(estados);
            comboEstado.setFont(new Font("Arial", Font.PLAIN, 16));
            comboEstado.setSelectedItem(detalle.getEstado());

            fila.add(lbl);
            fila.add(comboEstado);

            panelEstudiantes.add(fila);
            nuevosEstados.put(detalle, comboEstado);
        }

        panelEstudiantes.revalidate();
        panelEstudiantes.repaint();
    }

    private void guardarCambios() {
        if (nuevosEstados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para modificar.");
            return;
        }

        Map<Integer, String> nuevos = new HashMap<>();
        for (Map.Entry<AsistenciaDetalle, JComboBox<String>> entry : nuevosEstados.entrySet()) {
            nuevos.put(entry.getKey().getIdEstudiante(), (String) entry.getValue().getSelectedItem());
        }

        String seleccion = (String) comboAsistencias.getSelectedItem();
        if (seleccion == null) return;

        Asistencia asistencia = mapaAsistencias.get(seleccion);
        controladorAsistencia.editarAsistencia(asistencia.getIdAsistencia(), nuevos);

        JOptionPane.showMessageDialog(this, "Asistencia actualizada correctamente.");
        dispose();
        new VentanaPreceptor(usuario, "Preceptor").setVisible(true);
    }
}
