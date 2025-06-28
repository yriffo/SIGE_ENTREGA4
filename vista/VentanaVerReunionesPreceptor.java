package vista;

import controlador.ControladorConsulta;
import controlador.ControladorReunion;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Esta ventana es para el preceptor. Permite consultar reuniones confirmadas.
 * Trae los datos desde el controlador y los muestra con formato.
 * Se agregaron validaciones para evitar errores si falta algún dato en la base.
 * 
 * @author Yonatan
 */
public class VentanaVerReunionesPreceptor extends JFrame {

    private JComboBox<String> comboSolicitudes;
    private JTextArea areaDetalle;
    private List<SolicitudReunion> solicitudesConfirmadas;

    private final ControladorReunion controladorReunion;
    private final ControladorConsulta controladorConsulta;

    public VentanaVerReunionesPreceptor() {
        controladorReunion = new ControladorReunion();
        controladorConsulta = new ControladorConsulta();

        setTitle("Reuniones Confirmadas");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de arriba con el combo para elegir reunión
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        comboSolicitudes = new JComboBox<>();

        // Traemos todas las reuniones confirmadas desde el sistema
        solicitudesConfirmadas = controladorReunion.obtenerSolicitudes().stream()
                .filter(sr -> "Confirmada".equalsIgnoreCase(sr.getEstado()))
                .toList();

        // Recorremos las solicitudes y armamos el texto para el combo
        for (SolicitudReunion sr : solicitudesConfirmadas) {
            Usuario solicitante = controladorConsulta.buscarUsuarioPorId(sr.getIdUsuarioSolicitante());
            Curso curso = controladorConsulta.buscarCursoPorId(sr.getIdCurso());

            // Validamos por si algún dato no está bien cargado
            if (solicitante != null && curso != null) {
                String item = solicitante.getApellido() + ", " + solicitante.getNombre() + " - Curso " + curso.getNombre();
                comboSolicitudes.addItem(item);
            } else {
                comboSolicitudes.addItem("Solicitud #" + sr.getIdSolicitud() + " (datos incompletos)");
            }
        }

        comboSolicitudes.addActionListener(e -> mostrarDetalle());

        panelSuperior.add(new JLabel("Seleccione una reunión confirmada:"), BorderLayout.NORTH);
        panelSuperior.add(comboSolicitudes, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        // Área central donde se muestran los detalles completos
        areaDetalle = new JTextArea();
        areaDetalle.setEditable(false);
        areaDetalle.setFont(new Font("Monospaced", Font.PLAIN, 15));
        JScrollPane scroll = new JScrollPane(areaDetalle);
        scroll.setBorder(BorderFactory.createTitledBorder("Detalle de la reunión"));
        add(scroll, BorderLayout.CENTER);

        // Si hay al menos una solicitud confirmada, mostramos su detalle
        if (!solicitudesConfirmadas.isEmpty()) {
            comboSolicitudes.setSelectedIndex(0);
            mostrarDetalle();
        } else {
            areaDetalle.setText("No hay reuniones confirmadas.");
        }

        setVisible(true);
    }

    /**
     * Muestra los datos detallados de la solicitud seleccionada.
     * Se ve el solicitante, curso, motivo, fecha, y estudiantes.
     */
    private void mostrarDetalle() {
        int index = comboSolicitudes.getSelectedIndex();
        if (index >= 0 && index < solicitudesConfirmadas.size()) {
            SolicitudReunion sr = solicitudesConfirmadas.get(index);
            StringBuilder sb = new StringBuilder();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Buscamos el usuario y el curso para mostrar nombres reales
            Usuario solicitante = controladorConsulta.buscarUsuarioPorId(sr.getIdUsuarioSolicitante());
            Curso curso = controladorConsulta.buscarCursoPorId(sr.getIdCurso());
            List<Estudiante> estudiantes = controladorConsulta.obtenerEstudiantesPorCurso(sr.getIdCurso());

            if (solicitante == null || curso == null) {
                areaDetalle.setText("No se pudo cargar la información completa de esta reunión.");
                return;
            }

            // Armamos el texto con los datos de la reunión
            sb.append("Solicitante: ").append(solicitante.getApellido()).append(", ").append(solicitante.getNombre()).append("\n");
            sb.append("Curso: ").append(curso.getNombre()).append("\n\n");
            sb.append("Motivo: ").append(sr.getMotivo()).append("\n");
            sb.append("Disponibilidad declarada: ").append(sr.getDisponibilidad()).append("\n");
            sb.append("Estado: ").append(sr.getEstado()).append("\n");
            sb.append("Fecha de solicitud: ").append(sr.getFechaSolicitud().format(formato)).append("\n");

            if (sr.getFechaReunionConfirmada() != null) {
                sb.append("Fecha confirmada: ").append(sr.getFechaReunionConfirmada().format(formato)).append("\n");
            }
            if (sr.getHoraReunionConfirmada() != null) {
                sb.append("Hora confirmada: ").append(sr.getHoraReunionConfirmada().toString()).append("\n");
            }

            // Mostramos los estudiantes del curso
            sb.append("\nEstudiantes convocados:\n");
            for (Estudiante est : estudiantes) {
                sb.append(" - ").append(est.getApellido()).append(", ").append(est.getNombre()).append("\n");
            }

            areaDetalle.setText(sb.toString());
        }
    }
}
