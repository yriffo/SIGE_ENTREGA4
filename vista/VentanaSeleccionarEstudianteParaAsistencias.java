package vista;

import modelo.Curso;
import modelo.Estudiante;
import controlador.ControladorConsulta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Ventana que permite al Asesor o Directivo seleccionar un estudiante
 * para consultar sus asistencias. Se accede desde la bitácora o desde opciones del menú.
 * Usa JComboBox para mostrar los estudiantes y mapa para recuperar el ID correspondiente.
 * Una vez seleccionado, abre la SubventanaAsistenciasEstudiante con el objeto real desde la base de datos.
 * 
 * @author Yonatan
 */
public class VentanaSeleccionarEstudianteParaAsistencias extends JFrame {

    private JComboBox<String> comboEstudiante;
    private JButton botonVer;
    private JButton botonVolver;

    private List<Estudiante> estudiantes;
    private Map<String, Integer> mapaEstudiantes; // Mapea la cadena visible a ID real
    private JFrame ventanaAnterior;

    private ControladorConsulta controladorConsulta; // Controlador que accede a la BD

    /**
     * Constructor de la ventana
     * 
     * @param ventanaAnterior ventana desde donde se llamó esta (para volver)
     * @param estudiantes lista de estudiantes para mostrar en el combo
     */
    public VentanaSeleccionarEstudianteParaAsistencias(JFrame ventanaAnterior, List<Estudiante> estudiantes) {
        this.ventanaAnterior = ventanaAnterior;
        this.estudiantes = estudiantes;
        this.mapaEstudiantes = new HashMap<>();
        this.controladorConsulta = new ControladorConsulta(); // Instanciamos el controlador que accede a BD

        setTitle("Seleccionar Estudiante");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        inicializarComponentes();
    }

    /**
     * Crea y organiza los componentes visuales
     */
    private void inicializarComponentes() {
        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));
        JPanel panelBotones = new JPanel();

        comboEstudiante = new JComboBox<>();
        cargarEstudiantesEnCombo(); // Llena el combo y el mapa

        botonVer = new JButton("Ver Asistencias");
        botonVolver = new JButton("Volver");

        // Acción al hacer clic en “Ver Asistencias”
        botonVer.addActionListener((ActionEvent e) -> abrirSubventana());

        // Acción al hacer clic en “Volver”
        botonVolver.addActionListener(e -> {
            ventanaAnterior.setVisible(true);
            dispose();
        });

        panelCentral.add(new JLabel("Seleccione un estudiante:"));
        panelCentral.add(comboEstudiante);

        panelBotones.add(botonVer);
        panelBotones.add(botonVolver);

        add(panelCentral, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Carga los estudiantes en el combo y construye el mapa nombre → ID
     */
    private void cargarEstudiantesEnCombo() {
        for (Estudiante est : estudiantes) {
            String clave = est.getApellido() + ", " + est.getNombre();
            comboEstudiante.addItem(clave);
            mapaEstudiantes.put(clave, est.getIdEstudiante()); // Se usa luego para recuperar el objeto
        }
    }

    /**
     * Al seleccionar un estudiante, se recupera su ID,
     * se obtiene el objeto completo desde la base de datos y
     * se abre la subventana con sus asistencias.
     */
    private void abrirSubventana() {
        String estudianteSeleccionado = (String) comboEstudiante.getSelectedItem();
        if (estudianteSeleccionado == null) return;

        Integer idEstudiante = mapaEstudiantes.get(estudianteSeleccionado);
        if (idEstudiante == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el ID del estudiante.");
            return;
        }

        // Obtenemos el estudiante real desde la base usando el controlador
        Estudiante estudiante = controladorConsulta.buscarEstudiantePorId(idEstudiante);
        if (estudiante != null) {
            new SubventanaAsistenciasEstudiante(estudiante, this).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el estudiante en la base de datos.");
        }
    }
}
