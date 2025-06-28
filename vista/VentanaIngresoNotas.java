package vista;

import controlador.ControladorConsulta;
import controlador.ControladorCalificacion;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ventana para ingresar calificaciones de una actividad.
 * Usa controladores reales y conexión con base de datos.
 * 
 * @author Yonatan
 */
public class VentanaIngresoNotas extends JFrame {

    private Actividad actividad;
    private Curso curso;
    private Materia materia;
    private Usuario docente;

    private ControladorConsulta controladorConsulta;
    private ControladorCalificacion controladorCalificacion;

    private Map<Estudiante, JComboBox<String>> combosNotas;

    public VentanaIngresoNotas(Usuario docente, Curso curso, Materia materia, Actividad actividad) {
        this.docente = docente;
        this.curso = curso;
        this.materia = materia;
        this.actividad = actividad;

        this.controladorConsulta = new ControladorConsulta();
        this.controladorCalificacion = new ControladorCalificacion();
        this.combosNotas = new HashMap<>();

        setTitle("Calificar actividad: " + actividad.getTitulo());
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titulo = new JLabel("Curso: " + curso.getNombre() + " - Materia: " + materia.getNombre() +
                " - Tipo: " + actividad.getTipo());
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(titulo);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 15)));

        // Obtener estudiantes del curso desde BD
        List<Estudiante> estudiantes = controladorConsulta.obtenerEstudiantesPorCurso(curso.getIdCurso());

        // Generar combo de calificaciones para cada estudiante
        for (Estudiante est : estudiantes) {
            JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel lblNombre = new JLabel(est.getApellido() + ", " + est.getNombre());
            lblNombre.setPreferredSize(new Dimension(250, 25));

            JComboBox<String> comboNota = new JComboBox<>();
            cargarOpcionesNota(comboNota, actividad.getTipo());

            // Si ya hay nota registrada, cargarla
            String notaActual = controladorConsulta.obtenerNota(actividad.getIdActividad(), est.getIdEstudiante());
            comboNota.setSelectedItem(notaActual);

            combosNotas.put(est, comboNota);

            fila.add(lblNombre);
            fila.add(comboNota);
            panelPrincipal.add(fila);
        }

        // Botón para guardar notas
        JButton btnGuardar = new JButton("Guardar calificaciones");
        btnGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGuardar.addActionListener(e -> guardarNotas());

        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        panelPrincipal.add(btnGuardar);

        JScrollPane scroll = new JScrollPane(panelPrincipal);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scroll);
        setVisible(true);
    }

    private void cargarOpcionesNota(JComboBox<String> combo, String tipo) {
        combo.removeAllItems();

        combo.addItem("NE");
        combo.addItem("Ausente");
        combo.addItem("Ausente Justificado");

        if (tipo.equalsIgnoreCase("Numerica")) {
            for (int i = 1; i <= 10; i++) combo.addItem(String.valueOf(i));
        } else {
            combo.addItem("Mal");
            combo.addItem("Regular");
            combo.addItem("Bien");
            combo.addItem("Muy Bien");
            combo.addItem("Excelente");
        }
    }

    private void guardarNotas() {
        boolean exito = true;

        for (Map.Entry<Estudiante, JComboBox<String>> entry : combosNotas.entrySet()) {
            Estudiante estudiante = entry.getKey();
            String valorSeleccionado = (String) entry.getValue().getSelectedItem();

            try {
                controladorCalificacion.guardarCalificacion(
                        actividad.getIdActividad(),
                        estudiante.getIdEstudiante(),
                        valorSeleccionado
                );
            } catch (Exception e) {
                exito = false;
                JOptionPane.showMessageDialog(this,
                        "Error al guardar la calificación de " +
                        estudiante.getApellido() + ", " + estudiante.getNombre(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (exito) {
            JOptionPane.showMessageDialog(this, "Calificaciones guardadas correctamente.");
            dispose();
        }
    }
}
