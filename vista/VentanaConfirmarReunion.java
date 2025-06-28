package vista;

import controlador.ControladorConsulta;
import controlador.ControladorReunion;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class VentanaConfirmarReunion extends JFrame {

    private JComboBox<String> comboSolicitudes;
    private JTextArea areaDetalle;
    private JSpinner spinnerFecha;
    private JSpinner spinnerHora;
    private List<SolicitudReunion> solicitudesPendientes;

    private final ControladorReunion controladorReunion;
    private final ControladorConsulta controladorConsulta;

    public VentanaConfirmarReunion() {
        this.controladorReunion = new ControladorReunion();
        this.controladorConsulta = new ControladorConsulta();

        setTitle("Confirmar Solicitudes de Reunión");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        Font fuente = new Font("Arial", Font.PLAIN, 17);
        Font fuenteBold = new Font("Arial", Font.BOLD, 20);

        // ======= TÍTULO =========
        JLabel titulo = new JLabel("Confirmación de Reuniones Pendientes", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // ======= PANEL SUPERIOR: Combo de solicitudes =========
        JPanel panelCombo = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panelCombo.setBorder(BorderFactory.createTitledBorder("Seleccione una solicitud pendiente"));
        comboSolicitudes = new JComboBox<>();
        comboSolicitudes.setPreferredSize(new Dimension(600, 30));
        comboSolicitudes.setFont(fuente);

        solicitudesPendientes = controladorReunion.obtenerSolicitudes().stream()
                .filter(sr -> sr.getEstado().equalsIgnoreCase("Pendiente"))
                .toList();

        for (SolicitudReunion sr : solicitudesPendientes) {
            Usuario solicitante = controladorConsulta.buscarUsuarioPorId(sr.getIdUsuarioSolicitante());
            Curso curso = controladorConsulta.buscarCursoPorId(sr.getIdCurso());
            String texto = solicitante.getApellido() + ", " + solicitante.getNombre() + " - Curso " + curso.getNombre();
            comboSolicitudes.addItem(texto);
        }

        comboSolicitudes.addActionListener(e -> mostrarDetalle());
        panelCombo.add(comboSolicitudes);
        add(panelCombo, BorderLayout.BEFORE_FIRST_LINE);

        // ======= PANEL CENTRAL: Área de detalle =========
        areaDetalle = new JTextArea();
        areaDetalle.setFont(new Font("Monospaced", Font.PLAIN, 16));
        areaDetalle.setEditable(false);
        areaDetalle.setMargin(new Insets(15, 15, 15, 15));

        JScrollPane scrollDetalle = new JScrollPane(areaDetalle);
        scrollDetalle.setBorder(BorderFactory.createTitledBorder("Detalle de la solicitud"));
        scrollDetalle.setPreferredSize(new Dimension(1000, 500));
        add(scrollDetalle, BorderLayout.CENTER);

        // ======= PANEL INFERIOR: Fecha, hora y botón =========
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        spinnerFecha = new JSpinner(new SpinnerDateModel());
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy"));
        spinnerFecha.setFont(fuente);
        spinnerFecha.setPreferredSize(new Dimension(150, 30));

        spinnerHora = new JSpinner(new SpinnerDateModel());
        spinnerHora.setEditor(new JSpinner.DateEditor(spinnerHora, "HH:mm"));
        spinnerHora.setFont(fuente);
        spinnerHora.setPreferredSize(new Dimension(100, 30));

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(fuente);
        JLabel lblHora = new JLabel("Hora:");
        lblHora.setFont(fuente);

        JButton btnConfirmar = new JButton("Confirmar reunión");
        btnConfirmar.setFont(fuenteBold);
        btnConfirmar.setPreferredSize(new Dimension(250, 35));
        btnConfirmar.addActionListener(e -> confirmarReunionSeleccionada());

        panelInferior.add(lblFecha);
        panelInferior.add(spinnerFecha);
        panelInferior.add(lblHora);
        panelInferior.add(spinnerHora);
        panelInferior.add(btnConfirmar);

        add(panelInferior, BorderLayout.SOUTH);

        // Mostrar primer detalle si hay solicitudes
        if (!solicitudesPendientes.isEmpty()) {
            comboSolicitudes.setSelectedIndex(0);
            mostrarDetalle();
        } else {
            areaDetalle.setText("No hay solicitudes pendientes.");
        }

        setVisible(true);
    }

    private void mostrarDetalle() {
        int index = comboSolicitudes.getSelectedIndex();
        if (index >= 0 && index < solicitudesPendientes.size()) {
            SolicitudReunion sr = solicitudesPendientes.get(index);

            Usuario solicitante = controladorConsulta.buscarUsuarioPorId(sr.getIdUsuarioSolicitante());
            Curso curso = controladorConsulta.buscarCursoPorId(sr.getIdCurso());

            // Filtramos los estudiantes de ese curso que participaron en la solicitud
            List<Estudiante> todos = controladorConsulta.obtenerEstudiantesPorCurso(sr.getIdCurso());
            List<String> estudiantes = todos.stream()
                    .filter(e -> controladorReunion.obtenerSolicitudesPorEstudiante(e.getIdEstudiante())
                            .stream().anyMatch(s -> s.getIdSolicitud() == sr.getIdSolicitud()))
                    .map(e -> e.getApellido() + ", " + e.getNombre())
                    .collect(Collectors.toList());

            StringBuilder sb = new StringBuilder();
            sb.append("Solicitante: ").append(solicitante.getApellido()).append(", ").append(solicitante.getNombre()).append("\n\n");
            sb.append("Curso: ").append(curso.getNombre()).append("\n");
            sb.append("Motivo: ").append(sr.getMotivo()).append("\n");
            sb.append("Disponibilidad: ").append(sr.getDisponibilidad()).append("\n");
            sb.append("Estado: ").append(sr.getEstado()).append("\n");
            sb.append("Fecha de solicitud: ").append(sr.getFechaSolicitud()).append("\n\n");

            sb.append("Estudiantes:\n");
            for (String est : estudiantes) {
                sb.append(" - ").append(est).append("\n");
            }

            areaDetalle.setText(sb.toString());
        }
    }

    private void confirmarReunionSeleccionada() {
        int index = comboSolicitudes.getSelectedIndex();
        if (index < 0 || index >= solicitudesPendientes.size()) {
            JOptionPane.showMessageDialog(this, "No hay solicitud seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date fechaSeleccionada = (Date) spinnerFecha.getValue();
        Date horaSeleccionada = (Date) spinnerHora.getValue();

        LocalDate fecha = fechaSeleccionada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime hora = horaSeleccionada.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        SolicitudReunion sr = solicitudesPendientes.get(index);
        boolean exito = controladorReunion.confirmarReunion(sr.getIdSolicitud(), fecha, hora);

        if (exito) {
            JOptionPane.showMessageDialog(this, "Reunión confirmada exitosamente.");
            dispose();
            new VentanaConfirmarReunion();
        } else {
            JOptionPane.showMessageDialog(this, "Error al confirmar la reunión.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
