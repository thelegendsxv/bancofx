package co.edu.uniquindio.banco.modelo.entidades;

import lombok.Getter;
import lombok.Setter;

/**
 * Clase singleton que representa la sesión actual de un usuario en el sistema.
 * Permite acceder al usuario autenticado desde cualquier parte de la aplicación
 * y gestionar el cierre de sesión.
 */
public class Sesion {

    /**
     * Instancia única de la sesión (patrón Singleton).
     */
    public static Sesion INSTANCIA;

    /**
     * Usuario actualmente autenticado en la sesión.
     */
    @Getter @Setter
    private Usuario usuario;

    /**
     * Constructor privado para evitar la creación de instancias externas.
     */
    private Sesion() {
    }

    /**
     * Retorna la instancia única de la sesión.
     * Si no existe, la crea.
     *
     * @return instancia única de {@code Sesion}.
     */
    public static Sesion getInstancia() {
        if (INSTANCIA == null) {
            INSTANCIA = new Sesion();
        }
        return INSTANCIA;
    }

    /**
     * Cierra la sesión actual, eliminando al usuario autenticado.
     */
    public void cerrarSesion() {
        usuario = null;
    }

}