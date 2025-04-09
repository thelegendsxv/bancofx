package co.edu.uniquindio.banco.modelo.entidades;

import co.edu.uniquindio.banco.config.Constantes;
import co.edu.uniquindio.banco.modelo.enums.Categoria;
import co.edu.uniquindio.banco.modelo.enums.TipoTransaccion;
import co.edu.uniquindio.banco.modelo.vo.PorcentajeGastosIngresos;
import co.edu.uniquindio.banco.modelo.vo.SaldoTransaccionesBilletera;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Clase que representa la lógica del banco y gestiona a los usuarios y sus billeteras virtuales.
 * Implementa el patrón Singleton para garantizar una única instancia.
 *
 * @author caflorezvi
 * @version 1.0
 */
@Getter
@Setter
public class Banco {

    private static Banco instancia;
    private List<Usuario> usuarios;
    private List<BilleteraVirtual> billeteras;

    // Constructor privado para el patrón Singleton
    private Banco() {
        this.usuarios = new ArrayList<>();
        this.billeteras = new ArrayList<>();
    }

    /**
     * Obtiene la única instancia del banco.
     *
     * @return instancia del banco
     */
    public static Banco getInstance() {
        if (instancia == null) {
            instancia = new Banco();
        }
        return instancia;
    }

    // ---------------------- Registro de Usuarios y Billeteras ----------------------

    /**
     * Registra un nuevo usuario con una billetera inicial.
     *
     * @param id         Identificación del usuario
     * @param nombre     Nombre completo
     * @param direccion  Dirección física
     * @param email      Correo electrónico
     * @param password   Contraseña
     * @throws Exception si los datos son inválidos o el usuario ya existe
     */
    public void registrarUsuario(String id, String nombre, String direccion, String email, String password) throws Exception {
        if (id == null || id.isEmpty()) throw new Exception("El id es obligatorio");
        if (nombre == null || nombre.isEmpty()) throw new Exception("El nombre es obligatorio");
        if (direccion == null || direccion.isEmpty()) throw new Exception("La dirección es obligatoria");
        if (email == null || email.isEmpty()) throw new Exception("El email es obligatorio");
        if (password == null || password.isEmpty()) throw new Exception("La contraseña es obligatoria");

        if (buscarUsuario(id) != null) throw new Exception("El usuario ya existe");

        Usuario usuario = new Usuario(id, nombre, direccion, email, password);
        usuarios.add(usuario);
        registrarBilletera(usuario);
    }

    /**
     * Crea y asocia una billetera virtual a un usuario.
     *
     * @param usuario Usuario al que se le asigna la billetera
     */
    public void registrarBilletera(Usuario usuario) {
        String numero = crearNumeroBilletera();
        BilleteraVirtual billetera = new BilleteraVirtual(numero, 0, usuario);
        billeteras.add(billetera);
    }

    // ---------------------- Generación y Búsqueda ----------------------

    private String crearNumeroBilletera() {
        String numero = generarNumeroAleatorio();
        while (buscarBilletera(numero) != null) {
            numero = generarNumeroAleatorio();
        }
        return numero;
    }

    private String generarNumeroAleatorio() {
        StringBuilder numero = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            numero.append((int) (Math.random() * 10));
        }
        return numero.toString();
    }

    public BilleteraVirtual buscarBilletera(String numero) {
        return billeteras.stream()
                .filter(b -> b.getNumero().equals(numero))
                .findFirst()
                .orElse(null);
    }

    public BilleteraVirtual buscarBilleteraUsuario(String idUsuario) {
        return billeteras.stream()
                .filter(b -> b.getUsuario().getId().equals(idUsuario))
                .findFirst()
                .orElse(null);
    }

    public Usuario buscarUsuario(String id) {
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean verificarUsuario(String id, String contraseña) {
        return billeteras.stream()
                .anyMatch(b -> b.getUsuario().getId().equals(id) && b.getUsuario().getPassword().equals(contraseña));
    }

    // ---------------------- Operaciones con Billetera ----------------------

    /**
     * Consulta el saldo y transacciones de un usuario autenticado.
     *
     * @param numeroIdentificacion ID del usuario
     * @param password             Contraseña
     * @return objeto con saldo y lista de transacciones
     * @throws Exception si el usuario no existe o la contraseña es incorrecta
     */
    public SaldoTransaccionesBilletera consultarSaldoYTransacciones(String numeroIdentificacion, String password) throws Exception {
        Usuario usuario = buscarUsuario(numeroIdentificacion);
        if (usuario == null) throw new Exception("El usuario no existe");
        if (!usuario.getPassword().equals(password)) throw new Exception("Contraseña incorrecta");

        BilleteraVirtual billetera = buscarBilleteraUsuario(usuario.getId());
        return new SaldoTransaccionesBilletera(billetera.consultarSaldo(), billetera.obtenerTransacciones());
    }

    public void recargarBilletera(String numeroBilletera, float monto) throws Exception {
        BilleteraVirtual billetera = buscarBilletera(numeroBilletera);
        if (billetera == null) throw new Exception("La billetera no existe");

        Transaccion transaccion = new Transaccion(
                TipoTransaccion.DEPOSITO,
                UUID.randomUUID().toString(),
                monto,
                LocalDateTime.now(),
                Categoria.RECARGA,
                billetera,
                billetera,
                0
        );
        billetera.depositar(monto, transaccion);
    }
//realizar transferencia
    public void realizarTransferencia(String numeroBilleteraOrigen, String numeroBilleteraDestino, float monto, Categoria categoria) throws Exception {
        BilleteraVirtual billeteraOrigen = buscarBilletera(numeroBilleteraOrigen);
        BilleteraVirtual billeteraDestino = buscarBilletera(numeroBilleteraDestino);

        if (billeteraOrigen == null || billeteraDestino == null) throw new Exception("La billetera no existe");
        if (!billeteraOrigen.tieneSaldo(monto)) throw new Exception("Saldo insuficiente");

        Transaccion salida = new Transaccion(TipoTransaccion.RETIRO, UUID.randomUUID().toString(), monto, LocalDateTime.now(), categoria, billeteraOrigen, billeteraDestino, Constantes.COMISION);
        Transaccion entrada = new Transaccion(TipoTransaccion.DEPOSITO, UUID.randomUUID().toString(), monto, LocalDateTime.now(), categoria, billeteraOrigen, billeteraDestino, Constantes.COMISION);

        billeteraOrigen.retirar(monto, salida);
        billeteraDestino.depositar(monto, entrada);
    }

    // ---------------------- Reportes ----------------------

    public List<Transaccion> obtenerTransacciones(String numeroBilletera) {
        BilleteraVirtual billetera = buscarBilletera(numeroBilletera);
        return (billetera != null) ? billetera.obtenerTransacciones() : new ArrayList<>();
    }

    public List<Transaccion> obtenerTransaccionesPeriodo(String numeroBilletera, LocalDateTime inicio, LocalDateTime fin) {
        BilleteraVirtual billetera = buscarBilletera(numeroBilletera);
        return (billetera != null) ? billetera.obtenerTransaccionesPeriodo(inicio, fin) : new ArrayList<>();
    }

    public PorcentajeGastosIngresos obtenerPorcentajeGastosIngresos(String numeroBilletera, int mes, int anio) throws Exception {
        BilleteraVirtual billetera = buscarBilletera(numeroBilletera);
        if (billetera == null) throw new Exception("La billetera no existe");

        return billetera.obtenerPorcentajeGastosIngresos(mes, anio);
    }
}