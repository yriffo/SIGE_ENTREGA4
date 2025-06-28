package vista;

import controlador.ControladorBitacora;
import controlador.ControladorConsulta;
import modelo.Curso;
import modelo.Estudiante;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ventana que permite al Docente consultar sus propias observaciones pedagógicas
 * registradas sobre estudiantes de sus cursos asignados.
 * Solo se muestran observaciones que él mismo haya generado.
 * 
 * Esta versión utiliza lógica de filtrado por curso y estudiante.
 * Se integra al menú del Docente como funcionalidad complementaria al CU10.
 *
 * @author Yonatan
 */
public class VentanaVerMisObservacionesDocente extends JFrame {

    private final Usuario docente;
    private final ControladorConsulta controladorConsulta;

    private JComboBox<Curso> comboCurso;
    private JComboBox<Estudiante> comboEstudiante;
    private DefaultTableModel modeloTabla;

    public VentanaVerMisObservacionesDocente(Usuario docente) {
        this.docente = docente;
        this.controladorConsulta = new ControladorConsulta();

        setTitle("Mis Observaciones Registradas");
        setSize(1100, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Título
        JLabel titulo = new JLabel("Consultar observaciones pedagógicas registradas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titulo, BorderLayout.NORTH);

        // Panel superior con combos
        JPanel panelFiltros = new JPanel(new FlowLayout());

        comboCurso = new JComboBox<>();
        for (Curso c : controladorConsulta.obtenerCursosPorDocente(docente.getIdUsuario())) {
            comboCurso.addItem(c);
        }
        comboCurso.addActionListener(e -> cargarEstudiantes());

        comboEstudiante = new JComboBox<>();

        JButton btnBuscar = new JButton("Buscar observaciones");
        btnBuscar.addActionListener(e -> cargarObservaciones());

        panelFiltros.add(new JLabel("Curso:"));
        panelFiltros.add(comboCurso);
        panelFiltros.add(new JLabel("Estudiante:"));
        panelFiltros.add(comboEstudiante);
        panelFiltros.add(btnBuscar);

        add(panelFiltros, BorderLayout.NORTH);

        // Tabla de resultados
        String[] columnas = {"Fecha", "Curso", "Observación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Arial", Font.PLAIN, 16));
        tabla.setRowHeight(48);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(800);

        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        setVisible(true);

        if (comboCurso.getItemCount() > 0) {
            comboCurso.setSelectedIndex(0);
            cargarEstudiantes();
        }
    }

    /**
     * Carga los estudiantes según el curso seleccionado
     */
    private void cargarEstudiantes() {
        comboEstudiante.removeAllItems();
        Curso curso = (Curso) comboCurso.getSelectedItem();
        if (curso != null) {
            List<Estudiante> lista = controladorConsulta.obtenerEstudiantesPorCurso(curso.getIdCurso());
            for (Estudiante e : lista) {
                comboEstudiante.addItem(e);
            }
        }
    }

    /**
     * Carga las observaciones registradas por el docente para el estudiante seleccionado
     */
    private void cargarObservaciones() {
        modeloTabla.setRowCount(0);
        Curso curso = (Curso) comboCurso.getSelectedItem();
        Estudiante estudiante = (Estudiante) comboEstudiante.getSelectedItem();

        if (curso == null || estudiante == null) return;

        List<String[]> resultados = ControladorBitacora.obtenerObservacionesDelDocente(
                docente.getIdUsuario(), estudiante.getIdEstudiante(), curso.getIdCurso()
        );

        for (String[] fila : resultados) {
            modeloTabla.addRow(fila);
        }

        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron observaciones registradas por usted para este estudiante.",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
