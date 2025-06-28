package controlador;

import gestionSIGE.SIGEAppSwing;
import modelo.Usuario;
import vista.*;

import conexion.conexionBD;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador que gestiona el inicio de sesión, la carga de roles desde la base de datos
 * y la navegación del usuario según el rol seleccionado. Esta versión ya utiliza JDBC
 * y respeta el uso de rolActivo dentro del objeto Usuario.
 * 
 * @author Yonatan
 */
public class ControladorUsuario {

    /**
     * Autentica al usuario con los datos ingresados (email y contraseña).
     * Si el login es correcto, se retorna el objeto Usuario completo, con roles cargados.
     *
     * @param email Correo electrónico ingresado
     * @param contrasenia Clave ingresada
     * @return Usuario autenticado o null si no existe en la base
     */
    public static Usuario autenticarUsuario(String email, String contrasenia) {
        Usuario usuario = null;

        String sql = "SELECT * FROM usuario WHERE email = ? AND contrasenia = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, contrasenia);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("idUsuario");
                String dni = rs.getString("dni");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String telefono = rs.getString("telefono");

                usuario = new Usuario(id, dni, nombre, apellido, telefono, email, contrasenia);

                // Cargamos los roles desde la base
                cargarRoles(usuario);
            }

        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario:");
            System.err.println(e.getMessage());
        }

        return usuario;
    }

    /**
     * Consulta los roles del usuario en la tabla usuarios_rol y los guarda en su lista.
     * 
     * @param usuario Usuario ya autenticado
     */
    private static void cargarRoles(Usuario usuario) {
        String sql = "SELECT r.tipoRol FROM rol r " +
                     "JOIN usuarios_rol ur ON r.idRol = ur.idRol " +
                     "WHERE ur.idUsuario = ?";

        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuario.getIdUsuario());

            ResultSet rs = stmt.executeQuery();
            List<String> roles = new ArrayList<>();

            while (rs.next()) {
                roles.add(rs.getString("tipoRol"));
            }

            usuario.setRoles(roles);

        } catch (SQLException e) {
            System.err.println("Error al cargar los roles:");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Verifica si el usuario tiene uno o varios roles.
     * Si tiene uno, se redirige directo al menú. Si tiene más de uno, se muestra selector.
     *
     * @param usuario Usuario autenticado
     */
    public static void redirigirSegunRoles(Usuario usuario) {
        SIGEAppSwing.setUsuarioActual(usuario); // Guarda la sesión

        List<String> roles = usuario.getRoles();

        if (roles.size() == 1) {
            String nombreRol = roles.get(0);
            usuario.setRolActivo(nombreRol); // Guarda el rol activo
            redirigirMenuPorRol(usuario);
        } else {
            // Si tiene múltiples roles, se abre el selector
            JFrame selector = new SelectorDeRoles(usuario);
            selector.setVisible(true);
        }
    }

        /**
     * Abre la ventana del menú correspondiente al rol activo del usuario.
     * Soporta nombres personalizados de roles como "Coordinador Pedagógico (Preceptor)".
     *
     * @param usuario Usuario autenticado con rolActivo ya definido
     */
    public static void redirigirMenuPorRol(Usuario usuario) {
        String nombreRol = usuario.getRolActivo();
        JFrame menu = null;

        if (nombreRol == null) {
            JOptionPane.showMessageDialog(null,
                    "No se ha seleccionado ningún rol.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            SIGEAppSwing.mostrarLogin();
            return;
        }

        String rol = nombreRol.toLowerCase();

        // Se permite flexibilidad con nombres personalizados
        if (rol.contains("docente")) {
            menu = new VentanaDocente(usuario);
        } else if (rol.contains("preceptor")) {
            menu = new VentanaPreceptor(usuario, nombreRol);
        } else if (rol.contains("asesor")) {
            menu = new VentanaAsesor(usuario);
        } else if (rol.contains("directivo")) {
            menu = new VentanaDirectivo(usuario);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Rol no reconocido: " + nombreRol,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            SIGEAppSwing.mostrarLogin();
            return;
        }

        if (menu != null) {
            menu.setVisible(true);
        }
    }

}
