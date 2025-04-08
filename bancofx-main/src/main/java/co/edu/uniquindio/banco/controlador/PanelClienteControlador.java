package co.edu.uniquindio.banco.controlador;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import co.edu.uniquindio.banco.BancoApp;
import co.edu.uniquindio.banco.modelo.entidades.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Controlador del panel del cliente. Muestra la información del usuario,
 * permite realizar transferencias, consultar el saldo, actualizar datos y cerrar sesión.
 */
public class PanelClienteControlador {

    private Usuario usuario;
    private BilleteraVirtual billetera;
    private final Banco banco = Banco.getInstance();
    private final Sesion sesion = Sesion.getInstancia();
    private ObservableList<Transaccion> transacciones = FXCollections.observableArrayList();

    @FXML private ResourceBundle resources;
    @FXML private URL location;

    @FXML private Button btnCerrarSesion;
    @FXML private Button btnConsultarSaldo;
    @FXML private Button btnTransferir;
    @FXML private Button btnActualizarDatos;

    @FXML private TableView<Transaccion> tablaTransacciones;
    @FXML private TableColumn<Transaccion, String> colCategoria;
    @FXML private TableColumn<Transaccion, String> colFecha;
    @FXML private TableColumn<Transaccion, String> colTipo;
    @FXML private TableColumn<Transaccion, String> colUsuario;
    @FXML private TableColumn<Transaccion, Float> colValor;

    @FXML private Label lblBienvenida;
    @FXML private Label lblNumeroCuenta;

    /**
     * Inicializa el controlador después de que se haya cargado el archivo FXML.
     */
    @FXML
    void initialize() {
        configurarEventos();
        inicializarColumnasTabla();

        Platform.runLater(() -> {
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setOnCloseRequest(event -> cerrarSesion());
        });

        inicializarDatos(sesion.getUsuario());
    }

    /**
     * Configura los eventos de los botones principales.
     */
    private void configurarEventos() {
        btnTransferir.setOnAction(this::transferirDinero);
        btnCerrarSesion.setOnAction(this::cerrarSesion);
        btnConsultarSaldo.setOnAction(this::consultarSaldo);
        btnActualizarDatos.setOnAction(this::actualizarDatosUsuario);
    }

    /**
     * Define cómo se mostrarán las columnas de la tabla de transacciones.
     */
    private void inicializarColumnasTabla() {
        colFecha.setCellValueFactory(celda ->
                new SimpleStringProperty(celda.getValue().getFecha().toString()));

        colTipo.setCellValueFactory(celda ->
                new SimpleStringProperty(celda.getValue().getTipoTransaccion().name()));

        colCategoria.setCellValueFactory(celda ->
                new SimpleStringProperty(celda.getValue().getTipo().name()));

        colUsuario.setCellValueFactory(celda -> {
            String nombre = (celda.getValue().getBilleteraOrigen() == billetera) ?
                    celda.getValue().getBilleteraDestino().getUsuario().getNombre() :
                    celda.getValue().getBilleteraOrigen().getUsuario().getNombre();
            return new SimpleStringProperty(nombre);
        });
        colValor.setCellValueFactory(celda ->
                new SimpleFloatProperty(celda.getValue().getMonto()).asObject());

// Formateo personalizado: $ y 2 decimales
        colValor.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.2f", item)); // Ejemplo: $1,234.56
                }
            }
        });

    }

    /**
     * Inicializa los datos del cliente logueado y actualiza la interfaz.
     * @param usuario Usuario actualmente autenticado.
     */
    public void inicializarDatos(Usuario usuario) {
        this.usuario = usuario;
        this.billetera = banco.buscarBilleteraUsuario(usuario.getId());

        lblBienvenida.setText("¡Buen día, " + usuario.getNombre() + "!");
        lblNumeroCuenta.setText("Nro. Cuenta: " + billetera.getNumero());
        actualizarTablaTransacciones();
    }

    /**
     * Actualiza la tabla con el historial de transacciones del usuario.
     */
    private void actualizarTablaTransacciones() {
        transacciones = billetera.obtenerTransacciones()
                .stream()
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tablaTransacciones.setItems(transacciones);
    }

    /**
     * Muestra una ventana emergente con el saldo disponible.
     * @param event Evento del botón.
     */
    private void consultarSaldo(ActionEvent event) {
        float saldo = billetera.consultarSaldo();
        mostrarAlerta("Tu saldo actual es: $" + String.format("%.2f", saldo), Alert.AlertType.INFORMATION);
    }

    /**
     * Navega a la ventana para realizar una transferencia.
     * @param event Evento del botón.
     */
    private void transferirDinero(ActionEvent event) {
        abrirVentana("/transferencia.fxml", "Transferencia");
    }

    /**
     * Navega a la ventana para actualizar los datos del usuario.
     * @param event Evento del botón.
     */
    private void actualizarDatosUsuario(ActionEvent event) {
        abrirVentana("/actualizarUsuario.fxml", "Actualizar Perfil");
    }

    /**
     * Cierra la sesión del usuario actual y abre la ventana de inicio.
     */
    private void cerrarSesion() {
        sesion.cerrarSesion();
        abrirVentana("/inicio.fxml", "Banco");
    }

    /**
     * Evento del botón de cerrar sesión.
     * @param event Evento del botón.
     */
    private void cerrarSesion(ActionEvent event) {
        cerrarSesion();
    }

    /**
     * Abre una nueva ventana con la ruta FXML dada y cierra la actual.
     * @param rutaFxml Ruta del archivo FXML a abrir.
     * @param tituloVentana Título de la ventana.
     */
    private void abrirVentana(String rutaFxml, String tituloVentana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(Objects.requireNonNull(BancoApp.class.getResourceAsStream("/img/logo.png"))));
            stage.setScene(scene);
            stage.setTitle(tituloVentana);
            stage.setResizable(false);
            stage.show();
            cerrarVentanaActual();
        } catch (Exception e) {
            mostrarAlerta("No se pudo abrir la ventana: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Muestra una alerta con un mensaje y tipo determinado.
     * @param mensaje Texto a mostrar.
     * @param tipo Tipo de alerta.
     */
    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Mensaje del sistema");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(BancoApp.class.getResourceAsStream("/img/logo.png"))));

        alerta.showAndWait();
    }

    /**
     * Cierra la ventana actual del panel cliente.
     */
    private void cerrarVentanaActual() {
        Stage ventanaActual = (Stage) btnTransferir.getScene().getWindow();
        ventanaActual.close();
    }

}