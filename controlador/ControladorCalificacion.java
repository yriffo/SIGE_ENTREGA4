package controlador;

import conexion.conexionBD;

import java.sql.*;
import java.util.*;


/**
 * Controlador que maneja todas las operaciones relacionadas
 * con la carga de calificaciones por parte del Docente.
 * Esta clase accede a la base de datos real del sistema SIGE
 * y los métodos para ser utilizados por la interfaz.
 * 
 * @author Yonatan
 */
public class ControladorCalificacion implements modelo.RegistrableEnBitacora {

            /**
      * Devuelve todos los cursos en los que el docente tiene asignada al menos una materia.
      * La relación se obtiene a través de la tabla docente_materia, vinculada por usuarios_rol.
      *
      * @param idDocente ID del usuario con rol Docente (de la tabla usuario)
      * @return Mapa con nombre del curso como clave y su ID como valor (nombreCurso → idCurso)
      */
     public Map<String, Integer> obtenerCursosPorDocente(int idDocente) {
         Map<String, Integer> cursos = new LinkedHashMap<>();

         // Consulta que une las tablas curso, docente_materia y usuarios_rol para obtener los cursos asignados
         String sql = """
             SELECT DISTINCT c.idCurso, c.nombreCurso
             FROM curso c
             JOIN docente_materia_curso dmc ON c.idCurso = dmc.idCurso
             JOIN usuarios_rol ur ON dmc.idUsuarioRol = ur.idUsuarioRol
             WHERE ur.idUsuario = ?
             ORDER BY c.nombreCurso
         """;

         try (Connection conn = conexionBD.obtenerConexion();
              PreparedStatement stmt = conn.prepareStatement(sql)) {

             stmt.setInt(1, idDocente); // Reemplaza el parámetro con el ID del docente
             ResultSet rs = stmt.executeQuery();

             while (rs.next()) {
                 int idCurso = rs.getInt("idCurso");
                 String nombreCurso = rs.getString("nombreCurso");
                 cursos.put(nombreCurso, idCurso); // Mapea el nombre del curso al ID
             }

         } catch (SQLException e) {
             System.out.println("Error al obtener cursos por docente: " + e.getMessage());
         }

         return cursos;
     }


