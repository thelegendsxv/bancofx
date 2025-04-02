package co.edu.uniquindio.banco.modelo.vo;

import co.edu.uniquindio.banco.modelo.entidades.Transaccion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class SaldoTransaccionesBilletera {

    private float saldo;
    private List<Transaccion> transacciones;
}
