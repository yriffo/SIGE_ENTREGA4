package controlador;

import conexion.conexionBD;
import modelo.*;
import vista.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador que unifica la gestión de reuniones en el sistema SIGE.
 * Permite a Docente, Asesor o Directivo solicitar reuniones,
 * al Preceptor confirmar y visualizar reuniones.
 * Incluye manejo de fecha y hora confirmadas.
 * 
 * @author Yonatan
 */
public class ControladorReunion {

    /**
     * Registra una solicitud de reunión en la base de datos.
     * Inserta la solicitud y luego asocia cada estudiante.
     * 
     * @param idUsuarioSolicitante ID del usuario que solicita
     * @param idCurso ID del curso seleccionado
     * @param idsEstudiantes Lista de IDs de los estudiantes convocados
     * @param motivo Texto breve del motivo
     * @param disponibilidad Texto de disponibilidad horaria
     * @return true si la solicitud fue registrada correctamente
     */
    public boolean solicitarReunion(int idUsuarioSolicitante, int idCurso, List<Integer> idsEstudiantes,
                                   String motivo, String disponibilidad) {
        String sqlInsertSolicitud = "INSERT INTO solicitud_reunion (idUsuarioSolicitante, idCurso, motivo, disponibilidad, estado, fechaSolicitud) " +
                                   "VALUES (?, ?, ?, ?, 'Pendiente', CURDATE())";
        String sqlInsertEstudiantes = "INSERT INTO solicitud_estudiante (idSolicitud, idEstudiante) VALUES (?, ?)";

        try (Connection conn = conexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psSolicitud = conn.prepareStatement(sqlInsertSolicitud, Statement.RETURN_GENERATED_KEYS)) {
                psSolicitud.setInt(1, idUsuarioSolicitante);
                psSolicitud.setInt(2, idCurso);
                psSolicitud.setString(3, motivo);
                psSolicitud.setString(4, disponibilidad);
                int filas = psSolicitud.executeUpdate();

                if (filas == 0) {
                    conn.rollback();
                    return false;
                }

                ResultSet generatedKeys = psSolicitud.getGeneratedKeys();
                if (!generatedKeys.next()) {
                    conn.rollback();
                    return false;
                }
                int idSolicitud = generatedKeys.getInt(1);

                try (PreparedStatement psEstudiantes = conn.prepareStatement(sqlInsertEstudiantes)) {
                    for (Integer idEst : idsEstudiantes) {
                        psEstudiantes.setInt(1, idSolicitud);
                        psEstudiantes.setInt(2, idEst);
                        psEstudiantes.addBatch();
                    }
                    psEstudiantes.executeBatch();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error al solicitar reunión: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error en conexión al solicitar reunión: " + e.getMessage());
            return false;
        }
    }

    /**
     * Confirma una solicitud de reunión.
     * Se actualiza el estado a Confirmada y se registra fecha y hora.
     * 
     * @param idSolicitud ID de la solicitud a actualizar
     * @param fechaConfirmada Fecha de la reunión confirmada
     * @param horaConfirmada Hora de la reunión confirmada
     * @return true si se confirmó correctamente
     */
    public boolean confirmarReunion(int idSolicitud, LocalDate fechaConfirmada, LocalTime horaConfirmada) {
        String sql = "UPDATE solicitud_reunion " +
                     "SET estado = 'Confirmada', fechaReunionConfirmada = ?, horaReunionConfirmada = ? " +
                     "WHERE idSolicitud = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(fechaConfirmada));
            ps.setTime(2, java.sql.Time.valueOf(horaConfirmada));
            ps.setInt(3, idSolicitud);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al confirmar reunión: " + e.getMessage());
            return false;
        }
    }

    /**
     * Devuelve todas las solicitudes de reunión del sistema.
     * Incluye pendientes y confirmadas, con fecha y hora si aplica.
     * 
     * @return Lista completa de solicitudes
     */
    public List<SolicitudReunion> obtenerSolicitudes() {
        List<SolicitudReunion> solicitudes = new ArrayList<>();
        String sql = "SELECT * FROM solicitud_reunion ORDER BY fechaSolicitud DESC";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SolicitudReunion sr = new SolicitudReunion();
                sr.setIdSolicitud(rs.getInt("idSolicitud"));
                sr.setIdUsuarioSolicitante(rs.getInt("idUsuarioSolicitante"));
                sr.setIdCurso(rs.getInt("idCurso"));
                sr.setMotivo(rs.getString("motivo"));
                sr.setDisponibilidad(rs.getString("disponibilidad"));
                sr.setEstado(rs.getString("estado"));
                sr.setFechaSolicitud(rs.getDate("fechaSolicitud").toLocalDate());

                if (rs.getDate("fechaReunionConfirmada") != null) {
                    sr.setFechaReunionConfirmada(rs.getDate("fechaReunionConfirmada").toLocalDate());
                }

                if (rs.getTime("horaReunionConfirmada") != null) {
                    sr.setHoraReunionConfirmada(rs.getTime("horaReunionConfirmada").toLocalTime());
                }

                solicitudes.add(sr);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener solicitudes: " + e.getMessage());
        }

