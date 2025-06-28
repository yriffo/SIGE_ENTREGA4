/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import conexion.conexionBD; // para acceder a la base de datos
import java.sql.Connection;  // Para manejar la conexión
import java.sql.PreparedStatement;// Para ejecutar consultas SQL con parámetros
import java.sql.SQLException; // Para capturar errores específicos de SQL


/**
 *
 * @author Yonatan
 */


/**
 * Clase que representa un registro de asistencia para un curso en una fecha determinada.
 * Puede ser tomado por un preceptor (asistencia general) o un docente (asistencia por materia).
 * Los detalles por estudiante y estado se guardan en AsistenciaDetalle.
 */
public class Asistencia implements RegistrableEnBitacora {

    private int idAsistencia;
    private int idCurso;
    private int idUsuario;     // Quien toma la asistencia (docente o preceptor)
    private int idMateria = -1; // Si aplica (solo docentes)
    private LocalDate fecha;
    private List<AsistenciaDetalle> detalles;

    /**
     * Constructor vacío requerido.
     */
    public Asistencia() {
    this.detalles = new ArrayList<>();
    }


    /**
     * Constructor general utilizado para registro básico (sin materia).
     *
     * @param idAsistencia identificador único
     * @param idCurso curso al que se le toma asistencia
     * @param idUsuario quien la toma
     * @param fecha fecha de la asistencia
     */
    public Asistencia(int idAsistencia, int idCurso, int idUsuario, LocalDate fecha) {
        this.idAsistencia = idAsistencia;
        this.idCurso = idCurso;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.detalles = new ArrayList<>();
    }

    /**
     * Constructor extendido que incluye la materia (para docentes).
     *
     * @param idAsistencia ID de la asistencia
     * @param fecha Fecha
     * @param idCurso Curso
     * @param idMateria Materia (si aplica, o -1 si no)
     * @param idUsuario Usuario que toma la asistencia
     */
    public Asistencia(int idAsistencia, LocalDate fecha, int idCurso, int idMateria, int idUsuario) {
        this.idAsistencia = idAsistencia;
        this.fecha = fecha;
        this.idCurso = idCurso;
        this.idMateria = idMateria;
        this.idUsuario = idUsuario;
        this.detalles = new ArrayList<>();
    }

    // === Getters y Setters ===

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public List<AsistenciaDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<AsistenciaDetalle> detalles) {
        this.detalles = detalles;
    }

    // === toString ===

    @Override
    public String toString() {
        return "Asistencia del " + fecha +
               " | Curso ID: " + idCurso +
               " | Usuario ID: " + idUsuario +
               (idMateria != -1 ? " | Materia ID: " + idMateria : "");
    }
    
    /**
 * Registra un evento en la bitácora del estudiante con la descripción proporcionada.
 * Este método forma parte de la interfaz RegistrableEnBitacora e implementa su comportamiento
 * concreto para los casos en que una asistencia debe dejar constancia en la bitácora.
 *
 * @param idEstudiante ID del estudiante involucrado
 * @param descripcion  Descripción del evento que se desea dejar registrado
 */
    @Override
    public void registrarEnBitacora(int idEstudiante, String descripcion) {
        // Consulta SQL para insertar un nuevo registro en la tabla bitácora
        String sql = "INSERT INTO bitacora (idEstudiante, fecha, descripcion) VALUES (?, NOW(), ?)";

        // Se intenta establecer conexión y preparar la sentencia para ejecutar
        try (Connection conn = conexion.conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Se establecen los parámetros: ID del estudiante y descripción del evento
            stmt.setInt(1, idEstudiante);
            stmt.setString(2, descripcion);

            // Se ejecuta la inserción del registro en la base de datos
            stmt.executeUpdate();

        } catch (Exception e) {
            // En caso de error, se muestra un mensaje en consola
            System.err.println("Error al registrar en bitácora desde Asistencia: " + e.getMessage());
        }
    }
    
        /**
     * Recorre los detalles de asistencia y registra en la bitácora
     * a cada estudiante cuyo estado no sea "Presente".
     */
    public void registrarEventosDeBitacora() {
        for (AsistenciaDetalle detalle : detalles) {
            String estado = detalle.getEstado();

            if (!estado.equalsIgnoreCase("Presente")) {
                String descripcion = "Asistencia del " + fecha.toString() + ": " + estado;
                registrarEnBitacora(detalle.getIdEstudiante(), descripcion);
            }
        }
    }


}
