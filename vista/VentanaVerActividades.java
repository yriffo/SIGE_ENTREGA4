package vista;

import controlador.ControladorActividad;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ventana que muestra al docente todas las actividades que ha registrado.
 * Incluye rediseño visual: pantalla completa, estilo SIGE unificado.
 * Se muestran curso, materia, título y fecha.
 * 
 * @author Yonatan
 */
public class VentanaVerActividades extends JFrame {

    private final Usuario docente;
    private final ControladorActividad controlador;

    public VentanaVerActividades(Usuario docente) {
        this.docente = docente;
        this.controlador = new ControladorActividad();

        setTitle("Actividades Registradas - SIGE");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // pantalla completa
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Obtener actividades registradas por el docente
        List<String[]> actividades = controlador.obtenerActividadesDelDocente(docente.getIdUsuario());

        // Si no hay actividades, mostrar mensaje y cerrar
        if (actividades.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron actividades registradas.",
                    "Sin actividades",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }

        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(30, 100, 10, 100));

        JLabel lblTitulo = new JLabel("Listado de Actividades Registradas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("A continuación se muestran las actividades que usted registró.");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        panelTitulo.add(lblTitulo);
        panelTitulo.add(lblSubtitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Configurar tabla
        String[] columnas = {"Curso", "Materia", "Título", "Fecha"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        for (String[] fila : actividades) {
            modelo.addRow(fila);
        }

        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Arial", Font.PLAIN, 16));
        tabla.setRowHeight(28);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        add(scroll, BorderLayout.CENTER);
        setVisible(true);
    }
}
