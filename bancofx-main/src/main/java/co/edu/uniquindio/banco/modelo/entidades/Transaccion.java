package co.edu.uniquindio.banco.modelo.entidades;

import co.edu.uniquindio.banco.modelo.enums.Categoria;
import co.edu.uniquindio.banco.modelo.enums.TipoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Clase que representa una transacción bancaria
 * @version 1.0
 * @author caflorezvi
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Transaccion {
    private TipoTransaccion tipoTransaccion;
    private String id;
    private float monto;
    private LocalDateTime fecha;
    private Categoria tipo;
    private BilleteraVirtual billeteraOrigen, billeteraDestino;
    private float comision;

}