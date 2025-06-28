package controlador;

import conexion.conexionBD;
import modelo.Bitacora;
import modelo.Estudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;



/**
 * Controlador encargado de gestionar la bitácora del sistema SIGE.
 * Permite consultar eventos académicos y administrativos relacionados
 * con los estudiantes, como asistencias, calificaciones y reuniones.
 * 
 * Utilizado por roles (Preceptor, Asesor, Directivo).
 * 
 * @author Yonatan
 */
public class ControladorBitacora {

    /**
     * Obtiene la lista completa de eventos en la bitácora,
     * ordenada por fecha descendente (más recientes primero).
     * 
     * @return Lista de objetos Bitacora
     */
    public List<Bitacora> obtenerBitacoraGeneral() {
        List<Bitacora> eventos = new ArrayList<>();
        String sql = "SELECT idBitacora, idEstudiante, idCurso, idMateria, idDocente, fecha, motivo " +
                     "FROM bitacora ORDER BY fecha DESC";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Bitacora evento = new Bitacora();
                evento.setIdBitacora(rs.getInt("idBitacora"));
                evento.setIdEstudiante(rs.getInt("idEstudiante"));
                evento.setIdCurso(rs.getInt("idCurso"));
                evento.setIdMateria(rs.getInt("idMateria"));
                evento.setIdDocente(rs.getInt("idDocente"));
                evento.setFecha(rs.getDate("fecha").toLocalDate());
                evento.setMotivo(rs.getString("motivo"));
                eventos.add(evento);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la bitácora general: " + e.getMessage());
        }

