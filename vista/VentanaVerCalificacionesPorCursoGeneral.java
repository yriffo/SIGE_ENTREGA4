package vista;

import controlador.ControladorCalificacion;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

/**
 * Ventana que muestra la planilla de calificaciones de un curso y materia seleccionados.
 * Diseño visual actualizado. Lógica original intacta.
 * 
 * Cada fila representa un estudiante, y cada columna una actividad.
 * Los datos provienen de la base de datos a través del controlador.
 * 
 * @author Yonatan
 */
public class VentanaVerCalificacionesPorCursoGeneral extends JFrame {

    private final Usuario usuario;
    private final ControladorCalificacion controlador;

    private JComboBox<String> comboCurso;
    private JComboBox<String> comboMateria;
    private JTable tablaPlanilla;

    private Map<String, Integer> mapaCursos;
    private Map<String, Integer> mapaMaterias;
    private Map<String, Integer> mapaEstudiantes;
    private Map<String, Integer> mapaActividades;

    public VentanaVerCalificacionesPorCursoGeneral(Usuario usuario) {
        this.usuario = usuario;
        this.controlador = new ControladorCalificacion();

        setTitle("Ver Calificaciones por Curso y Materia");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // pantalla completa
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        inicializarComponentes();
        cargarCursos();
    }

    private void inicializarComponentes() {
        // Panel título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(30, 100, 10, 100));

        JLabel lblTitulo = new JLabel("Planilla de Calificaciones");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Seleccione un curso y una materia para ver las calificaciones.");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        panelTitulo.add(lblTitulo);
        panelTitulo.add(lblSubtitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel de filtros centrado
        JPanel panelFiltros = new JPanel();
        panelFiltros.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblCurso = new JLabel("Curso:");
        lblCurso.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_END;
        panelFiltros.add(lblCurso, gbc);

        comboCurso = new JComboBox<>();
        comboCurso.setFont(new Font("Arial", Font.PLAIN, 18));
        comboCurso.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START;
        panelFiltros.add(comboCurso, gbc);

        JLabel lblMateria = new JLabel("Materia:");
        lblMateria.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_END;
        panelFiltros.add(lblMateria, gbc);

        comboMateria = new JComboBox<>();
        comboMateria.setFont(new Font("Arial", Font.PLAIN, 18));
        comboMateria.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START;
        panelFiltros.add(comboMateria, gbc);

        JButton botonVer = new JButton("Ver Calificaciones");
        botonVer.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelFiltros.add(botonVer, gbc);

        add(panelFiltros, BorderLayout.WEST);

        // Tabla
        tablaPlanilla = new JTable();
        tablaPlanilla.setFont(new Font("Arial", Font.PLAIN, 16));
        tablaPlanilla.setRowHeight(28);
        tablaPlanilla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        JScrollPane scroll = new JScrollPane(tablaPlanilla);
        scroll.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        add(scroll, BorderLayout.CENTER);

        // Listeners
        botonVer.addActionListener(e -> cargarPlanilla());
        comboCurso.addActionListener(e -> cargarMaterias());
    }

    private void cargarCursos() {
        mapaCursos = controlador.obtenerCursosPorDocente(usuario.getIdUsuario());
        comboCurso.removeAllItems();
        for (String nombre : mapaCursos.keySet()) {
            comboCurso.addItem(nombre);
        }
    }

    private void cargarMaterias() {
        String cursoSeleccionado = (String) comboCurso.getSelectedItem();
        if (cursoSeleccionado == null) return;

        int idCurso = mapaCursos.get(cursoSeleccionado);
        mapaMaterias = controlador.obtenerMateriasPorCursoYDocente(usuario.getIdUsuario(), idCurso);
        comboMateria.removeAllItems();
        for (String nombre : mapaMaterias.keySet()) {
            comboMateria.addItem(nombre);
        }
    }

    private void cargarPlanilla() {
        String curso = (String) comboCurso.getSelectedItem();
        String materia = (String) comboMateria.getSelectedItem();

        if (curso == null || materia == null) return;

        int idCurso = mapaCursos.get(curso);
        int idMateria = mapaMaterias.get(materia);

        mapaEstudiantes = controlador.obtenerEstudiantesDelCurso(idCurso);
        mapaActividades = controlador.obtenerActividades(usuario.getIdUsuario(), idCurso, idMateria);

        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Estudiante");

        for (String act : mapaActividades.keySet()) {
            modelo.addColumn(act);
        }

        for (Map.Entry<String, Integer> entradaEst : mapaEstudiantes.entrySet()) {
            String nombreEst = entradaEst.getKey();
            int idEst = entradaEst.getValue();
            Vector<String> fila = new Vector<>();
            fila.add(nombreEst);

            for (int idAct : mapaActividades.values()) {
                String nota = controlador.obtenerNotaPorActividad(idEst, idAct);
                fila.add(nota != null ? nota : "-");
            }

            modelo.addRow(fila);
        }

        tablaPlanilla.setModel(modelo);
    }
}
