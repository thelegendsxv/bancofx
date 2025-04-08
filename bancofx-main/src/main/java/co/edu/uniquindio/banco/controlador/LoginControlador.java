package co.edu.uniquindio.banco.controlador;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import co.edu.uniquindio.banco.BancoApp;
import co.edu.uniquindio.banco.modelo.entidades.Banco;
import co.edu.uniquindio.banco.modelo.entidades.Sesion;
import co.edu.uniquindio.banco.modelo.entidades.Usuario;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Controlador para la ventana de inicio de sesión.
 * Permite a los usuarios autenticarse y acceder al panel principal del sistema.
 */
public class LoginControlador {

    @FXML private ResourceBundle resources;
    @FXML private URL location;

    @FXML private TextField IdTextField;
    @FXML private Button IniciarSesionButton;
    @FXML private PasswordField passwordField;

    private final Banco banco = Banco.getInstance();
    private final Sesion sesion = Sesion.getInstancia();

    /**
     * Inicializa el controlador una vez que se carga el archivo FXML.
     * Configura eventos y comportamientos iniciales de la interfaz.
     */
    @FXML
    void initialize() {
        assert IdTextField != null : "fx:id=\"IdTextField\" was not injected.";
        assert IniciarSesionButton != null : "fx:id=\"IniciarSesionButton\" was not injected.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected.";

        Platform.runLater(() -> {
            Stage stage = (Stage) IniciarSesionButton.getScene().getWindow();
            stage.setOnCloseRequest(event -> irPanelInicio());
        });

        IniciarSesionButton.setOnAction(this::iniciarSesion);
    }

    /**
     * Inicia sesión del usuario al hacer clic en el botón correspondiente.
     * Valida credenciales y navega al panel de cliente si son correctas.
     * @param actionEvent Evento de clic del botón.
     */
    private void iniciarSesion(ActionEvent actionEvent) {
        try {
            String id = IdTextField.getText();
            String password = passwordField.getText();

            if (id.isEmpty() || password.isEmpty()) {
                crearAlerta("Por favor, complete todos los campos.", Alert.AlertType.WARNING);
                return;
            }

            if (!banco.verificarUsuario(id, password)) {
                crearAlerta("Usuario o contraseña incorrectos.", Alert.AlertType.ERROR);
                return;
            }

            Usuario usuario = banco.buscarUsuario(id);
            sesion.setUsuario(usuario);

            crearAlerta("Inicio de sesión exitoso", Alert.AlertType.INFORMATION);
            cerrarVentana();
            irPanelCliente();

        } catch (Exception e) {
            crearAlerta("Ocurrió un error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Cierra la ventana de inicio de sesión actual.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) IdTextField.getScene().getWindow();
        stage.close();
    }

    /**
     * Muestra una alerta en pantalla con un mensaje y tipo especificado.
     * @param mensaje Texto a mostrar.
     * @param tipo Tipo de alerta (INFORMATION, ERROR, WARNING, etc).
     */
    private void crearAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(BancoApp.class.getResourceAsStream("/img/logo.png"))));
        alert.setTitle("Mensaje del sistema");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Navega al panel del cliente luego de iniciar sesión correctamente.
     */
    private void irPanelCliente() {
        navegarVentana("/panelCliente.fxml", "Banco - Panel Cliente");
    }

    /**
     * Navega a una ventana general del sistema.
     * @param rutaFxml Ruta del archivo FXML a cargar.
     * @param tituloVentana Título de la nueva ventana.
     */
    private void navegarVentana(String rutaFxml, String tituloVentana) {
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

        } catch (Exception e) {
            crearAlerta("No se pudo abrir la ventana: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Abre nuevamente el panel de inicio si se cierra la ventana de login.
     */
    public void irPanelInicio() {
        navegarVentanaPrincipal("/inicio.fxml", "Banco");
    }

    /**
     * Abre una ventana principal del sistema y cierra la actual.
     * @param rutaFxml Ruta del archivo FXML.
     * @param tituloVentana Título de la nueva ventana.
     */
    private void navegarVentanaPrincipal(String rutaFxml, String tituloVentana) {
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
            cerrarVentana();

        } catch (Exception e) {
            crearAlerta("No se pudo abrir la ventana: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}