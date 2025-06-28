package controlador;

import modelo.Usuario;
import vista.VentanaTomarAsistenciaPreceptor;
import vista.VentanaVerAsistenciasPorCurso;
import vista.VentanaEditarAsistencia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.Map;
import conexion.conexionBD;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import excepciones.FechaInvalidaException;

/**
 * Controlador que gestiona la lógica y las operaciones relacionadas con
 * la gestión de asistencias en el sistema SIGE.
 * Incluye métodos para abrir ventanas de asistencia y para manipular datos de asistencias.
 * 
 * @author Yonatan
 */
public class ControladorAsistencia {

    /**
     * Abre la ventana para que el Preceptor pueda tomar la asistencia general del curso.
     * 
     * @param usuario Usuario que accede (Preceptor)
     */
    public void abrirVentanaTomarAsistenciaPreceptor(Usuario usuario) {
        new VentanaTomarAsistenciaPreceptor(usuario).setVisible(true);
    }

    
    /**
     * Abre la ventana para ver asistencias filtradas por curso.
     * 
     * @param usuario Usuario que accede
     */
    public void abrirVentanaVerAsistenciasPorCurso(Usuario usuario) {
        new VentanaVerAsistenciasPorCurso(usuario).setVisible(true);
    }

    /**
     * Abre la ventana que permite editar una asistencia ya registrada.
     * 
     * @param usuario Usuario que accede
     */
    public void abrirVentanaEditarAsistencia(Usuario usuario) {
        new VentanaEditarAsistencia(usuario).setVisible(true);
    }


