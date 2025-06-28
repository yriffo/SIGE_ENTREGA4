package modelo;

/**
 * Clase que representa un curso en el sistema SIGE.
 * Por ejemplo: 1A, 2B, etc.
 * 
 * @author Yonatan
 */
public class Curso {

    private int idCurso;
    private String nombre;
    private int anio;

    /**
     * Constructor vacío necesario para instanciación sin parámetros.
     */
    public Curso() {}

    /**
     * Constructor con parámetros.
     * 
     * @param idCurso identificador único del curso
     * @param nombre nombre del curso (por ejemplo, "1A")
     * @param anio año lectivo (por ejemplo, 2025)
     */
    public Curso(int idCurso, String nombre, int anio) {
        this.idCurso = idCurso;
        this.nombre = nombre;
        this.anio = anio;
    }

    // Getters y setters

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * Devuelve una representación legible del curso.
     */
    @Override
    public String toString() {
        return nombre;
    }
}
