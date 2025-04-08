package co.edu.uniquindio.banco.controlador;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

import co.edu.uniquindio.banco.BancoApp;
import co.edu.uniquindio.banco.modelo.entidades.Banco;
import co.edu.uniquindio.banco.modelo.entidades.BilleteraVirtual;
import co.edu.uniquindio.banco.modelo.entidades.Sesion;
import co.edu.uniquindio.banco.modelo.enums.Categoria;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Controlador para la vista de Transferencia.
 * Permite realizar una transferencia entre billeteras del sistema bancario virtual.
 * Controla los eventos de la interfaz y comunica con la lógica del modelo (Banco).
 */
public class TransferenciaControlador {

    // ------------------ Atributos ------------------

    private final Banco banco = Banco.getInstance();
    private BilleteraVirtual billeteraOrigen;
    private final Sesion sesion = Sesion.getInstancia();

    @FXML private ResourceBundle resources;
    @FXML private URL location;

    @FXML private ComboBox<Categoria> CategoriaBox;
    @FXML private TextField MontoTransferirField;
    @FXML private TextField NumeroCuentafield;
    @FXML private Button TransferirButton;

    // ------------------ Inicialización ------------------

    /**
     * Método que se ejecuta al cargar la vista. Inicializa componentes y listeners.
     */
    @FXML
    void initialize() {
        inicializarDatos();

        assert CategoriaBox != null : "fx:id=\"CategoriaBox\" no fue inyectado. Verifica el FXML 'transferencia.fxml'.";
        assert MontoTransferirField != null : "fx:id=\"MontoTransferirField\" no fue inyectado.";
        assert NumeroCuentafield != null : "fx:id=\"NumeroCuentafield\" no fue inyectado.";
        assert TransferirButton != null : "fx:id=\"TransferirButton\" no fue inyectado.";

        CategoriaBox.getItems().addAll(Categoria.values());

        Platform.runLater(() -> {
            Stage stage = (Stage) TransferirButton.getScene().getWindow();
            stage.setOnCloseRequest(event -> irPanelCliente());
        });

        TransferirButton.setOnAction(e -> {
            try {
                realizarTransferencia();
            } catch (Exception ex) {
                mostrarAlerta("Error", ex.getMessage());
            }
        });
    }

    /**
     * Inicializa la billetera de origen del usuario en sesión.
     */
    public void inicializarDatos() {
        this.billeteraOrigen = banco.buscarBilleteraUsuario(sesion.getUsuario().getId());
    }

    // ------------------ Lógica de Transferencia ------------------

    /**
     * Realiza la transferencia a otra billetera si se cumplen las validaciones.
     *
     * @throws Exception en caso de campos vacíos, monto inválido o errores de lógica.
     */
    private void realizarTransferencia() throws Exception {
        String numeroDestino = NumeroCuentafield.getText().trim();
        String montoTexto = MontoTransferirField.getText().trim();
        Categoria categoria = CategoriaBox.getValue();

        if (numeroDestino.isEmpty() || montoTexto.isEmpty() || categoria == null) {
            throw new Exception("Por favor completa todos los campos.");
        }

        float monto;
        try {
            monto = Float.parseFloat(montoTexto);
        } catch (NumberFormatException e) {
            throw new Exception("El monto debe ser un número válido.");
        }

        BilleteraVirtual billeteraDestino = banco.buscarBilletera(numeroDestino);
        if (billeteraDestino == null) throw new Exception("La billetera destino no existe.");

        if (billeteraOrigen.getNumero().equals(billeteraDestino.getNumero())) {
            throw new Exception("No puedes transferirte a tu propia billetera.");
        }

        if (!billeteraOrigen.tieneSaldo(monto)) {
            throw new Exception("Saldo insuficiente para realizar la transferencia.");
        }

        banco.realizarTransferencia(billeteraOrigen.getNumero(), numeroDestino, monto, categoria);

        mostrarAlerta("Éxito", "Transferencia realizada correctamente.");
        limpiarCampos();
    }

    /**
     * Limpia los campos del formulario después de realizar la transferencia.
     */
    private void limpiarCampos() {
        MontoTransferirField.clear();
        NumeroCuentafield.clear();
        CategoriaBox.getSelectionModel().clearSelection();
    }

    // ------------------ Ventanas y Navegación ------------------

    /**
     * Cierra la ventana actual.
     */
    public void cerrarVentana() {
        Stage ventanaActual = (Stage) TransferirButton.getScene().getWindow();
        ventanaActual.close();
    }

    /**
     * Abre el panel del cliente cuando se cierra esta ventana.
     */
    private void irPanelCliente() {
        navegarVentana("/panelCliente.fxml", "Banco - Panel Cliente");
    }

    /**
     * Abre una nueva ventana a partir de un archivo FXML.
     *
     * @param rutaFxml Ruta del archivo FXML
     * @param tituloVentana Título de la nueva ventana
     */
    private void navegarVentana(String rutaFxml, String tituloVentana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(Objects.requireNonNull(BancoApp.class.getResourceAsStream("/img/S.png"))));
            stage.setScene(scene);
            stage.setTitle(tituloVentana);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            crearAlerta("No se pudo abrir la ventana: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ------------------ Utilidades ------------------

    /**
     * Muestra una alerta informativa al usuario.
     *
     * @param titulo Título de la alerta
     * @param mensaje Contenido del mensaje
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(BancoApp.class.getResourceAsStream("/img/logo.png"))));
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }

    /**
     * Muestra una alerta con un tipo específico.
     *
     * @param mensaje Contenido del mensaje
     * @param tipo Tipo de alerta (ERROR, WARNING, etc.)
     */
    public void crearAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Alerta");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}