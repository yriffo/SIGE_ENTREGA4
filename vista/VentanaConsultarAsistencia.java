package vista;

import controlador.ControladorConsulta;
import modelo.AsistenciaDetalle;
import modelo.Curso;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Ventana para consultar asistencias por curso y fecha.
 * Puede ser utilizada por Docentes y Preceptores.
 * 
 * Usa ControladorConsulta para obtener los datos desde base de datos.
 * 
 * @author Yonatan
 */
public class VentanaConsultarAsistencia extends JFrame {

    private Usuario usuario;
    private JComboBox<String> comboCursos;
    private JTextField campoFecha;
    private JTextArea areaResultados;

    private List<Curso> cursos;
    private Curso cursoSeleccionado;

    private ControladorConsulta controladorConsulta;

    /**
     * Constructor de la ventana.
     * 
     * @param usuario Usuario que accede
     */
    public VentanaConsultarAsistencia(Usuario usuario) {
        this.usuario = usuario;
        this.controladorConsulta = new ControladorConsulta();

        setTitle("Consultar Asistencia");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de selección
        JPanel panelBusqueda = new JPanel(new GridLayout(3, 2, 5, 5));
        panelBusqueda.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        comboCursos = new JComboBox<>();
        cursos = controladorConsulta.obtenerCursos();
        for (Curso c : cursos) comboCursos.addItem(c.getNombre());

        campoFecha = new JTextField();
        campoFecha.setToolTipText("Formato: dd/MM/aaaa");

        JButton btnBuscar = new JButton("Consultar");
        btnBuscar.addActionListener(e -> consultarAsistencia());

        panelBusqueda.add(new JLabel("Curso:"));
        panelBusqueda.add(comboCursos);
        panelBusqueda.add(new JLabel("Fecha (dd/MM/aaaa):"));
        panelBusqueda.add(campoFecha);
        panelBusqueda.add(new JLabel(""));
        panelBusqueda.add(btnBuscar);

        add(panelBusqueda, BorderLayout.NORTH);

        // Área de resultados
        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(areaResultados);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultado"));
        add(scroll, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * Ejecuta la consulta y muestra el resultado.
     */
    private void consultarAsistencia() {
        int indexCurso = comboCursos.getSelectedIndex();
        if (indexCurso < 0 || indexCurso >= cursos.size()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un curso válido.");
            return;
        }

        cursoSeleccionado = cursos.get(indexCurso);
        String fechaTexto = campoFecha.getText().trim();
        if (fechaTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar una fecha.");
            return;
        }

        try {
            String[] partes = fechaTexto.split("/");
            if (partes.length != 3) throw new Exception("Formato incorrecto");
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int anio = Integer.parseInt(partes[2]);
            LocalDate fecha = LocalDate.of(anio, mes, dia);

            List<AsistenciaDetalle> detalles = controladorConsulta.obtenerAsistenciaPorCursoYFecha(cursoSeleccionado.getIdCurso(), fecha);

            if (detalles.isEmpty()) {
                areaResultados.setText("No se encontraron asistencias registradas para ese curso en esa fecha.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Curso: ").append(cursoSeleccionado.getNombre()).append("\n");
            sb.append("Fecha: ").append(fechaTexto).append("\n\n");

            for (AsistenciaDetalle det : detalles) {
                sb.append("Estudiante: ").append(det.getIdEstudiante())
                  .append(" - Estado: ").append(det.getEstado()).append("\n");
            }

            areaResultados.setText(sb.toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use dd/MM/aaaa", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
