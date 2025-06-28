/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;
import excepciones.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
/**
 *
 * @author Yonatan
 */




/**
 * Clase de utilidad con métodos estáticos para validaciones comunes del sistema SIGE.
 */
public class Validador {

    /**
     * Verifica si el estado ingresado es válido para una asistencia.
     *
     * @param estado estado a verificar
     * @throws EstadoAsistenciaInvalidoException si el estado no es válido
     */
    public static void validarEstadoAsistencia(String estado) throws EstadoAsistenciaInvalidoException {
        if (!(estado.equalsIgnoreCase("Presente") ||
              estado.equalsIgnoreCase("Ausente") ||
              estado.equalsIgnoreCase("Retirado"))) {
            throw new EstadoAsistenciaInvalidoException(estado);
        }
    }

    /**
     * Verifica si la calificación ingresada es válida según el tipo de actividad.
     *
     * @param valor valor ingresado (nota)
     * @param tipo tipo de calificación: "Numérica" o "Conceptual"
     * @throws CalificacionInvalidaException si el valor no se corresponde con el tipo
     */
    public static void validarCalificacion(String valor, String tipo) throws CalificacionInvalidaException {
        if (tipo.equalsIgnoreCase("Numérica")) {
            if (!(valor.matches("\\d+") &&
                  Integer.parseInt(valor) >= 1 && Integer.parseInt(valor) <= 10 ||
                  valor.equalsIgnoreCase("Ausente") ||
                  valor.equalsIgnoreCase("Ausente justificado") ||
                  valor.equalsIgnoreCase("No entregó"))) {
                throw new CalificacionInvalidaException(valor, tipo);
            }
        } else if (tipo.equalsIgnoreCase("Conceptual")) {
            if (!(valor.equalsIgnoreCase("Excelente") ||
                  valor.equalsIgnoreCase("Muy bien") ||
                  valor.equalsIgnoreCase("Bien") ||
                  valor.equalsIgnoreCase("Regular") ||
                  valor.equalsIgnoreCase("Mal") ||
                  valor.equalsIgnoreCase("Ausente") ||
                  valor.equalsIgnoreCase("Ausente justificado") ||
                  valor.equalsIgnoreCase("No entregó"))) {
                throw new CalificacionInvalidaException(valor, tipo);
            }
        } else {
            throw new CalificacionInvalidaException(valor, tipo);
        }
    }

    /**
     * Verifica que el nombre de curso sea válido (ej: 1A, 2B).
     *
     * @param nombre nombre ingresado
     * @throws NombreCursoInvalidoException si no cumple el patrón
     */
    public static void validarNombreCurso(String nombre) throws NombreCursoInvalidoException {
        if (!nombre.matches("[1-6][A-Z]")) {
            throw new NombreCursoInvalidoException(nombre);
        }
    }

    /**
     * Intenta parsear una fecha en formato dd/MM/yyyy.
     *
     * @param fechaStr fecha ingresada en formato texto
     * @return fecha como objeto LocalDate
     * @throws FechaInvalidaException si el formato no es válido
     */
    public static LocalDate parsearFecha(String fechaStr) throws FechaInvalidaException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(fechaStr, formatter);
        } catch (DateTimeParseException e) {
            throw new FechaInvalidaException(fechaStr);
        }
    }
}
