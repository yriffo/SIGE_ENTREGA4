package vista;

import controlador.ControladorBitacora;
import modelo.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ventana que muestra todas las observaciones pedagógicas registradas
 * en la bitácora para un estudiante determinado. Se utiliza dentro de la
 * ventana de bitácora por parte de Preceptores, Asesores o Directivos.
 *
 * Esta versión está organizada para verse clara, con tabla ordenada por fecha.
 * Solo se muestra si el estudiante tiene al menos una observación registrada.
 * Además, el nombre del docente se incluye al final de cada observación.
 * 
 * @author Yonatan
 */
public class SubventanaObservacionesEstudiante extends JFrame {

    private final Estudiante estudiante;
    private final JFrame ventanaAnterior;

    public SubventanaObservacionesEstudiante(Estudiante estudiante, JFrame ventanaAnterior) {
        this.estudiante = estudiante;
        this.ventanaAnterior = ventanaAnterior;

        // Obtenemos las observaciones desde el controlador
        List<String[]> observaciones = ControladorBitacora.obtenerObservacionesPorEstudiante(estudiante.getIdEstudiante());

        // Si no hay observaciones, se muestra un mensaje y no se abre la ventana
        if (observaciones.isEmpty()) {
            JOptionPane.showMessageDialog(ventanaAnterior,
                    "Este estudiante no tiene observaciones pedagógicas registradas.",
                    "Sin observaciones", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        setTitle("Observaciones Pedagógicas - " + estudiante.getApellido() + ", " + estudiante.getNombre());
        setSize(1100, 500); // Ampliamos el ancho para que se vea todo
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Título superior
        JLabel titulo = new JLabel("Observaciones registradas para: " + estudiante.getApellido() + ", " + estudiante.getNombre(), SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titulo, BorderLayout.NORTH);

        // Encabezados de la tabla
        String[] columnas = {"Fecha", "Curso", "Observación + Docente"};

        // Modelo de tabla para mostrar datos de forma no editable
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // La tabla solo es de lectura
            }
        };

        // Cargamos los datos, uniendo descripción con nombre del docente
        for (String[] fila : observaciones) {
            String fecha = fila[0];
            String docente = fila[1];
            String curso = fila[2];
            String descripcion = fila[3];

            String observacionFinal = descripcion + "\n— Docente: " + docente;
            modeloTabla.addRow(new String[]{fecha, curso, observacionFinal});
        }

        JTable tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Arial", Font.PLAIN, 16));
        tabla.setRowHeight(48);

        // Ajustar el ancho de columnas manualmente
        tabla.getColumnModel().getColumn(0).setPreferredWidth(100);  // Fecha
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150);  // Curso
        tabla.getColumnModel().getColumn(2).setPreferredWidth(800);  // Observación

        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        // Botón volver
        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 18));
        btnVolver.addActionListener(e -> dispose());

        JPanel panelInferior = new JPanel();
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelInferior.add(btnVolver);
        add(panelInferior, BorderLayout.SOUTH);

        setVisible(true);
    }
}
