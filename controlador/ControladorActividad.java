package controlador;

import conexion.conexionBD;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Controlador responsable de gestionar todas las operaciones
 * relacionadas con actividades académicas en el sistema SIGE.
 * Esta clase accede directamente a la base de datos para registrar y consultar actividades.
 * 
 * @author Yonatan
 */
public class ControladorActividad {

    /**
     * Registra una nueva actividad en la base de datos.
     * Este método será llamado desde la interfaz del rol Docente.
     *
     * @param idDocente ID del docente que crea la actividad
     * @param titulo Nombre o título de la actividad
     * @param tipo Tipo de calificación: "Numerica" o "Conceptual"
     * @param fecha Fecha de realización de la actividad
     * @param idCurso ID del curso al que pertenece la actividad
     * @param idMateria ID de la materia correspondiente
     */
    public void registrarActividad(int idDocente, String titulo, String tipo, LocalDate fecha, int idCurso, int idMateria) {
        String sql = "INSERT INTO planillacalificaciones (idMateria, idCurso, fecha, nombreActividad, tipo, idUsuario) " +
             "VALUES (?, ?, ?, ?, ?, ?)";


        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Se reemplazan los valores en la consulta preparada
            stmt.setInt(1, idMateria);
            stmt.setInt(2, idCurso);
            stmt.setDate(3, java.sql.Date.valueOf(fecha));
            stmt.setString(4, titulo);
            stmt.setString(5, tipo);  // tipo ("Numerica" o "Conceptual")
            stmt.setInt(6, idDocente);


            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("Actividad registrada exitosamente.");
            } else {
                System.out.println("No se pudo registrar la actividad.");
            }

        } catch (SQLException e) {
            System.err.println("Error al registrar la actividad:");
            System.err.println(e.getMessage());
        }
    }

        /**
     * Obtiene el listado de cursos y materias que tiene asignado un docente.
     * Utiliza la tabla  `docente_materia_curso`, que relaciona cursos, materias y el id del usuario con rol docente.
     * Devuelve un mapa con los nombres de cursos como claves y las materias correspondientes como listas de valores.
     *
     * @param idDocente ID del usuario con rol Docente (de la tabla usuario)
     * @return Mapa con cursos como clave y lista de materias como valor
     */
    public Map<String, List<String>> obtenerCursosYMateriasDelDocente(int idDocente) {
        Map<String, List<String>> mapa = new HashMap<>();

        // Consulta cursos y materias según el usuario
        String sql = "SELECT c.nombreCurso AS curso, m.nombre_materia AS materia " +
                     "FROM docente_materia_curso dmc " +
                     "JOIN usuarios_rol ur ON dmc.idUsuarioRol = ur.idUsuarioRol " +
                     "JOIN curso c ON dmc.idCurso = c.idCurso " +
                     "JOIN materia m ON dmc.idMateria = m.idMateria " +
                     "WHERE ur.idUsuario = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDocente);  // usa el id del usuario con rol docente
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String curso = rs.getString("curso");
                String materia = rs.getString("materia");

                // Si el curso no está en el mapa, se agrega con una lista vacía
                mapa.putIfAbsent(curso, new ArrayList<>());

                // Agrega la materia a la lista del curso correspondiente
                mapa.get(curso).add(materia);
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar cursos y materias del docente:");
        }

        return mapa;
    }

    
        /**
     * Devuelve el ID del curso a partir de su nombre, verificando en la tabla curso.
     *
     * @param nombreCurso Nombre textual del curso (por ejemplo, "1A")
     * @return ID del curso correspondiente, o -1 si no se encuentra
     */
    public int obtenerIdCursoPorNombre(String nombreCurso) {
        String sql = "SELECT idCurso FROM curso WHERE nombreCurso = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreCurso);  // Se busca por el nombre visible en el combo
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("idCurso");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ID del curso por nombre:");
            e.printStackTrace();
        }

        return -1; // Valor por defecto si no se encuentra
    }


        /**
     * Devuelve el ID de la materia a partir de su nombre, verificando en la tabla materia.
     *
     * @param nombreMateria Nombre textual de la materia (por ejemplo, "Matemática")
     * @return ID de la materia correspondiente, o -1 si no se encuentra
     */
    public int obtenerIdMateriaPorNombre(String nombreMateria) {
        String sql = "SELECT idMateria FROM materia WHERE nombre_materia = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreMateria);  // Se busca por el nombre que se ve en el combo
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("idMateria");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ID de la materia por nombre:");
            e.printStackTrace();
        }

        return -1; // Valor por defecto si no se encuentra
    }
    
     /**
      * Obtiene las actividades registradas por un docente autenticado.
      * Consulta la tabla planillacalificaciones .
      *
      * @param idDocente ID del usuario autenticado
      * @return Lista de actividades con curso, materia, nombre y fecha
      */
     public List<String[]> obtenerActividadesDelDocente(int idDocente) {
         List<String[]> lista = new ArrayList<>();

         String sql = """
             SELECT c.nombreCurso AS curso, m.nombre_materia AS materia,
                    p.nombreActividad, p.fecha
             FROM planillacalificaciones p
             JOIN curso c ON p.idCurso = c.idCurso
             JOIN materia m ON p.idMateria = m.idMateria
             WHERE p.idUsuario = ?
             ORDER BY p.fecha
         """;

         try (Connection conn = conexionBD.obtenerConexion();
              PreparedStatement stmt = conn.prepareStatement(sql)) {

             stmt.setInt(1, idDocente);
             ResultSet rs = stmt.executeQuery();

             DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

             while (rs.next()) {
                 String curso = rs.getString("curso");
                 String materia = rs.getString("materia");
                 String titulo = rs.getString("nombreActividad");
                 String fecha = rs.getDate("fecha") != null
                         ? rs.getDate("fecha").toLocalDate().format(formatter)
                         : "Sin fecha";

                 lista.add(new String[]{curso, materia, titulo, fecha});
             }

         } catch (Exception e) {
             System.err.println("Error al obtener actividades del docente:");
             e.printStackTrace();
         }

         return lista;
     }



}
