package vista;

import controlador.ControladorConsulta;
import modelo.Curso;
import modelo.Estudiante;

import javax.swing.*;
import java.awt.*;

public class SubventanaDatosEstudiante extends JFrame {

    private final ControladorConsulta controladorConsulta;

    public SubventanaDatosEstudiante(Estudiante estudiante, JFrame ventanaAnterior) {
        controladorConsulta = new ControladorConsulta();

        setTitle("Datos del Estudiante");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Datos Personales y Familiares", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        Curso curso = controladorConsulta.buscarCursoPorId(estudiante.getIdCurso());

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 15));
        area.setMargin(new Insets(15, 15, 15, 15));

        StringBuilder sb = new StringBuilder();
        sb.append("Nombre completo: ").append(estudiante.getNombre()).append(" ").append(estudiante.getApellido()).append("\n");
        sb.append("DNI: ").append(estudiante.getDni()).append("\n");
        sb.append("Teléfono: ").append(estudiante.getTelefono()).append("\n");
        sb.append("Responsable adulto: ").append(estudiante.getResponsable()).append("\n\n");

        if (curso != null) {
            sb.append("Curso: ").append(curso.getNombre()).append("\n");
            sb.append("Año lectivo: ").append(curso.getAnio()).append("\n");
        } else {
            sb.append("Curso: (no disponible)\n");
        }

        area.setText(sb.toString());
        add(new JScrollPane(area), BorderLayout.CENTER);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 15));
        btnVolver.addActionListener(e -> dispose());

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panelBoton.add(btnVolver);
        add(panelBoton, BorderLayout.SOUTH);

        setVisible(true);
    }
}
