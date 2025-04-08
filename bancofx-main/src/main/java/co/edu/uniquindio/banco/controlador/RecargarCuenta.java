package co.edu.uniquindio.banco.controlador;


import co.edu.uniquindio.banco.modelo.entidades.Banco;
import co.edu.uniquindio.banco.modelo.entidades.BilleteraVirtual;
import co.edu.uniquindio.banco.modelo.entidades.Transaccion;
import co.edu.uniquindio.banco.modelo.enums.Categoria;
import co.edu.uniquindio.banco.modelo.enums.TipoTransaccion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class RecargarCuenta {

    @FXML
    private TextField txtNumeroCuenta;

    @FXML
    private TextField txtValorRecarga;

    private final Banco banco = Banco.getInstance();

    /**
     * Método que se ejecuta al presionar el botón de recargar.
     */
    @FXML
    void recargar(ActionEvent event) {
        String numeroCuenta = txtNumeroCuenta.getText().trim();
        String valorTexto = txtValorRecarga.getText().trim();

        if (numeroCuenta.isEmpty() || valorTexto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor, ingresa todos los datos.");
            return;
        }

        float valor;
        try {
            valor = Float.parseFloat(valorTexto);
            if (valor <= 0) {
                throw new NumberFormatException("El monto debe ser mayor a cero.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Valor inválido", e.getMessage());
            return;
        }

        try {
            BilleteraVirtual billetera = banco.buscarBilletera(numeroCuenta);
            if (billetera == null) {
                throw new Exception("La billetera no existe.");
            }

            Transaccion transaccion = new Transaccion(
                    TipoTransaccion.ENTRADA,
                    UUID.randomUUID().toString(),
                    valor,
                    LocalDateTime.now(),
                    Categoria.RECARGA,
                    billetera,
                    billetera,
                    0
            );

            billetera.depositar(valor, transaccion);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cuenta recargada correctamente.");
            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtNumeroCuenta.clear();
        txtValorRecarga.clear();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}