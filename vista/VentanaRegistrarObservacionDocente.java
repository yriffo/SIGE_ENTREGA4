package vista;

import controlador.ControladorBitacora;
import controlador.ControladorConsulta;
import modelo.Curso;
import modelo.Estudiante;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Ventana que permite al Docente registrar observaciones pedagógicas directas
 * en la bitácora de uno o más estudiantes de un curso asignado.
 * Cumple con el caso de uso CU10 del sistema SIGE.
 *
 * @author Yonatan
 */
public class VentanaRegistrarObservacionDocente extends JFrame {

    private final Usuario docente;
    private JComboBox<Curso> comboCursos;
    private JPanel panelEstudiantes;
    private JTextArea campoObservacion;
    private List<JCheckBox> checkboxesEstudiantes;

    public VentanaRegistrarObservacionDocente(Usuario docente) {
        this.docente = docente;
        setTitle("Registrar Observación Pedagógica");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Registrar Observación Pedagógica", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        // Selección de curso
        panelCentral.add(new JLabel("Seleccione un curso:"));
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10)));
        comboCursos = new JComboBox<>();
        for (Curso c : new ControladorConsulta().obtenerCursosPorDocente(docente.getIdUsuario())) {
            comboCursos.addItem(c);
        }
        comboCursos.addActionListener(e -> cargarEstudiantes());
        panelCentral.add(comboCursos);

        panelCentral.add(Box.createRigidArea(new Dimension(0, 20)));
        panelCentral.add(new JLabel("Seleccione estudiante(s):"));
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10)));
        panelEstudiantes = new JPanel();
        panelEstudiantes.setLayout(new BoxLayout(panelEstudiantes, BoxLayout.Y_AXIS));
        JScrollPane scrollEstudiantes = new JScrollPane(panelEstudiantes);
        scrollEstudiantes.setPreferredSize(new Dimension(600, 200));
        panelCentral.add(scrollEstudiantes);

        panelCentral.add(Box.createRigidArea(new Dimension(0, 20)));
        panelCentral.add(new JLabel("Observación pedagógica:"));
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10)));
        campoObservacion = new JTextArea(5, 60);
        campoObservacion.setLineWrap(true);
        campoObservacion.setWrapStyleWord(true);
        JScrollPane scrollObservacion = new JScrollPane(campoObservacion);
        panelCentral.add(scrollObservacion);

        // Botón registrar
        panelCentral.add(Box.createRigidArea(new Dimension(0, 20)));
        JButton botonRegistrar = new JButton("Registrar observación");
        botonRegistrar.setFont(new Font("Arial", Font.BOLD, 20));
        botonRegistrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonRegistrar.addActionListener(e -> registrarObservacion());
        panelCentral.add(botonRegistrar);

        // Scroll general
        JScrollPane scrollGeneral = new JScrollPane(panelCentral);
        scrollGeneral.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollGeneral, BorderLayout.CENTER);

        // Inicializar carga de estudiantes si hay cursos
        if (comboCursos.getItemCount() > 0) {
            comboCursos.setSelectedIndex(0);
            cargarEstudiantes();
        }

        setVisible(true);
    }

    /**
     * Carga los estudiantes del curso seleccionado y genera checkboxes
     */
    private void cargarEstudiantes() {
        panelEstudiantes.removeAll();
        checkboxesEstudiantes = new ArrayList<>();

        Curso cursoSeleccionado = (Curso) comboCursos.getSelectedItem();
        if (cursoSeleccionado != null) {
            List<Estudiante> estudiantes = new ControladorConsulta().obtenerEstudiantesPorCurso(cursoSeleccionado.getIdCurso());
            for (Estudiante est : estudiantes) {
                JCheckBox check = new JCheckBox(est.getApellido() + ", " + est.getNombre());
                check.setActionCommand(String.valueOf(est.getIdEstudiante()));
                checkboxesEstudiantes.add(check);
                panelEstudiantes.add(check);
            }
        }
        panelEstudiantes.revalidate();
        panelEstudiantes.repaint();
    }

    /**
     * Valida los campos y registra la observación para cada estudiante seleccionado
     */
    private void registrarObservacion() {
        Curso cursoSeleccionado = (Curso) comboCursos.getSelectedItem();
        String texto = campoObservacion.getText().trim();
        List<Integer> idsEstudiantes = new ArrayList<>();

        for (JCheckBox check : checkboxesEstudiantes) {
            if (check.isSelected()) {
                idsEstudiantes.add(Integer.parseInt(check.getActionCommand()));
            }
        }

        if (cursoSeleccionado == null || idsEstudiantes.isEmpty() || texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un curso, al menos un estudiante y escribir una observación.",
                    "Campos obligatorios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean exito = ControladorBitacora.registrarObservacionPedagogica(
                docente.getIdUsuario(), cursoSeleccionado.getIdCurso(), idsEstudiantes, texto, LocalDate.now()
        );

        if (exito) {
            JOptionPane.showMessageDialog(this, "Observación registrada correctamente en la bitácora.",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            campoObservacion.setText("");
            for (JCheckBox check : checkboxesEstudiantes) check.setSelected(false);
            dispose(); // Cierra esta ventana

        } else {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al registrar la observación.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
