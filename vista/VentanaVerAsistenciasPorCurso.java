package vista;

import controlador.ControladorAsistencia;
import controlador.ControladorConsulta;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class VentanaVerAsistenciasPorCurso extends JFrame {

    private JComboBox<String> comboCurso;
    private JTextArea areaResultados;
    private List<Curso> cursos;
    private final Usuario usuario;

    private final ControladorAsistencia controladorAsistencia;
    private final ControladorConsulta controladorConsulta;

    public VentanaVerAsistenciasPorCurso(Usuario usuario) {
        this.usuario = usuario;
        this.controladorAsistencia = new ControladorAsistencia();
        this.controladorConsulta = new ControladorConsulta();

        setTitle("Ver Asistencias por Curso");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== Panel de título =====
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(30, 80, 10, 80));

        JLabel lblTitulo = new JLabel("Visualización de Asistencias");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Seleccione un curso para consultar todas las asistencias registradas");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(5));
        panelTitulo.add(lblSub);
        add(panelTitulo, BorderLayout.NORTH);

        // ===== Panel superior con selección =====
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Curso"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblCurso = new JLabel("Curso:");
        lblCurso.setFont(new Font("Arial", Font.PLAIN, 16));
        comboCurso = new JComboBox<>();
        comboCurso.setFont(new Font("Arial", Font.PLAIN, 16));
        comboCurso.setPreferredSize(new Dimension(300, 28));
        cursos = controladorConsulta.obtenerCursos();

        for (Curso curso : cursos) {
            comboCurso.addItem(curso.getNombre());
        }

        JButton btnVer = new JButton("Ver asistencias");
        btnVer.setFont(new Font("Arial", Font.PLAIN, 16));
        btnVer.addActionListener(e -> mostrarAsistencias());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelSuperior.add(lblCurso, gbc);
        gbc.gridx = 1;
        panelSuperior.add(comboCurso, gbc);
        gbc.gridx = 2;
        panelSuperior.add(btnVer, gbc);

        add(panelSuperior, BorderLayout.BEFORE_FIRST_LINE);

        // ===== Panel central con resultados =====
        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaResultados.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(areaResultados);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultados"));
        add(scroll, BorderLayout.CENTER);

        // ===== Panel inferior =====
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 16));
        btnVolver.addActionListener(e -> {
            dispose();
            new VentanaPreceptor(usuario, "Preceptor").setVisible(true);
        });
        panelInferior.add(btnVolver);
        add(panelInferior, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Muestra todas las asistencias registradas del curso seleccionado.
     * Se detallan tanto generales como por materia, ordenadas por fecha descendente.
     */
    private void mostrarAsistencias() {
        int index = comboCurso.getSelectedIndex();
        if (index < 0) return;

        Curso curso = cursos.get(index);
        int idCurso = curso.getIdCurso();

        List<Asistencia> asistenciasCurso = controladorAsistencia.obtenerAsistencias().stream()
                .filter(a -> a.getIdCurso() == idCurso)
                .sorted((a1, a2) -> a2.getFecha().compareTo(a1.getFecha()))
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Asistencia asistencia : asistenciasCurso) {
            String tipo = "General";
            String materiaNombre = "";

            if (asistencia.getIdMateria() > 0) {
                tipo = "Por materia";
                Materia mat = controladorConsulta.buscarMateriaPorId(asistencia.getIdMateria());
                if (mat != null) materiaNombre = mat.getNombre();
            }

            sb.append("Fecha: ").append(asistencia.getFecha().format(formato))
              .append(" - Tipo: ").append(tipo);
            if (!materiaNombre.isEmpty()) {
                sb.append(" (").append(materiaNombre).append(")");
            }
            sb.append("\n");

            for (AsistenciaDetalle detalle : asistencia.getDetalles()) {
                Estudiante est = controladorConsulta.buscarEstudiantePorId(detalle.getIdEstudiante());
                if (est != null) {
                    sb.append(" - ").append(est.getApellido())
                      .append(", ").append(est.getNombre())
                      .append(": ").append(detalle.getEstado())
                      .append("\n");
                }
            }

            sb.append("\n");
        }

        if (sb.length() == 0) {
            areaResultados.setText("No se encontraron asistencias para este curso.");
        } else {
            areaResultados.setText(sb.toString());
        }
    }
}
