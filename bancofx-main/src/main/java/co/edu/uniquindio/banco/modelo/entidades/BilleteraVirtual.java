package co.edu.uniquindio.banco.modelo.entidades;

import co.edu.uniquindio.banco.config.Constantes;
import co.edu.uniquindio.banco.modelo.enums.TipoTransaccion;
import co.edu.uniquindio.banco.modelo.vo.PorcentajeGastosIngresos;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Representa una billetera virtual asociada a un usuario,
 * la cual permite realizar transacciones como depósitos y retiros.
 */
public class BilleteraVirtual {
    private String numero;
    private float saldo;
    private Usuario usuario;
    private ArrayList<Transaccion> transacciones;

    /**
     * Constructor de la clase BilleteraVirtual.
     *
     * @param numero Número único identificador de la billetera.
     * @param saldo Saldo inicial disponible en la billetera.
     * @param usuario Usuario propietario de la billetera.
     */
    public BilleteraVirtual(String numero, float saldo, Usuario usuario) {
        this.numero = numero;
        this.saldo = saldo;
        this.usuario = usuario;
        this.transacciones = new ArrayList<>();
    }

    /**
     * Verifica si la billetera tiene suficiente saldo (incluyendo la comisión).
     *
     * @param monto Monto a verificar.
     * @return true si el saldo es suficiente, false en caso contrario.
     */
    public boolean tieneSaldo(float monto) {
        float montoConComision = monto + Constantes.COMISION;
        return saldo >= montoConComision;
    }

    /**
     * Realiza un retiro de dinero de la billetera. Aplica la comisión correspondiente.
     *
     * @param monto Monto a retirar.
     * @param transaccion Transacción asociada al retiro.
     * @throws Exception si el monto es inválido o no hay suficiente saldo.
     */
    public void retirar(float monto, Transaccion transaccion) throws Exception {
        float montoConComision = monto + Constantes.COMISION;

        if (montoConComision <= 0) {
            throw new Exception("El monto a retirar debe ser mayor a cero");
        }

        transaccion.setComision(Constantes.COMISION);

        saldo -= montoConComision;
        System.out.println("Se ha hecho");
        transacciones.add(transaccion);
    }

    /**
     * Realiza un depósito en la billetera.
     *
     * @param monto Monto a depositar.
     * @param transaccion Transacción asociada al depósito.
     * @throws Exception si el monto es inválido o si no se asigna billetera origen.
     */
    public void depositar(float monto, Transaccion transaccion) throws Exception {
        if (monto <= 0) {
            throw new Exception("El monto a depositar debe ser mayor a cero");
        }

        if (transaccion.getBilleteraOrigen() == null) {
            throw new Exception("La transacción no tiene una billetera origen asignada");
        }

        saldo += monto;
        System.out.println("Se ha hecho");
        transacciones.add(transaccion);
    }

    /**
     * Obtiene todas las transacciones realizadas dentro de un periodo de tiempo específico.
     *
     * @param inicio Fecha de inicio del periodo.
     * @param fin Fecha de fin del periodo.
     * @return Lista de transacciones dentro del periodo indicado.
     */
    public ArrayList<Transaccion> obtenerTransaccionesPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        ArrayList<Transaccion> transaccionesMes = new ArrayList<>();

        for (int i = 0; i < transacciones.size(); i++) {
            if (transacciones.get(i).getFecha().isAfter(inicio) && transacciones.get(i).getFecha().isBefore(fin)) {
                transaccionesMes.add(transacciones.get(i));
            }
        }
        return transaccionesMes;
    }

    /**
     * Calcula el porcentaje de gastos e ingresos de la billetera en un mes y año específico.
     *
     * @param mes Mes del año (1 a 12).
     * @param anio Año correspondiente.
     * @return Objeto con los porcentajes de gastos e ingresos.
     */
    public PorcentajeGastosIngresos obtenerPorcentajeGastosIngresos(int mes, int anio) {
        float ingresos = 0;
        float egresos = 0; // gastos

        for (Transaccion transaccion : transacciones) {
            if (transaccion.getFecha().getMonthValue() == mes && transaccion.getFecha().getYear() == anio) {
                if (transaccion.getBilleteraOrigen() == this) {
                    egresos += transaccion.getMonto() + transaccion.getComision();
                } else {
                    ingresos += transaccion.getMonto();
                }
            }
        }

        float total = ingresos + egresos;
        float porcentajeGastos = (egresos / total) * 100;
        float porcentajeIngresos = (ingresos / total) * 100;

        return new PorcentajeGastosIngresos(porcentajeGastos, porcentajeIngresos);
    }

    /**
     * Obtiene la lista de todas las transacciones asociadas a la billetera.
     *
     * @return Lista de transacciones.
     */
    public ArrayList<Transaccion> obtenerTransacciones() {
        return transacciones;
    }

    /**
     * Obtiene el número de la billetera.
     *
     * @return Número de la billetera.
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Consulta el saldo actual disponible en la billetera.
     *
     * @return Saldo disponible.
     */
    public float consultarSaldo() {
        return saldo;
    }

    /**
     * Obtiene el usuario propietario de la billetera.
     *
     * @return Usuario asociado.
     */
    public Usuario getUsuario() {
        return usuario;
    }
}