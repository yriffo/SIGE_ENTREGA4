/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.List;
import java.util.Map;
import java.util.HashMap;


/**
 *
 * @author Yonatan
 */



/**
 * Clase que representa la planilla de calificaciones como una estructura matricial.
 * Cada fila corresponde a un estudiante, y cada columna a una actividad de una materia.
 */
public class PlanillaCalificaciones {

    // Mapa de calificaciones por estudiante -> (actividad -> calificación)
    private Map<Integer, Map<Integer, Calificacion>> calificaciones;

    /**
     * Constructor que inicializa la estructura.
     */
    public PlanillaCalificaciones() {
        calificaciones = new HashMap<>();
    }

    /**
     * Agrega o actualiza una calificación en la planilla.
     * @param estudianteId ID del estudiante
     * @param actividadId ID de la actividad
     * @param calificacion objeto Calificacion a registrar
     */
    public void agregarCalificacion(int estudianteId, int actividadId, Calificacion calificacion) {
        // Si el estudiante no tiene calificaciones aún, crear su mapa
        if (!calificaciones.containsKey(estudianteId)) {
            calificaciones.put(estudianteId, new HashMap<>());
        }
        // Agregar o actualizar la calificación para la actividad
        calificaciones.get(estudianteId).put(actividadId, calificacion);
    }

    /**
     * Obtiene la calificación de un estudiante en una actividad determinada.
     * @param estudianteId ID del estudiante
     * @param actividadId ID de la actividad
     * @return Calificacion correspondiente, o null si no existe
     */
    public Calificacion getCalificacion(int estudianteId, int actividadId) {
        if (calificaciones.containsKey(estudianteId)) {
            Map<Integer, Calificacion> calPorActividad = calificaciones.get(estudianteId);
            if (calPorActividad != null && calPorActividad.containsKey(actividadId)) {
                return calPorActividad.get(actividadId);
            }
        }
        return null;
    }

    /**
     * Devuelve el mapa completo de calificaciones.
     * @return estructura (estudiante -> (actividad -> calificación))
     */
    public Map<Integer, Map<Integer, Calificacion>> getCalificaciones() {
        return calificaciones;
    }
    
    /**
    * Devuelve el mapa de calificaciones de un estudiante específico.
    * Si no tiene calificaciones registradas, retorna un mapa vacío.
    *
    * @param idEstudiante ID del estudiante
    * @return Mapa de calificaciones por actividad
    */
   public Map<Integer, Calificacion> obtenerCalificacionesPorEstudiante(int idEstudiante) {
       return calificaciones.getOrDefault(idEstudiante, new HashMap<>());
   }

}
