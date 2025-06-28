package vista;

import controlador.ControladorConsulta;
import modelo.Estudiante;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Subventana que muestra solo las asistencias del estudiante
 * donde el estado NO es "Presente". Incluye asistencias generales
 * y por materia si corresponde. Usada en la bitácora de Preceptor,
 * Asesor y Directivo.
 * 
 * @author Yonatan
 */
public class SubventanaAsistenciasEstudiante extends JFrame {

    private final ControladorConsulta controladorConsulta;

    public SubventanaAsistenciasEstudiante(Estudiante estudiante, JFrame ventanaAnterior) {
        this.controladorConsulta = new ControladorConsulta();

        setTitle("Asistencias del Estudiante");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Área de texto
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

        // Obtener asistencias no "Presente"
        List<String[]> asistencias = controladorConsulta.obtenerAsistenciasNoPresentesPorEstudiante(estudiante.getIdEstudiante());

        StringBuilder sb = new StringBuilder();
        sb.append("Estudiante: ").append(estudiante.getApellido()).append(", ").append(estudiante.getNombre()).append("\n\n");
        sb.append("Asistencias registradas (Ausencias o Retiros):\n\n");

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (asistencias.isEmpty()) {
            sb.append("No hay asistencias registradas distintas de 'Presente'.");
        } else {
            for (String[] fila : asistencias) {
                String fecha = LocalDate.parse(fila[0]).format(formato);
                String curso = fila[1];
                String materia = fila[2];
                String estado = fila[3];

                sb.append("Fecha: ").append(fecha).append(" / ");
                sb.append("Curso: ").append(curso).append(" / ");
                sb.append("Materia: ").append(materia).append(" / ");
                sb.append("Estado: ").append(estado).append("\n");
            }
        }

        area.setText(sb.toString());
        setVisible(true);
    }
}
