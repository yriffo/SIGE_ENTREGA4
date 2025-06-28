package vista;

import controlador.ControladorConsulta;
import controlador.ControladorReunion;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Vista para el preceptor.
 * Permite ver todas las reuniones en las que participó un estudiante de un curso.
 * Muestra la información real (nombres y fechas).
 *
 * @author Yonatan
 */
public class VentanaVerReunionesPorEstudiantePreceptor extends JFrame {

    private JComboBox<String> comboCurso;
    private JComboBox<String> comboEstudiante;
    private JTextArea areaDetalle;

    private List<Curso> cursos;
    private List<Estudiante> estudiantesCurso;

    private final ControladorConsulta controladorConsulta;
    private final ControladorReunion controladorReunion;

    public VentanaVerReunionesPorEstudiantePreceptor() {
        controladorConsulta = new ControladorConsulta();
        controladorReunion = new ControladorReunion();

        setTitle("Reuniones por estudiante");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior con combos de selección
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCurso = new JLabel("Curso:");
        lblCurso.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelSuperior.add(lblCurso, gbc);

        comboCurso = new JComboBox<>();
        comboCurso.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        panelSuperior.add(comboCurso, gbc);

        JLabel lblEstudiante = new JLabel("Estudiante:");
        lblEstudiante.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelSuperior.add(lblEstudiante, gbc);

        comboEstudiante = new JComboBox<>();
        comboEstudiante.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        panelSuperior.add(comboEstudiante, gbc);

        add(panelSuperior, BorderLayout.NORTH);

        // Área de texto con los resultados
        areaDetalle = new JTextArea();
        areaDetalle.setEditable(false);
        areaDetalle.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(areaDetalle);
        scroll.setBorder(BorderFactory.createTitledBorder("Reuniones del estudiante"));
        add(scroll, BorderLayout.CENTER);

        // Eventos
        comboCurso.addActionListener(e -> cargarEstudiantes());
        comboEstudiante.addActionListener(e -> mostrarReuniones());

        // Carga inicial
        cursos = controladorConsulta.obtenerCursos();
        for (Curso c : cursos) {
            comboCurso.addItem(c.getNombre());
        }

        if (!cursos.isEmpty()) {
            comboCurso.setSelectedIndex(0);
            cargarEstudiantes();
        }

        setVisible(true);
    }

    /**
     * Carga los estudiantes del curso elegido en el segundo combo.
     */
    private void cargarEstudiantes() {
        int indexCurso = comboCurso.getSelectedIndex();
        if (indexCurso < 0 || indexCurso >= cursos.size()) return;

        Curso curso = cursos.get(indexCurso);
        estudiantesCurso = controladorConsulta.obtenerEstudiantesPorCurso(curso.getIdCurso());

        comboEstudiante.removeAllItems();
        for (Estudiante est : estudiantesCurso) {
            comboEstudiante.addItem(est.getApellido() + ", " + est.getNombre());
        }

        if (!estudiantesCurso.isEmpty()) {
            comboEstudiante.setSelectedIndex(0);
            mostrarReuniones();
        } else {
            areaDetalle.setText("Este curso no tiene estudiantes cargados.");
        }
    }

    /**
     * Muestra todas las reuniones donde participó el estudiante seleccionado.
     */
    private void mostrarReuniones() {
        int indexEst = comboEstudiante.getSelectedIndex();
        if (indexEst < 0 || indexEst >= estudiantesCurso.size()) return;

        Estudiante estudiante = estudiantesCurso.get(indexEst);
        List<SolicitudReunion> reuniones = controladorReunion.obtenerSolicitudesPorEstudiante(estudiante.getIdEstudiante());

        if (reuniones.isEmpty()) {
            areaDetalle.setText("No hay reuniones registradas para este estudiante.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (SolicitudReunion sr : reuniones) {
            Usuario solicitante = controladorConsulta.buscarUsuarioPorId(sr.getIdUsuarioSolicitante());
            Curso curso = controladorConsulta.buscarCursoPorId(sr.getIdCurso());

            sb.append("Solicitada por: ");
            if (solicitante != null) {
                sb.append(solicitante.getApellido()).append(", ").append(solicitante.getNombre());
            } else {
                sb.append("Usuario desconocido");
            }

            sb.append(" | Curso: ");
            sb.append(curso != null ? curso.getNombre() : "Desconocido");
            sb.append("\n");

            sb.append("Motivo: ").append(sr.getMotivo()).append("\n");
            sb.append("Disponibilidad: ").append(sr.getDisponibilidad()).append("\n");
            sb.append("Estado: ").append(sr.getEstado()).append("\n");
            sb.append("Fecha de solicitud: ").append(sr.getFechaSolicitud().format(formato)).append("\n");

            if (sr.getFechaReunionConfirmada() != null) {
                sb.append("Fecha confirmada: ").append(sr.getFechaReunionConfirmada().format(formato)).append("\n");
            }
            if (sr.getHoraReunionConfirmada() != null) {
                sb.append("Hora confirmada: ").append(sr.getHoraReunionConfirmada().toString()).append("\n");
            }

            sb.append("-------------------------------------------------------------\n");
        }

        areaDetalle.setText(sb.toString());
    }
}
