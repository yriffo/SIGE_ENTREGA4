package vista;

import controlador.ControladorActividad;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

/**
 * Ventana para que el Docente registre una nueva actividad.
 * Rediseñada visualmente con pantalla completa, estilo uniforme, y distribución moderna.
 * Mantiene la lógica original del sistema SIGE.
 * 
 * @author Yonatan
 */
public class VentanaRegistrarActividad extends JFrame {

    private final Usuario docente;

    private JComboBox<String> comboCurso;
    private JComboBox<String> comboMateria;
    private JTextField campoTitulo;
    private JComboBox<String> comboTipo;
    private JSpinner spinnerFecha;

    private Map<String, List<String>> mapaCursoMateria;
    private ControladorActividad controlador;

    public VentanaRegistrarActividad(Usuario docente) {
        this.docente = docente;
        this.controlador = new ControladorActividad();

        setTitle("Registrar Actividad - SIGE");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // pantalla completa
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior con título y subtítulo
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(30, 100, 10, 100));

        JLabel titulo = new JLabel("Registrar Nueva Actividad");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Complete los siguientes campos:");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        panelSuperior.add(titulo);
        panelSuperior.add(subtitulo);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel central con formulario
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 15, 15));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 150, 20, 150));
        Font fuente = new Font("Arial", Font.PLAIN, 18);

        // Curso
        JLabel lblCurso = new JLabel("Curso:");
        lblCurso.setFont(fuente);
        panelFormulario.add(lblCurso);

        comboCurso = new JComboBox<>();
        comboCurso.setFont(fuente);
        panelFormulario.add(comboCurso);

        // Materia
        JLabel lblMateria = new JLabel("Materia:");
        lblMateria.setFont(fuente);
        panelFormulario.add(lblMateria);

        comboMateria = new JComboBox<>();
        comboMateria.setFont(fuente);
        panelFormulario.add(comboMateria);

        // Título
        JLabel lblTitulo = new JLabel("Título:");
        lblTitulo.setFont(fuente);
        panelFormulario.add(lblTitulo);

        campoTitulo = new JTextField();
        campoTitulo.setFont(fuente);
        panelFormulario.add(campoTitulo);

        // Tipo de calificación
        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setFont(fuente);
        panelFormulario.add(lblTipo);

        comboTipo = new JComboBox<>(new String[]{"Numerica", "Conceptual"});
        comboTipo.setFont(fuente);
        panelFormulario.add(comboTipo);

        // Fecha
        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(fuente);
        panelFormulario.add(lblFecha);

        SpinnerDateModel modeloFecha = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        spinnerFecha = new JSpinner(modeloFecha);
        JSpinner.DateEditor editorFecha = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");
        spinnerFecha.setEditor(editorFecha);
        spinnerFecha.setFont(fuente);
        panelFormulario.add(spinnerFecha);

        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        JButton botonRegistrar = new JButton("Registrar");
        botonRegistrar.setFont(new Font("Arial", Font.BOLD, 18));
        botonRegistrar.addActionListener(e -> registrarActividad());

        JButton botonCancelar = new JButton("Cancelar");
        botonCancelar.setFont(new Font("Arial", Font.PLAIN, 18));
        botonCancelar.addActionListener(e -> dispose());

        panelBotones.add(botonRegistrar);
        panelBotones.add(botonCancelar);

        add(panelFormulario, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        cargarDatos();
        setVisible(true);
    }

    private void cargarDatos() {
        mapaCursoMateria = controlador.obtenerCursosYMateriasDelDocente(docente.getIdUsuario());

        if (mapaCursoMateria.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron cursos o materias asignadas.",
                    "Sin datos",
                    JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        for (String curso : mapaCursoMateria.keySet()) {
            comboCurso.addItem(curso);
        }

        comboCurso.addActionListener(e -> actualizarMaterias());
        actualizarMaterias();
    }

    private void actualizarMaterias() {
        comboMateria.removeAllItems();
        String cursoSeleccionado = (String) comboCurso.getSelectedItem();

        if (cursoSeleccionado != null && mapaCursoMateria.containsKey(cursoSeleccionado)) {
            for (String materia : mapaCursoMateria.get(cursoSeleccionado)) {
                comboMateria.addItem(materia);
            }
        }
    }

    private void registrarActividad() {
        String curso = (String) comboCurso.getSelectedItem();
        String materia = (String) comboMateria.getSelectedItem();
        String titulo = campoTitulo.getText().trim();
        String tipo = (String) comboTipo.getSelectedItem();

        if (curso == null || materia == null || titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor complete todos los campos.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date fechaSeleccionada = (Date) spinnerFecha.getValue();
            LocalDate fecha = fechaSeleccionada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            int idCurso = controlador.obtenerIdCursoPorNombre(curso);
            int idMateria = controlador.obtenerIdMateriaPorNombre(materia);

            if (idCurso == -1 || idMateria == -1) {
                throw new Exception("No se encontraron los IDs necesarios.");
            }

            controlador.registrarActividad(docente.getIdUsuario(), titulo, tipo, fecha, idCurso, idMateria);

            JOptionPane.showMessageDialog(this,
                    "Actividad registrada correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error al registrar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