        /**
     * Registra una nueva asistencia general tomada por el Preceptor.
     * Guarda la asistencia en la tabla 'asistencia', registra los estados de cada estudiante
     * y además agrega un evento en la bitácora si el estado no es "Presente".
     * 
     * @param usuario Usuario que toma la asistencia (Preceptor)
     * @param idCurso Curso al que pertenece la asistencia
     * @param fecha Fecha en que se toma la asistencia
     * @param estadosPorEstudiante Mapa que relaciona ID de estudiante con su estado (Presente, Ausente, etc.)
     */
    public void registrarAsistencia(Usuario usuario, int idCurso, LocalDate fecha,
                                    Map<Integer, String> estadosPorEstudiante) throws FechaInvalidaException {

        // Validamos que la fecha no sea nula ni futura
        if (fecha == null || fecha.isAfter(LocalDate.now())) {
            throw new FechaInvalidaException("La fecha ingresada no es válida. No puede ser futura ni nula.");
        }

        // Sentencia para insertar el encabezado de la asistencia general
        String insertAsistenciaSQL = "INSERT INTO asistencia (idCurso, idUsuario, fecha) VALUES (?, ?, ?)";

        // Sentencia para insertar los estados de cada estudiante en la tabla asistenciadetalle
        String insertDetalleSQL = "INSERT INTO asistenciadetalle (idAsistencia, idEstudiante, estado) VALUES (?, ?, ?)";

        // Sentencia para registrar en la bitácora solo si el estudiante no estuvo presente
        String insertBitacoraSQL = """
            INSERT INTO bitacora (idEstudiante, fecha, descripcion, tipoEvento, idCurso)
            VALUES (?, NOW(), ?, ?, ?)
        """;

        Connection conn = null;
        PreparedStatement stmtAsistencia = null;
        PreparedStatement stmtDetalle = null;
        PreparedStatement stmtBitacora = null;
        ResultSet generatedKeys = null;

        try {
            // Abrimos la conexión a la base de datos
            conn = conexionBD.obtenerConexion();
            conn.setAutoCommit(false); // Iniciamos una transacción para asegurar consistencia

            // Insertamos el encabezado de la asistencia (fecha, curso, usuario que toma la asistencia)
            stmtAsistencia = conn.prepareStatement(insertAsistenciaSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            stmtAsistencia.setInt(1, idCurso);
            stmtAsistencia.setInt(2, usuario.getIdUsuario());
            stmtAsistencia.setDate(3, Date.valueOf(fecha));
            stmtAsistencia.executeUpdate();

            // Obtenemos el ID generado automáticamente para esta asistencia
            generatedKeys = stmtAsistencia.getGeneratedKeys();
            int idAsistencia = -1;
            if (generatedKeys.next()) {
                idAsistencia = generatedKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el ID de la asistencia generada.");
            }

            // Preparamos la sentencia para insertar los detalles de cada estudiante
            stmtDetalle = conn.prepareStatement(insertDetalleSQL);

            // Preparamos la sentencia para la bitácora
            stmtBitacora = conn.prepareStatement(insertBitacoraSQL);

            // Recorremos todos los estudiantes del curso y registramos su estado
            for (Map.Entry<Integer, String> entrada : estadosPorEstudiante.entrySet()) {
                int idEstudiante = entrada.getKey();
                String estado = entrada.getValue();

                // Insertamos en asistenciadetalle
                stmtDetalle.setInt(1, idAsistencia);
                stmtDetalle.setInt(2, idEstudiante);
                stmtDetalle.setString(3, estado);
                stmtDetalle.addBatch(); // Acumulamos todos los inserts para ejecutarlos juntos

                // Si el estado no fue "Presente", también lo registramos en la bitácora
                if (!estado.equalsIgnoreCase("Presente")) {
                    String descripcion = "Estado de asistencia: " + estado;

                    stmtBitacora.setInt(1, idEstudiante);
                    stmtBitacora.setString(2, descripcion);
                    stmtBitacora.setString(3, "Asistencia Preceptor");
                    stmtBitacora.setInt(4, idCurso);
                    stmtBitacora.addBatch(); // Acumulamos los inserts en la bitácora
                }
            }

            // Ejecutamos en bloque todas las inserciones en asistenciadetalle
            stmtDetalle.executeBatch();

            // Ejecutamos en bloque las inserciones en bitácora (solo si hubo ausentes o similares)
            stmtBitacora.executeBatch();

            // Confirmamos toda la transacción: encabezado + detalles + bitácora
            conn.commit();

        } catch (SQLException e) {
            // Si ocurre un error, deshacemos todos los cambios realizados
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new RuntimeException("Error al registrar la asistencia: " + e.getMessage(), e);
        } finally {
            // Cerramos todos los recursos utilizados
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (stmtDetalle != null) stmtDetalle.close();
                if (stmtBitacora != null) stmtBitacora.close();
                if (stmtAsistencia != null) stmtAsistencia.close();
                if (conn != null) conn.setAutoCommit(true); // Dejamos la conexión en estado normal
                if (conn != null) conn.close(); // Cerramos la conexión
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


 
            /**
      * Guarda una asistencia por materia tomada por un docente.
      * Registra un encabezado en la tabla asistencia, luego un detalle por cada estudiante,
      * inserta en bitácora si el estado no es "Presente".
      *
      * @param idUsuario ID del docente que toma la asistencia
      * @param idCurso Curso al que pertenece la asistencia
      * @param idMateria Materia específica donde se tomó la asistencia
      * @param asistencias Mapa con ID de estudiante y su estado ("Presente", "Ausente", etc.)
      * @return true si todo fue guardado correctamente, false si hubo error
      */
     public static boolean guardarAsistenciaPorMateria(int idUsuario, int idCurso, int idMateria, LocalDate fecha, Map<Integer, String> asistencias){
         Connection conn = null;
         PreparedStatement psAsistencia = null;
         PreparedStatement psDetalle = null;
         PreparedStatement psBitacora = null;

         try {
             conn = conexionBD.obtenerConexion();
             conn.setAutoCommit(false); // Inicia transacción

             // ==========================
             // 1. Insertar encabezado de asistencia
             // ==========================
             // Insertamos el encabezado de la asistencia, incluyendo la materia si aplica
                String sqlAsistencia = "INSERT INTO asistencia (idUsuario, idCurso, fecha, idMateria) VALUES (?, ?, ?, ?)";
                psAsistencia = conn.prepareStatement(sqlAsistencia, Statement.RETURN_GENERATED_KEYS);
                psAsistencia.setInt(1, idUsuario);
                psAsistencia.setInt(2, idCurso);
                psAsistencia.setDate(3, java.sql.Date.valueOf(fecha));
                psAsistencia.setInt(4, idMateria);
                psAsistencia.executeUpdate();


             ResultSet rs = psAsistencia.getGeneratedKeys();
             if (!rs.next()) throw new SQLException("No se pudo generar ID de asistencia");
             int idAsistencia = rs.getInt(1);

             // ==========================
             // 2. Insertar detalle por estudiante
             // ==========================
             String sqlDetalle = "INSERT INTO asistenciadetalle (idAsistencia, idEstudiante, estado, idMateria) VALUES (?, ?, ?, ?)";
             psDetalle = conn.prepareStatement(sqlDetalle);

             // ==========================
             // 3. Insertar eventos en la bitácora
             // ==========================
             String sqlBitacora = """
                 INSERT INTO bitacora (idEstudiante, fecha, descripcion, tipoEvento, idCurso, idMateria)
                 VALUES (?, NOW(), ?, ?, ?, ?)
             """;
             psBitacora = conn.prepareStatement(sqlBitacora);

             for (Map.Entry<Integer, String> entry : asistencias.entrySet()) {
                 int idEstudiante = entry.getKey();
                 String estado = entry.getValue();

                 // Detalle de asistencia
                 psDetalle.setInt(1, idAsistencia);
                 psDetalle.setInt(2, idEstudiante);
                 psDetalle.setString(3, estado);
                 psDetalle.setInt(4, idMateria);
                 psDetalle.addBatch();

                 // Registro en bitácora si no fue "Presente"
                 if (!estado.equalsIgnoreCase("Presente")) {
                     String descripcion = "Estado de asistencia: " + estado;

                     psBitacora.setInt(1, idEstudiante);
                     psBitacora.setString(2, descripcion);
                     psBitacora.setString(3, "Asistencia Docente");
                     psBitacora.setInt(4, idCurso);
                     psBitacora.setInt(5, idMateria);
                     psBitacora.addBatch();
                 }
             }

             // Ejecutamos ambos lotes
             psDetalle.executeBatch();
             psBitacora.executeBatch();

             // Confirmamos la transacción
             conn.commit();
             return true;

         } catch (SQLException e) {
             // Si algo falla, deshacemos los cambios
             try {
                 if (conn != null) conn.rollback();
             } catch (SQLException ex) {
                 System.err.println("Error al hacer rollback: " + ex.getMessage());
             }
             System.err.println("Error al guardar asistencia por materia: " + e.getMessage());
             e.printStackTrace();
             return false;

         } finally {
             // Cerramos todos los recursos
             try {
                 if (psAsistencia != null) psAsistencia.close();
                 if (psDetalle != null) psDetalle.close();
                 if (psBitacora != null) psBitacora.close();
                 if (conn != null) conn.close();
             } catch (SQLException ex) {
                 System.err.println("Error al cerrar recursos: " + ex.getMessage());
             }
         }
     }



        /**
     * Permite modificar los estados de asistencia existentes para una asistencia ya registrada.
     * 
     * @param idAsistencia ID de la asistencia a modificar
     * @param nuevosEstados Mapa de ID del estudiante a nuevo estado de asistencia
     */
    public void editarAsistencia(int idAsistencia, Map<Integer, String> nuevosEstados) {
        String sql = "UPDATE asistenciadetalle SET estado = ? WHERE idAsistencia = ? AND idEstudiante = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // Iniciamos transacción

            for (Map.Entry<Integer, String> entry : nuevosEstados.entrySet()) {
                int idEstudiante = entry.getKey();
                String estado = entry.getValue();

                stmt.setString(1, estado);
                stmt.setInt(2, idAsistencia);
                stmt.setInt(3, idEstudiante);
                stmt.addBatch();
            }

            stmt.executeBatch(); // Ejecutamos todos los updates en bloque
            conn.commit(); // Confirmamos los cambios

        } catch (SQLException e) {
            System.err.println("Error al editar asistencia: " + e.getMessage());
            try {
                if (conexionBD.obtenerConexion() != null) {
                    conexionBD.obtenerConexion().rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
        }
    }


            /**
      * Devuelve la lista completa de asistencias registradas en el sistema,
      * ordenadas por fecha descendente. Incluye asistencias generales y por materia.
      * Ahora también se cargan los detalles de cada asistencia (estado por estudiante).
      * 
      * @return Lista de objetos Asistencia con sus detalles cargados.
      */
     public List<modelo.Asistencia> obtenerAsistencias() {
         List<modelo.Asistencia> lista = new ArrayList<>();

         String sql = "SELECT idAsistencia, idCurso, idUsuario, fecha, idMateria FROM asistencia ORDER BY fecha DESC";

         try (Connection conn = conexionBD.obtenerConexion();
              PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery()) {

             while (rs.next()) {
                 modelo.Asistencia asistencia = new modelo.Asistencia();

                 // Cargo los datos básicos de la asistencia
                 asistencia.setIdAsistencia(rs.getInt("idAsistencia"));
                 asistencia.setIdCurso(rs.getInt("idCurso"));
                 asistencia.setIdUsuario(rs.getInt("idUsuario"));
                 asistencia.setFecha(rs.getDate("fecha").toLocalDate());

                 try {
                     asistencia.setIdMateria(rs.getInt("idMateria")); // Puede ser 0 o -1 si es general
                 } catch (Exception e) {
                     asistencia.setIdMateria(-1);
                 }

                 // Cargamos también los detalles de esta asistencia
                 List<modelo.AsistenciaDetalle> detalles = obtenerDetallesPorAsistencia(asistencia.getIdAsistencia());
                 asistencia.setDetalles(detalles);

                 lista.add(asistencia);
             }

         } catch (SQLException e) {
             System.err.println("Error al obtener todas las asistencias: " + e.getMessage());
         }

         return lista;
     }


        /**
     * Devuelve las asistencias generales registradas para un curso específico.
     * Carga los datos básicos desde la tabla asistencia y devuelve una lista de objetos.
     *
     * @param idCurso ID del curso del cual se desean obtener asistencias
     * @return Lista de objetos Asistencia
     */
    public List<modelo.Asistencia> obtenerAsistenciasPorCurso(int idCurso) {
        List<modelo.Asistencia> lista = new ArrayList<>();

        String sql = "SELECT idAsistencia, idCurso, idUsuario, fecha FROM asistencia WHERE idCurso = ? ORDER BY fecha DESC";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modelo.Asistencia asistencia = new modelo.Asistencia();
                asistencia.setIdAsistencia(rs.getInt("idAsistencia"));
                asistencia.setIdCurso(rs.getInt("idCurso"));
                asistencia.setIdUsuario(rs.getInt("idUsuario"));
                asistencia.setFecha(rs.getDate("fecha").toLocalDate());

                lista.add(asistencia);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener asistencias por curso: " + e.getMessage());
        }

        return lista;
    }


        /**
      * Devuelve las asistencias registradas por un docente,
      * filtradas para incluir solo aquellas que pertenecen a cursos y materias
      * donde está asignado activamente según la tabla docente_materia_curso.
      *
      * @param idDocente ID del usuario con rol Docente
      * @return Lista de objetos Asistencia válidos para ese docente
      */
     public List<modelo.Asistencia> obtenerAsistenciasPorDocente(int idDocente) {
         List<modelo.Asistencia> lista = new ArrayList<>();

         // Consulta SQL que une asistencia con docente_materia_curso
         // y usuarios_rol para validar asignación actual de cursos del docente
         String sql = """
             SELECT a.idAsistencia, a.idCurso, a.idUsuario, a.fecha, a.idMateria
             FROM asistencia a
             JOIN docente_materia_curso dmc ON a.idCurso = dmc.idCurso AND a.idMateria = dmc.idMateria
             JOIN usuarios_rol ur ON dmc.idUsuarioRol = ur.idUsuarioRol
             WHERE ur.idUsuario = ? AND ur.idRol = 1
             ORDER BY a.fecha DESC
         """;

         try (Connection conn = conexionBD.obtenerConexion();
              PreparedStatement stmt = conn.prepareStatement(sql)) {

             stmt.setInt(1, idDocente);  // ID del docente como parámetro
             ResultSet rs = stmt.executeQuery();

             while (rs.next()) {
                 modelo.Asistencia asistencia = new modelo.Asistencia();

                 // Cargamos los campos principales de la asistencia
                 asistencia.setIdAsistencia(rs.getInt("idAsistencia"));
                 asistencia.setIdCurso(rs.getInt("idCurso"));
                 asistencia.setIdUsuario(rs.getInt("idUsuario"));
                 asistencia.setFecha(rs.getDate("fecha").toLocalDate());

                 try {
                     asistencia.setIdMateria(rs.getInt("idMateria"));
                 } catch (Exception ignored) {
                     asistencia.setIdMateria(0); // Si no tiene materia, puede ser general
                 }

                 lista.add(asistencia); // Agregamos la asistencia filtrada a la lista
             }

         } catch (SQLException e) {
             System.err.println("Error al obtener asistencias por docente (filtradas por asignación): " + e.getMessage());
         }

         return lista;
     }



        /**
     * Busca una asistencia registrada por su ID y devuelve su información básica.
     *
     * @param idAsistencia ID único de la asistencia
     * @return Objeto Asistencia con datos básicos, o null si no se encuentra
     */
    public modelo.Asistencia buscarAsistenciaPorId(int idAsistencia) {
        modelo.Asistencia asistencia = null;

        String sql = "SELECT idAsistencia, idCurso, idUsuario, fecha, idMateria FROM asistencia WHERE idAsistencia = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAsistencia);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                asistencia = new modelo.Asistencia();
                asistencia.setIdAsistencia(rs.getInt("idAsistencia"));
                asistencia.setIdCurso(rs.getInt("idCurso"));
                asistencia.setIdUsuario(rs.getInt("idUsuario"));
                asistencia.setFecha(rs.getDate("fecha").toLocalDate());
                asistencia.setIdMateria(rs.getInt("idMateria")); // Si no aplica, puede quedar en 0
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar asistencia por ID: " + e.getMessage());
        }

        return asistencia;
    }
    
        /**
     * Devuelve la lista de detalles (por estudiante) asociados a una asistencia registrada.
     * 
     * @param idAsistencia ID de la asistencia
     * @return Lista de objetos AsistenciaDetalle
     */
    public List<modelo.AsistenciaDetalle> obtenerDetallesPorAsistencia(int idAsistencia) {
        List<modelo.AsistenciaDetalle> detalles = new ArrayList<>();
        String sql = "SELECT idEstudiante, estado, idMateria FROM asistenciadetalle WHERE idAsistencia = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAsistencia);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modelo.AsistenciaDetalle detalle = new modelo.AsistenciaDetalle();
                detalle.setIdEstudiante(rs.getInt("idEstudiante"));
                detalle.setEstado(rs.getString("estado"));
                detalle.setIdMateria(rs.getInt("idMateria"));
                detalle.setIdAsistencia(idAsistencia);
                detalles.add(detalle);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener detalles de asistencia: " + e.getMessage());
        }

        return detalles;
    }


}