        return eventos;
    }

    /**
     * Obtiene los eventos de la bitácora que corresponden a un estudiante específico.
     * Ordena los eventos desde los más recientes hasta los más antiguos.
     * 
     * @param idEstudiante Identificador del estudiante
     * @return Lista de objetos Bitacora correspondientes al estudiante
     */
    public List<Bitacora> obtenerBitacoraPorEstudiante(int idEstudiante) {
        List<Bitacora> eventos = new ArrayList<>();
        String sql = "SELECT idBitacora, idEstudiante, idCurso, idMateria, idDocente, fecha, motivo " +
                     "FROM bitacora WHERE idEstudiante = ? ORDER BY fecha DESC";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bitacora evento = new Bitacora();
                    evento.setIdBitacora(rs.getInt("idBitacora"));
                    evento.setIdEstudiante(rs.getInt("idEstudiante"));
                    evento.setIdCurso(rs.getInt("idCurso"));
                    evento.setIdMateria(rs.getInt("idMateria"));
                    evento.setIdDocente(rs.getInt("idDocente"));
                    evento.setFecha(rs.getDate("fecha").toLocalDate());
                    evento.setMotivo(rs.getString("motivo"));
                    eventos.add(evento);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la bitácora por estudiante: " + e.getMessage());
        }

        return eventos;
    }

    /**
     * Obtiene la lista completa de estudiantes registrados en el sistema.
     * Ordena la lista por apellido y nombre para facilitar su visualización.
     * 
     * @return Lista de objetos Estudiante
     */
    public List<Estudiante> obtenerEstudiantes() {
        List<Estudiante> estudiantes = new ArrayList<>();
        String sql = "SELECT idEstudiante, nombre, apellido, dni, telefono, responsable, idCurso " +
                     "FROM estudiante ORDER BY apellido, nombre";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Estudiante estudiante = new Estudiante(
                    rs.getInt("idEstudiante"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("dni"),
                    rs.getString("telefono"),
                    rs.getString("responsable"),
                    rs.getInt("idCurso")
                );
                estudiantes.add(estudiante);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener estudiantes: " + e.getMessage());
        }

        return estudiantes;
    }
    
        /**
     * Registra una observación pedagógica del docente en la bitácora de uno o más estudiantes.
     * 
     * Este método se utiliza desde la vista VentanaRegistrarObservacionDocente, cumpliendo con el CU10.
     * Inserta una entrada por estudiante, dejando el campo idMateria en NULL y motivo como "Observación pedagógica".
     *
     */
        public static boolean registrarObservacionPedagogica(int idDocente, int idCurso, List<Integer> idEstudiantes, String texto, LocalDate fecha) {
       String sql = "INSERT INTO bitacora (idUsuario, idEstudiante, idCurso, idMateria, fecha, tipoEvento, descripcion) " +
                    "VALUES (?, ?, ?, NULL, ?, ?, ?)";

       int registrosInsertados = 0;

       try (Connection conn = conexionBD.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

           for (int idEst : idEstudiantes) {
               stmt.setInt(1, idDocente);
               stmt.setInt(2, idEst);
               stmt.setInt(3, idCurso);
               stmt.setDate(4, java.sql.Date.valueOf(fecha));
               stmt.setString(5, "Observación pedagógica"); // tipoEvento
               stmt.setString(6, texto);                    // descripcion
               stmt.addBatch();
           }

           int[] resultados = stmt.executeBatch();
           for (int r : resultados) {
               if (r > 0) registrosInsertados++;
           }

       } catch (SQLException e) {
           System.err.println("Error al registrar observación pedagógica en bitácora:");
           System.err.println(e.getMessage());
           return false;
       }

       return registrosInsertados > 0;
   }
        
        /**
     * Devuelve todas las observaciones pedagógicas registradas para un estudiante.
     * Cada fila contiene: Fecha, Docente (nombre y apellido), Curso, Observación.
     *
     * @param idEstudiante ID del estudiante a consultar
     * @return Lista de arreglos String[] con los datos mencionados
     */
    public static List<String[]> obtenerObservacionesPorEstudiante(int idEstudiante) {
        List<String[]> resultados = new ArrayList<>();

        String sql = """
            SELECT 
                b.fecha,
                u.nombre AS nombreDocente,
                u.apellido AS apellidoDocente,
                c.nombreCurso,
                b.descripcion
            FROM bitacora b
            JOIN usuario u ON b.idUsuario = u.idUsuario
            JOIN curso c ON b.idCurso = c.idCurso
            WHERE b.idEstudiante = ? AND b.tipoEvento = 'Observación pedagógica'
            ORDER BY b.fecha DESC
        """;

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String fecha = rs.getDate("fecha").toLocalDate().toString();
                String docente = rs.getString("nombreDocente") + " " + rs.getString("apellidoDocente");
                String curso = rs.getString("nombreCurso");
                String descripcion = rs.getString("descripcion");

                resultados.add(new String[]{fecha, docente, curso, descripcion});
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener observaciones pedagógicas: " + e.getMessage());
        }

        return resultados;
    }
    
        /**
     * Devuelve las observaciones pedagógicas registradas por un docente
     * para un estudiante específico en un curso determinado.
     *
     * Cada fila contiene: Fecha, Curso, Observación.
     *
     * @param idDocente ID del docente que realizó las observaciones
     * @param idEstudiante ID del estudiante a consultar
     * @param idCurso ID del curso del estudiante
     * @return Lista de filas con datos [fecha, curso, descripcion]
     */
    public static List<String[]> obtenerObservacionesDelDocente(int idDocente, int idEstudiante, int idCurso) {
        List<String[]> resultados = new ArrayList<>();

        String sql = """
            SELECT 
                b.fecha,
                c.nombreCurso,
                b.descripcion
            FROM bitacora b
            JOIN curso c ON b.idCurso = c.idCurso
            WHERE b.idUsuario = ? 
              AND b.idEstudiante = ? 
              AND b.idCurso = ? 
              AND b.tipoEvento = 'Observación pedagógica'
            ORDER BY b.fecha DESC
        """;

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDocente);
            ps.setInt(2, idEstudiante);
            ps.setInt(3, idCurso);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String fecha = rs.getDate("fecha").toLocalDate().toString();
                String curso = rs.getString("nombreCurso");
                String observacion = rs.getString("descripcion");

                resultados.add(new String[]{fecha, curso, observacion});
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener observaciones del docente: " + e.getMessage());
        }

        return resultados;
    }



}
