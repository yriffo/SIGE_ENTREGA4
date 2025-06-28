package modelo;

/**
 * Clase que representa el estado de asistencia de un estudiante
 * en una toma de asistencia específica.
 * Se vincula con la tabla asistenciadetalle en la base de datos.
 */
public class AsistenciaDetalle {

    private int idDetalle;
    private int idAsistencia;
    private int idEstudiante;
    private String estado; // Presente, Ausente, Ausente Justificado
    private int idMateria; // opcional: si aplica (solo docentes)

    /**
     * Constructor vacío para instanciación flexible.
     */
    public AsistenciaDetalle() {}

    /**
     * Constructor completo para asistencia por materia.
     *
     * @param idDetalle identificador único del detalle
     * @param idAsistencia referencia a la toma de asistencia
     * @param idEstudiante estudiante al que se le registra asistencia
     * @param estado estado de asistencia (Presente, Ausente, etc.)
     * @param idMateria materia a la que corresponde (si aplica)
     */
    public AsistenciaDetalle(int idDetalle, int idAsistencia, int idEstudiante, String estado, int idMateria) {
        this.idDetalle = idDetalle;
        this.idAsistencia = idAsistencia;
        this.idEstudiante = idEstudiante;
        this.estado = estado;
        this.idMateria = idMateria;
    }
    
    /**
    * Constructor para asistencia general sin materia (ej. Preceptor).
    */
   public AsistenciaDetalle(int idDetalle, int idAsistencia, int idEstudiante, String estado) {
       this(idDetalle, idAsistencia, idEstudiante, estado, -1); // por defecto, sin materia
   }

    // Getters y setters

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    /**
     * Representación legible del detalle.
     */
    @Override
    public String toString() {
        return "Estudiante ID: " + idEstudiante +
               " - Estado: " + estado +
               (idMateria > 0 ? " - Materia ID: " + idMateria : "");
    }
}
