package vista;

import controlador.ControladorConsulta;
import modelo.Estudiante;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Subventana que muestra todas las calificaciones del estudiante
 * agrupadas por materia y ordenadas por fecha.
 * Utiliza ControladorConsulta con datos reales desde planillacalificaciones y calificacion.
 * 
 * @author Yonatan
 */
public class SubventanaCalificacionesEstudiante extends JFrame {

    private final ControladorConsulta controladorConsulta;

    public SubventanaCalificacionesEstudiante(Estudiante estudiante, JFrame ventanaAnterior) {
        this.controladorConsulta = new ControladorConsulta();

        setTitle("Calificaciones del Estudiante");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Área de texto con scroll
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 15));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        add(scroll, BorderLayout.CENTER);

        // Botón volver
        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 15));
        btnVolver.addActionListener(e -> {
            dispose();
            if (ventanaAnterior != null) ventanaAnterior.setVisible(true);
        });
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panelBoton.add(btnVolver);
        add(panelBoton, BorderLayout.SOUTH);

        // Obtener calificaciones
        List<String[]> calificaciones = controladorConsulta.obtenerCalificacionesPorEstudiante(estudiante.getIdEstudiante());

        StringBuilder sb = new StringBuilder();
        sb.append("Estudiante: ").append(estudiante.getApellido()).append(", ").append(estudiante.getNombre()).append("\n\n");

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (calificaciones.isEmpty()) {
            sb.append("No hay calificaciones registradas.");
        } else {
            for (String[] fila : calificaciones) {
                String materia = fila[0];
                String fechaCruda = fila[1];
                String fechaFormateada;

                // Intentar formatear la fecha si está en formato válido
                try {
                    fechaFormateada = LocalDate.parse(fechaCruda).format(formato);
                } catch (Exception ex) {
                    fechaFormateada = fechaCruda; // si no se puede convertir, se deja como está
                }

                String actividad = fila[2];
                String nota = fila[3];

                sb.append("Materia: ").append(materia).append("\n");
                sb.append("Fecha: ").append(fechaFormateada).append("\n");
                sb.append("Actividad: ").append(actividad).append("\n");
                sb.append("Calificación: ").append(nota).append("\n");
                sb.append("---------------------------------------------------\n");
            }
        }

        area.setText(sb.toString());
        setVisible(true);
    }
}