        return solicitudes;
    }

    /**
     * Devuelve todas las reuniones donde participa un estudiante.
     * Se usa en la bitácora o consultas por estudiante.
     * 
     * @param idEstudiante ID del estudiante
     * @return Lista de reuniones en las que está involucrado
     */
    public List<SolicitudReunion> obtenerSolicitudesPorEstudiante(int idEstudiante) {
        List<SolicitudReunion> solicitudes = new ArrayList<>();
        String sql = "SELECT sr.* FROM solicitud_reunion sr " +
                     "JOIN solicitud_estudiante se ON sr.idSolicitud = se.idSolicitud " +
                     "WHERE se.idEstudiante = ? ORDER BY sr.fechaSolicitud DESC";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SolicitudReunion sr = new SolicitudReunion();
                sr.setIdSolicitud(rs.getInt("idSolicitud"));
                sr.setIdUsuarioSolicitante(rs.getInt("idUsuarioSolicitante"));
                sr.setIdCurso(rs.getInt("idCurso"));
                sr.setMotivo(rs.getString("motivo"));
                sr.setDisponibilidad(rs.getString("disponibilidad"));
                sr.setEstado(rs.getString("estado"));
                sr.setFechaSolicitud(rs.getDate("fechaSolicitud").toLocalDate());

                if (rs.getDate("fechaReunionConfirmada") != null) {
                    sr.setFechaReunionConfirmada(rs.getDate("fechaReunionConfirmada").toLocalDate());
                }

                if (rs.getTime("horaReunionConfirmada") != null) {
                    sr.setHoraReunionConfirmada(rs.getTime("horaReunionConfirmada").toLocalTime());
                }

                solicitudes.add(sr);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener solicitudes por estudiante: " + e.getMessage());
        }

        return solicitudes;
    }
    
        /**
     * Devuelve todas las reuniones asociadas a un curso específico,
     * sin importar el estado (pendiente o confirmada).
     *
     * @param idCurso ID del curso a consultar
     * @return Lista de reuniones vinculadas a ese curso
     */
    public List<SolicitudReunion> obtenerSolicitudesPorCurso(int idCurso) {
        List<SolicitudReunion> solicitudes = new ArrayList<>();
        String sql = "SELECT * FROM solicitud_reunion WHERE idCurso = ? ORDER BY fechaSolicitud DESC";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCurso);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SolicitudReunion sr = new SolicitudReunion();
                sr.setIdSolicitud(rs.getInt("idSolicitud"));
                sr.setIdUsuarioSolicitante(rs.getInt("idUsuarioSolicitante"));
                sr.setIdCurso(rs.getInt("idCurso"));
                sr.setMotivo(rs.getString("motivo"));
                sr.setDisponibilidad(rs.getString("disponibilidad"));
                sr.setEstado(rs.getString("estado"));
                sr.setFechaSolicitud(rs.getDate("fechaSolicitud").toLocalDate());

                if (rs.getDate("fechaReunionConfirmada") != null) {
                    sr.setFechaReunionConfirmada(rs.getDate("fechaReunionConfirmada").toLocalDate());
                }

                if (rs.getTime("horaReunionConfirmada") != null) {
                    sr.setHoraReunionConfirmada(rs.getTime("horaReunionConfirmada").toLocalTime());
                }

                solicitudes.add(sr);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener reuniones por curso: " + e.getMessage());
        }

        return solicitudes;
    }


    // ==== Métodos para abrir vistas desde menú ====

    public void abrirVentanaConfirmarReunion(Usuario usuario) {
        new VentanaConfirmarReunion().setVisible(true);
    }

    public void abrirVentanaVerReunionesPreceptor(Usuario usuario) {
        new VentanaVerReunionesPreceptor().setVisible(true);
    }

    public void abrirVentanaVerReunionesPorEstudiantePreceptor(Usuario usuario) {
        new VentanaVerReunionesPorEstudiantePreceptor().setVisible(true);
    }
}
