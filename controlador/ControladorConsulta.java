package controlador;

import conexion.conexionBD;
import modelo.*;
import java.time.LocalDate;
import java.sql.*;
import java.util.*;
import java.sql.Date;
/**
 * Controlador responsable de todas las consultas de información dentro del sistema SIGE.
 * Se conecta a la base de datos para obtener cursos, estudiantes, materias, usuarios y notas.
 * Este controlador es utilizado por múltiples vistas del sistema que requieren mostrar datos.
 * 
 * @author Yonatan
 */
public class ControladorConsulta {

    // ===================== CONSULTA DE CURSOS =====================

    /**
     * Obtiene todos los cursos existentes en la base de datos.
     * Cada curso incluye su ID, nombre (por ejemplo, "1A") y año lectivo.
     * 
     * @return Lista de objetos Curso encontrados en la BD.
     */
    public List<Curso> obtenerCursos() {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT idCurso, nombreCurso, anio FROM curso";

        try (Connection conn = conexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("idCurso");
                String nombre = rs.getString("nombreCurso");
                int anio = rs.getInt("anio");
                cursos.add(new Curso(id, nombre, anio));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener cursos: " + e.getMessage());
        }

        return cursos;
    }

            /**
     * Busca un curso específico en la base de datos a partir de su ID.
     * Retorna un objeto Curso con su nombre y año lectivo.
     * 
     * @param idCurso ID del curso a buscar
     * @return Objeto Curso encontrado o null si no existe
     */
    public Curso buscarCursoPorId(int idCurso) {
        // Sentencia SQL: buscamos nombreCurso y anio según el ID
        String sql = "SELECT nombreCurso, anio FROM curso WHERE idCurso = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);  // Establecemos el parámetro ID en la consulta
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Si encontramos un resultado, creamos y devolvemos el objeto Curso
                return new Curso(idCurso, rs.getString("nombreCurso"), rs.getInt("anio"));
            }

        } catch (SQLException e) {
            // En caso de error, lo mostramos en consola
            System.err.println("Error al buscar curso: " + e.getMessage());
        }

        return null; // Si no se encuentra nada, retornamos null
    }


            /**
     * Obtiene todos los cursos en los que el docente (por idUsuario) tiene asignadas materias.
     * Este método consulta la tabla docente_materia_curso, que relaciona docente, materia y curso.
     * Utiliza también usuarios_rol para filtrar solo roles de tipo Docente.
     *
     * @param idUsuario ID del docente logueado
     * @return Lista de cursos donde el docente dicta al menos una materia
     */
    public List<Curso> obtenerCursosPorDocente(int idUsuario) {
        List<Curso> cursos = new ArrayList<>();
        String sql = """
            SELECT DISTINCT c.idCurso, c.nombreCurso, c.anio
            FROM curso c
            JOIN docente_materia_curso dmc ON c.idCurso = dmc.idCurso
            JOIN usuarios_rol ur ON dmc.idUsuarioRol = ur.idUsuarioRol
            WHERE ur.idUsuario = ? AND ur.idRol = 1
            ORDER BY c.nombreCurso
        """;

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);  // Se filtra por el ID del docente actual
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Se instancia un objeto Curso con los datos obtenidos
                cursos.add(new Curso(
                    rs.getInt("idCurso"),
                    rs.getString("nombreCurso"),
                    rs.getInt("anio")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cursos por docente: " + e.getMessage());
        }

        return cursos;
    }



    // ===================== CONSULTA DE ESTUDIANTES =====================

    /**
     * Obtiene todos los estudiantes pertenecientes a un curso específico.
     * 
     * @param idCurso ID del curso al que pertenecen los estudiantes.
     * @return Lista de objetos Estudiante.
     */
    public List<Estudiante> obtenerEstudiantesPorCurso(int idCurso) {
        List<Estudiante> estudiantes = new ArrayList<>();
        String sql = "SELECT idEstudiante, nombre, apellido, dni, telefono, responsable FROM estudiante WHERE idCurso = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                estudiantes.add(new Estudiante(
                        rs.getInt("idEstudiante"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("telefono"),
                        rs.getString("responsable"),
                        idCurso
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener estudiantes: " + e.getMessage());
        }

        return estudiantes;
    }

    /**
     * Busca un estudiante por su ID único.
     * 
     * @param idEstudiante ID del estudiante.
     * @return Objeto Estudiante encontrado, o null si no existe.
     */
    public Estudiante buscarEstudiantePorId(int idEstudiante) {
        String sql = "SELECT nombre, apellido, dni, telefono, responsable, idCurso FROM estudiante WHERE idEstudiante = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEstudiante);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Estudiante(
                        idEstudiante,
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("telefono"),
                        rs.getString("responsable"),
                        rs.getInt("idCurso")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar estudiante: " + e.getMessage());
        }

        return null;
    }

    // ===================== CONSULTA DE MATERIAS =====================

        /**
     * Busca una materia en la base de datos a partir de su ID.
     * Retorna un objeto Materia con su nombre correspondiente.
     * 
     * @param idMateria ID de la materia a buscar
     * @return Objeto Materia encontrado o null si no existe
     */
    public Materia buscarMateriaPorId(int idMateria) {
        // Sentencia SQL usando el nombre real del campo en la tabla
        String sql = "SELECT nombre_materia FROM materia WHERE idMateria = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMateria);  // Cargamos el parámetro ID en la consulta
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Creamos el objeto Materia con el resultado obtenido
                return new Materia(idMateria, rs.getString("nombre_materia"));
            }

        } catch (SQLException e) {
            // Mostramos error si algo falla
            System.err.println("Error al buscar materia: " + e.getMessage());
        }

        return null; // Si no se encuentra ninguna materia, devolvemos null
    }

    
            /**
     * Obtiene las materias asignadas al docente en un curso específico.
     * La relación se toma desde docente_materia_curso, con validación de rol Docente.
     *
     * @param idUsuario ID del docente logueado
     * @param idCurso ID del curso seleccionado en la interfaz
     * @return Lista de materias que el docente dicta en ese curso
     */
    public List<Materia> obtenerMateriasPorDocenteYCurso(int idUsuario, int idCurso) {
        List<Materia> materias = new ArrayList<>();
        String sql = """
            SELECT DISTINCT m.idMateria, m.nombre_materia
            FROM materia m
            JOIN docente_materia_curso dmc ON m.idMateria = dmc.idMateria
            JOIN usuarios_rol ur ON dmc.idUsuarioRol = ur.idUsuarioRol
            WHERE ur.idUsuario = ? AND ur.idRol = 1 AND dmc.idCurso = ?
            ORDER BY m.nombre_materia
        """;

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);  // ID del docente
            stmt.setInt(2, idCurso);    // ID del curso actual
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Se crea la materia correspondiente al resultado
                materias.add(new Materia(
                    rs.getInt("idMateria"),
                    rs.getString("nombre_materia")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener materias del docente en el curso: " + e.getMessage());
        }

        return materias;
    }


        

    // ===================== CONSULTA DE USUARIOS =====================

    /**
     * Busca un usuario (por ejemplo, docente, asesor, directivo) por su ID.
     * 
     * @param idUsuario ID del usuario.
     * @return Objeto Usuario con nombre, apellido y email.
     */
    public Usuario buscarUsuarioPorId(int idUsuario) {
        String sql = "SELECT nombre, apellido, email FROM usuario WHERE idUsuario = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Usuario(idUsuario, rs.getString("nombre"), rs.getString("apellido"), rs.getString("email"));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }

        return null;
    }

    // ===================== CONSULTA DE CALIFICACIONES =====================

    /**
     * Devuelve la nota registrada para un estudiante en una actividad determinada.
     * Si no hay calificación, se devuelve "—".
     * 
     * @param idActividad ID de la actividad evaluativa.
     * @param idEstudiante ID del estudiante.
     * @return Nota como String, o "—" si no existe.
     */
    public String obtenerNota(int idActividad, int idEstudiante) {
        String sql = "SELECT nota FROM calificacion WHERE idActividad = ? AND idEstudiante = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idActividad);
            stmt.setInt(2, idEstudiante);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("nota");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener nota: " + e.getMessage());
        }

        return "—";
    }
    
        /**
     * Obtiene todas las actividades del sistema registradas en la base de datos.
     * 
     * @return Lista de actividades disponibles
     */
    public List<Actividad> obtenerTodasLasActividades() {
        List<Actividad> actividades = new ArrayList<>();
        String sql = "SELECT idActividad, titulo, tipo, fecha, idCurso, idMateria FROM actividad ORDER BY fecha DESC";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Actividad act = new Actividad(
                    rs.getInt("idActividad"),
                    rs.getString("titulo"),
                    rs.getString("tipo"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getInt("idCurso"),
                    rs.getInt("idMateria")
                );
                actividades.add(act);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener actividades: " + e.getMessage());
        }

        return actividades;
    }

        /**
     * Devuelve todas las calificaciones registradas para un estudiante,
     * organizadas por materia y ordenadas por fecha.
     * Se usa en la vista de bitácora para Preceptor, Asesor y Directivo.
     *
     * @param idEstudiante ID del estudiante a consultar
     * @return Lista de String[] con columnas: Materia, Fecha, Actividad, Calificación
     */
    public List<String[]> obtenerCalificacionesPorEstudiante(int idEstudiante) {
        List<String[]> resultados = new ArrayList<>();

        String sql = """
            SELECT 
                m.nombre_materia AS Materia,
                p.fecha AS Fecha,
                p.nombreActividad AS Actividad,
                c.nota AS Calificacion
            FROM calificacion c
            JOIN planillacalificaciones p ON c.idPlanilla = p.idPlanilla
            JOIN materia m ON p.idMateria = m.idMateria
            WHERE c.idEstudiante = ?
            ORDER BY m.nombre_materia, p.fecha
        """;

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String materia = rs.getString("Materia");
                Date fechaSql = rs.getDate("Fecha");
                String fecha = (fechaSql != null) ? fechaSql.toLocalDate().toString() : "—";// formato yyyy-MM-dd
                String actividad = rs.getString("Actividad");
                String nota = rs.getString("Calificacion");

                resultados.add(new String[]{materia, fecha, actividad, nota});
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener calificaciones del estudiante: " + e.getMessage());
        }

        return resultados;
    }

    
     // ===================== CONSULTA DE ASISTENCIAS =====================
    
        /**
     * Devuelve los registros de asistencia de un curso en una fecha específica.
     * 
     * @param idCurso ID del curso
     * @param fecha Fecha de asistencia
     * @return Lista de detalles de asistencia
     */
    public List<AsistenciaDetalle> obtenerAsistenciaPorCursoYFecha(int idCurso, LocalDate fecha) {
        List<AsistenciaDetalle> detalles = new ArrayList<>();

        String sql = """
            SELECT ad.idEstudiante, ad.estado
            FROM asistencia a
            JOIN asistenciadetalle ad ON a.idAsistencia = ad.idAsistencia
            WHERE a.idCurso = ? AND a.fecha = ?
        """;

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCurso);
            ps.setDate(2, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                AsistenciaDetalle detalle = new AsistenciaDetalle();
                detalle.setIdEstudiante(rs.getInt("idEstudiante"));
                detalle.setEstado(rs.getString("estado"));
                detalles.add(detalle);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener asistencia por curso y fecha: " + e.getMessage());
        }

        return detalles;
    }
    
        /**
     * Devuelve todas las asistencias del estudiante que NO fueron "Presente".
     * Incluye asistencias generales (tomadas por Preceptor) y por materia (Docente).
     *
     * @param idEstudiante ID del estudiante a consultar
     * @return Lista de String[] con columnas: Fecha, Curso, Materia (si aplica), Estado
     */
    public List<String[]> obtenerAsistenciasNoPresentesPorEstudiante(int idEstudiante) {
        List<String[]> resultados = new ArrayList<>();

        String sql = """
            SELECT 
                a.fecha,
                c.nombreCurso,
                m.nombre_materia,
                ad.estado
            FROM asistencia a
            JOIN asistenciadetalle ad ON a.idAsistencia = ad.idAsistencia
            JOIN curso c ON a.idCurso = c.idCurso
            LEFT JOIN materia m ON a.idMateria = m.idMateria
            WHERE ad.idEstudiante = ?
            AND ad.estado != 'Presente'
            ORDER BY a.fecha
        """;

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String fecha = rs.getDate("fecha").toLocalDate().toString();
                String curso = rs.getString("nombreCurso");
                String materia = rs.getString("nombre_materia"); // puede ser null
                String estado = rs.getString("estado");

                // Si es asistencia general, dejamos materia vacía
                if (materia == null) materia = "—";

                resultados.add(new String[]{fecha, curso, materia, estado});
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener asistencias no presentes del estudiante: " + e.getMessage());
        }

        return resultados;
    }

}
