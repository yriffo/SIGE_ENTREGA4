package vista;

import controlador.ControladorConsulta;
import controlador.ControladorReunion;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class SubventanaReunionesEstudiante extends JFrame {

    private final ControladorConsulta controladorConsulta;
    private final ControladorReunion controladorReunion;

    public SubventanaReunionesEstudiante(Estudiante estudiante, JFrame ventanaAnterior) {
        controladorConsulta = new ControladorConsulta();
        controladorReunion = new ControladorReunion();

        setTitle("Reuniones del Estudiante");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Historial de Reuniones", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 15));
        area.setMargin(new Insets(15, 15, 15, 15));
        JScrollPane scroll = new JScrollPane(area);
        add(scroll, BorderLayout.CENTER);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 15));
        btnVolver.addActionListener(e -> {
            dispose();
            if (ventanaAnterior != null) ventanaAnterior.setVisible(true);
        });
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panelBoton.add(btnVolver);
        add(panelBoton, BorderLayout.SOUTH);

        List<SolicitudReunion> reuniones = controladorReunion.obtenerSolicitudesPorEstudiante(estudiante.getIdEstudiante());
        reuniones.sort(Comparator.comparing(SolicitudReunion::getFechaSolicitud).reversed());

        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();

        if (reuniones.isEmpty()) {
            sb.append("El estudiante no tiene reuniones registradas.");
        } else {
            for (SolicitudReunion sr : reuniones) {
                Usuario solicitante = controladorConsulta.buscarUsuarioPorId(sr.getIdUsuarioSolicitante());
                Curso curso = controladorConsulta.buscarCursoPorId(sr.getIdCurso());

                sb.append("Fecha solicitud: ").append(sr.getFechaSolicitud().format(f)).append("\n");
                sb.append("Solicitante: ").append(solicitante.getApellido()).append(", ").append(solicitante.getNombre()).append("\n");
                sb.append("Curso: ").append(curso.getNombre()).append("\n");
                sb.append("Motivo: ").append(sr.getMotivo()).append("\n");
                sb.append("Disponibilidad: ").append(sr.getDisponibilidad()).append("\n");
                sb.append("Estado: ").append(sr.getEstado()).append("\n");

                if (sr.getFechaReunionConfirmada() != null) {
                    sb.append("Fecha confirmada: ").append(sr.getFechaReunionConfirmada().format(f)).append("\n");
                }
                if (sr.getHoraReunionConfirmada() != null) {
                    sb.append("Hora confirmada: ").append(sr.getHoraReunionConfirmada().toString()).append("\n");
                }

                sb.append("----------------------------------\n");
            }
        }

        area.setText(sb.toString());
        setVisible(true);
    }
}
