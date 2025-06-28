/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de establecer la conexión con la base de datos MySQL del sistema SIGE.
 * Esta clase puede ser reutilizada desde cualquier controlador que necesite acceder a la base.
 * 
 * @author Yonatan
 */
public class conexionBD {

    // Datos de conexión
    private static final String URL = "jdbc:mysql://localhost:3306/sige";
    private static final String USUARIO = "root";
    private static final String CONTRASENIA = "corolla18";

    /**
     * Método estático que devuelve una conexión activa con la base de datos.
     * Se puede invocar desde cualquier clase que necesite hacer una consulta o modificación.
     * 
     * @return objeto Connection si la conexión fue exitosa, o null si hubo un error.
     */
    public static Connection obtenerConexion() {
        try {
            // Intentamos establecer la conexión con la base de datos
            Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENIA);
            System.out.println("Conexión exitosa a la base de datos SIGE.");
            return conexion;
        } catch (SQLException e) {
            // Si hay un error, lo mostramos por consola con un mensaje claro
            System.err.println("Error al conectar con la base de datos SIGE:");
            System.err.println(e.getMessage());
            return null;
        }
    }
}
