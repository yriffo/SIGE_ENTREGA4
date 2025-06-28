package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase base para representar un usuario del sistema SIGE.
 * Contiene información personal y de acceso.
 * Puede ser extendida por roles específicos como Docente, Preceptor, etc.
 * Permite asignar múltiples roles a un mismo usuario.
 * 
 * @author Yonatan
 */
public class Usuario {

    // Atributos básicos de identificación
    private int idUsuario;
    private String dni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String contrasenia;

    // Roles asignados (ej: Docente, Preceptor, etc.)
    private List<String> roles = new ArrayList<>();

    // Rol activo actual (determina qué menú se muestra al iniciar sesión)
    private String rolActivo;

    /**
     * Constructor vacío.
     * Útil para frameworks o carga dinámica por reflexión.
     */
    public Usuario() {}

    /**
     * Constructor corto.
     * Usado para cargar un usuario con información mínima (por ejemplo, desde la base de datos).
     * No incluye datos sensibles como contraseña ni roles.
     * 
     * @param idUsuario ID del usuario
     * @param nombre Nombre del usuario
     * @param apellido Apellido del usuario
     * @param email Correo electrónico
     */
    public Usuario(int idUsuario, String nombre, String apellido, String email) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    /**
     * Constructor completo.
     * Usado por las subclases como Docente, Preceptor, Asesor, Directivo.
     * 
     * @param idUsuario ID del usuario
     * @param dni Documento nacional de identidad
     * @param nombre Nombre del usuario
     * @param apellido Apellido
     * @param telefono Teléfono de contacto
     * @param email Correo electrónico
     * @param contrasenia Contraseña de acceso al sistema
     */
    public Usuario(int idUsuario, String dni, String nombre, String apellido,
                   String telefono, String email, String contrasenia) {
        this.idUsuario = idUsuario;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.contrasenia = contrasenia;
    }

    // ====================== Getters y Setters ======================

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getRolActivo() {
        return rolActivo;
    }

    public void setRolActivo(String rolActivo) {
        this.rolActivo = rolActivo;
    }

    /**
     * Representación textual del usuario.
     * Puede sobreescribirse en subclases si se requiere personalización.
     */
    @Override
    public String toString() {
        return apellido + ", " + nombre;
    }
    
    public String getRol() {
    return rolActivo;  // o "Usuario" como valor por defecto
    }


}
