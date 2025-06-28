package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Clase que representa una solicitud de reunión realizada por un usuario del sistema.
 * Puede estar asociada a uno o varios estudiantes y es gestionada por el Preceptor.
 * Incluye fecha de solicitud, motivo, disponibilidad propuesta, y si está confirmada, la fecha y hora real.
 * 
 * @author Yonatan
 */
public class SolicitudReunion {

    private int idSolicitud;
    private int idUsuarioSolicitante;
    private int idCurso;
    private List<Integer> idsEstudiantes; // Se usa al registrar
    private String motivo;
    private String disponibilidad;
    private String estado; // Pendiente, Confirmada, Rechazada
    private LocalDate fechaSolicitud;
    private LocalDate fechaReunionConfirmada;
    private LocalTime horaReunionConfirmada; // Nuevo campo para hora confirmada

    /**
     * Constructor vacío requerido por frameworks o instanciación manual.
     */
    public SolicitudReunion() {}

    /**
     * Constructor completo para crear una solicitud con múltiples estudiantes.
     */
    public SolicitudReunion(int idSolicitud, int idUsuarioSolicitante, int idCurso, List<Integer> idsEstudiantes,
                            String motivo, String disponibilidad, LocalDate fechaSolicitud, String estado) {
        this.idSolicitud = idSolicitud;
        this.idUsuarioSolicitante = idUsuarioSolicitante;
        this.idCurso = idCurso;
        this.idsEstudiantes = idsEstudiantes;
        this.motivo = motivo;
        this.disponibilidad = disponibilidad;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = estado;
    }

    // === Getters y Setters ===

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public int getIdUsuarioSolicitante() {
        return idUsuarioSolicitante;
    }

    public void setIdUsuarioSolicitante(int idUsuarioSolicitante) {
        this.idUsuarioSolicitante = idUsuarioSolicitante;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public List<Integer> getIdsEstudiantes() {
        return idsEstudiantes;
    }

    public void setIdsEstudiantes(List<Integer> idsEstudiantes) {
        this.idsEstudiantes = idsEstudiantes;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFechaReunionConfirmada() {
        return fechaReunionConfirmada;
    }

    public void setFechaReunionConfirmada(LocalDate fechaReunionConfirmada) {
        this.fechaReunionConfirmada = fechaReunionConfirmada;
    }

    public LocalTime getHoraReunionConfirmada() {
        return horaReunionConfirmada;
    }

    public void setHoraReunionConfirmada(LocalTime horaReunionConfirmada) {
        this.horaReunionConfirmada = horaReunionConfirmada;
    }

    // === Representación para debug o vistas ===

    @Override
    public String toString() {
        return "SolicitudReunion{" +
                "idSolicitud=" + idSolicitud +
                ", curso=" + idCurso +
                ", estado='" + estado + '\'' +
                ", motivo='" + motivo + '\'' +
                ", solicitante ID=" + idUsuarioSolicitante +
                ", fechaSolicitud=" + fechaSolicitud +
                (fechaReunionConfirmada != null ? ", confirmada: " + fechaReunionConfirmada : "") +
                (horaReunionConfirmada != null ? " a las " + horaReunionConfirmada : "") +
                '}';
    }
}
