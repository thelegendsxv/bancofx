package co.edu.uniquindio.banco.modelo.entidades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase que representa un usuario del banco
 * @version 1.0
 * @autor caflorezvi
 */
@Getter
@Setter
@AllArgsConstructor
public class Usuario {

    private String id, nombre, direccion, email, password;

}
