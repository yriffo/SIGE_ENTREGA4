package modelo;

/**
 * Clase Estudiante que representa a un alumno del sistema SIGE.
 * Compatible con base de datos: usa idCurso como entero.
 * Puede tener además el objeto Curso asociado si se desea.
 * 
 * @author Yonatan
 */
public class Estudiante {

    private int idEstudiante;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String email;
    private String responsable;

    private int idCurso;        // ← Lo que devuelve la BD (clave foránea)
    private Curso curso;        // ← Objeto completo, opcional (no cargado desde BD por defecto)

    /**
     * Constructor vacío (necesario para beans, carga dinámica, etc.)
     */
    public Estudiante() {
    }

    /**
     * Constructor completo compatible con la base de datos.
     * 
     * @param idEstudiante ID del estudiante
     * @param nombre Nombre
     * @param apellido Apellido
     * @param dni Documento
     * @param telefono Teléfono
     * @param responsable Responsable adulto
     * @param idCurso ID del curso al que pertenece (como está en la BD)
     */
    public Estudiante(int idEstudiante, String nombre, String apellido, String dni,
                      String telefono, String responsable, int idCurso) {
        this.idEstudiante = idEstudiante;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.responsable = responsable;
        this.idCurso = idCurso;
    }

    // ====================== Getters y Setters ======================

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {
        return apellido + ", " + nombre;
    }
}