                /**
     * Devuelve las materias que dicta un docente en un curso específico.
     * Usa la tabla docente_materia_curso para garantizar coincidencias con la base.
     *
     * @param idDocente ID del usuario con rol Docente
     * @param idCurso ID del curso seleccionado
     * @return Mapa de nombre de materia → ID de materia
     */
    public Map<String, Integer> obtenerMateriasPorCursoYDocente(int idDocente, int idCurso) {
        Map<String, Integer> materias = new LinkedHashMap<>();

        String sql = """
            SELECT DISTINCT m.idMateria, m.nombre_materia
            FROM materia m
            JOIN docente_materia_curso dmc ON m.idMateria = dmc.idMateria
            JOIN usuarios_rol ur ON dmc.idUsuarioRol = ur.idUsuarioRol
            WHERE ur.idUsuario = ? AND dmc.idCurso = ?
            ORDER BY m.nombre_materia
        """;

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDocente);
            stmt.setInt(2, idCurso);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idMateria = rs.getInt("idMateria");
                String nombre = rs.getString("nombre_materia");
                materias.put(nombre, idMateria);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener materias por curso y docente: " + e.getMessage());
        }

        return materias;
    }


        /**
         * Devuelve las actividades registradas por un docente para un curso y una materia.
         * Utiliza la tabla planillacalificaciones y devuelve el nombre con fecha como clave.
         *
         * @param idDocente ID del usuario con rol Docente
         * @param idCurso ID del curso
         * @param idMateria ID de la materia
         * @return Mapa de "nombre (fecha)" → ID de actividad (idPlanilla)
         */
        public Map<String, Integer> obtenerActividades(int idDocente, int idCurso, int idMateria) {
            Map<String, Integer> actividades = new LinkedHashMap<>();

            String sql = """
                SELECT idPlanilla, nombreActividad, fecha
                FROM planillacalificaciones
                WHERE idUsuario = ? AND idCurso = ? AND idMateria = ?
                ORDER BY fecha
            """;

            try (Connection conn = conexionBD.obtenerConexion();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, idDocente);
                stmt.setInt(2, idCurso);
                stmt.setInt(3, idMateria);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("idPlanilla");
                    String nombre = rs.getString("nombreActividad");
                    java.sql.Date fecha = rs.getDate("fecha");

                    String clave = nombre + " (" + fecha.toLocalDate().toString() + ")";
                    actividades.put(clave, id);
                }

            } catch (SQLException e) {
                System.err.println("Error al obtener actividades: " + e.getMessage());
            }

            return actividades;
        }
        
            /**
     * Alias del método obtenerActividades para compatibilidad con vistas que lo requieren con otro nombre.
     * Devuelve las actividades registradas por un docente para un curso y una materia.
     *
     * @param idDocente ID del docente
     * @param idCurso ID del curso
     * @param idMateria ID de la materia
     * @return Mapa de nombre (fecha) → ID de actividad (idPlanilla)
     */
    public Map<String, Integer> obtenerActividadesPorDocenteYMateria(int idDocente, int idCurso, int idMateria) {
        return obtenerActividades(idDocente, idCurso, idMateria);
    }


            /**
     * Devuelve el tipo de calificación "Numerica" o "Conceptual" de una actividad específica.
     *
     * @param idActividad ID de la actividad  en planillacalificaciones
     * @return El tipo de calificación como texto
     */
    public String obtenerTipoDeActividad(int idActividad) {
        String tipo = "Numerica"; // Por defecto

        String sql = "SELECT tipo FROM planillacalificaciones WHERE idPlanilla = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idActividad);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                tipo = rs.getString("tipo");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tipo de actividad: " + e.getMessage());
        }

        return tipo;
    }


    
            /**
     * Devuelve la última calificación registrada de un estudiante en una materia.
     * Busca en planillacalificaciones + calificacion usando idEstudiante + idMateria.
     *
     * @param idEstudiante ID del estudiante
     * @param idMateria ID de la materia
     * @return Nota registrada como texto, o "Sin calificación"
     */
    public String obtenerNota(int idEstudiante, int idMateria) {
        String sql = """
            SELECT c.nota
            FROM calificacion c
            JOIN planillacalificaciones p ON c.idPlanilla = p.idPlanilla
            WHERE c.idEstudiante = ? AND p.idMateria = ?
            ORDER BY p.fecha DESC
            LIMIT 1
        """;

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ps.setInt(2, idMateria);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("nota");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener nota: " + e.getMessage());
        }

        return "Sin calificación";
    }


    /**
     * Obtiene el listado de estudiantes inscriptos en un curso.
     *
     * @param idCurso ID del curso
     * @return Mapa de nombreCompleto -> idEstudiante
     */
    public Map<String, Integer> obtenerEstudiantesDelCurso(int idCurso) {
        Map<String, Integer> estudiantes = new LinkedHashMap<>();

        String sql = "SELECT idEstudiante, nombre, apellido " +
                     "FROM estudiante WHERE idCurso = ? ORDER BY apellido, nombre";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String nombreCompleto = rs.getString("apellido") + ", " + rs.getString("nombre");
                estudiantes.put(nombreCompleto, rs.getInt("idEstudiante"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener estudiantes: " + e.getMessage());
        }

        return estudiantes;
    }

        /**
     * Guarda o actualiza la calificación de un estudiante para una actividad (planilla) específica.
     * Utiliza el campo correcto idPlanilla de la tabla calificacion.
     *
     * @param idPlanilla ID de la actividad (planilla de calificación)
     * @param idEstudiante ID del estudiante
     * @param nota Nota como texto ("NE", "7", "Bien", etc.)
     */
    public void guardarCalificacion(int idPlanilla, int idEstudiante, String nota) {
        String sqlBuscar = "SELECT * FROM calificacion WHERE idPlanilla = ? AND idEstudiante = ?";
        String sqlInsertar = "INSERT INTO calificacion (idPlanilla, idEstudiante, nota) VALUES (?, ?, ?)";
        String sqlActualizar = "UPDATE calificacion SET nota = ? WHERE idPlanilla = ? AND idEstudiante = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmtBuscar = conn.prepareStatement(sqlBuscar)) {

            stmtBuscar.setInt(1, idPlanilla);
            stmtBuscar.setInt(2, idEstudiante);

            ResultSet rs = stmtBuscar.executeQuery();

            if (rs.next()) {
                // Ya existe → actualizar
                try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlActualizar)) {
                    stmtUpdate.setString(1, nota);
                    stmtUpdate.setInt(2, idPlanilla);
                    stmtUpdate.setInt(3, idEstudiante);
                    stmtUpdate.executeUpdate();
                }

            } else {
                // No existe → insertar nueva
                try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsertar)) {
                    stmtInsert.setInt(1, idPlanilla);
                    stmtInsert.setInt(2, idEstudiante);
                    stmtInsert.setString(3, nota);
                    stmtInsert.executeUpdate();
                }
            }
        
        // Registrar en bitácora
        String descripcion = "Se registró la calificación '" + nota + "' en la actividad ID " + idPlanilla;
        registrarEnBitacora(idEstudiante, descripcion);    
            
        } catch (SQLException e) {
            System.err.println("Error al guardar calificación: " + e.getMessage());
        }
    }

    
                    /**
        * Devuelve la calificación registrada de un estudiante en una actividad específica.
        * Usa la tabla  calificacion con idPlanilla como identificador.
        *
        * @param idEstudiante ID del estudiante
        * @param idPlanilla ID de la actividad
        * @return Nota registrada o "Sin calificación"
        */
       public String obtenerNotaPorActividad(int idEstudiante, int idPlanilla) {
           String sql = "SELECT nota FROM calificacion WHERE idEstudiante = ? AND idPlanilla = ?";

           try (Connection conn = conexionBD.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

               stmt.setInt(1, idEstudiante);
               stmt.setInt(2, idPlanilla);
               ResultSet rs = stmt.executeQuery();

               if (rs.next()) {
                   return rs.getString("nota");
               }

           } catch (SQLException e) {
               System.err.println("Error al obtener nota por actividad: " + e.getMessage());
           }

           return "Sin calificación";
       }

            /**
      * Registra un evento en la bitácora del estudiante con la descripción proporcionada.
      * Se usa para dejar trazabilidad cuando se registra una calificación.
      *
      * @param idEstudiante ID del estudiante involucrado
      * @param descripcion  Descripción del evento (ej. "Se calificó la actividad 'X'")
      */
     @Override
     public void registrarEnBitacora(int idEstudiante, String descripcion) {
         String sql = "INSERT INTO bitacora (idEstudiante, fecha, descripcion) VALUES (?, NOW(), ?)";

         try (Connection conn = conexionBD.obtenerConexion();
              PreparedStatement stmt = conn.prepareStatement(sql)) {

             stmt.setInt(1, idEstudiante);
             stmt.setString(2, descripcion);
             stmt.executeUpdate();

         } catch (SQLException e) {
             System.err.println("Error al registrar en bitácora: " + e.getMessage());
         }
     }
 


}
