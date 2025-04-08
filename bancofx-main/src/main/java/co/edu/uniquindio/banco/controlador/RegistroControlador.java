package co.edu.uniquindio.banco.controlador;

import co.edu.uniquindio.banco.BancoApp;
import co.edu.uniquindio.banco.modelo.entidades.Banco;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Controlador de la interfaz de registro de usuarios en el sistema bancario.
 * Gestiona la lógica de validación y registro de nuevos usuarios.
 *
 * Autor: caflorezvi
 */
public class RegistroControlador {

    // =================== ATRIBUTOS ===================

    /**
     * Campo de texto para la identificación del usuario.
     */
    @FXML
    private TextField txtIdentificacion;

    /**
     * Campo de texto para el nombre del usuario.
     */
    @FXML
    private TextField txtNombre;

    /**
     * Campo de texto para el correo electrónico del usuario.
     */
    @FXML
    private TextField txtCorreo;

    /**
     * Campo de texto para la dirección del usuario.
     */
    @FXML
    private TextField txtDireccion;

    /**
     * Campo de texto para la contraseña del usuario.
     */
    @FXML
    private PasswordField txtPassword;

    /**
     * Botón para realizar el registro.
     */
    @FXML
    private Button btnRegistrar;

    /**
     * Instancia del banco para gestionar el registro.
     */
    private final Banco banco = Banco.getInstance();

    // =================== MÉTODOS ===================

    /**
     * Método llamado automáticamente al cargar la vista FXML.
     * Inicializa los componentes y configura los eventos de los botones.
     */
    @FXML
    void initialize() {
        // Validación de inyección FXML
        assert txtCorreo != null : "fx:id=\"txtCorreo\" was not injected: check your FXML file 'registro.fxml'.";
        assert txtDireccion != null : "fx:id=\"txtDireccion\" was not injected: check your FXML file 'registro.fxml'.";
        assert txtIdentificacion != null : "fx:id=\"txtIdentificacion\" was not injected: check your FXML file 'registro.fxml'.";
        assert txtNombre != null : "fx:id=\"txtNombre\" was not injected: check your FXML file 'registro.fxml'.";
        assert txtPassword != null : "fx:id=\"txtPassword\" was not injected: check your FXML file 'registro.fxml'.";

        // Configura cierre de ventana
        Platform.runLater(() -> {
            Stage stage = (Stage) btnRegistrar.getScene().getWindow();
            stage.setOnCloseRequest(event -> irPrincipal());
        });

        // Acción del botón registrar
        btnRegistrar.setOnAction(this::registrarse);
    }

    /**
     * Método que se ejecuta cuando el usuario presiona el botón de registrar.
     * Intenta registrar un nuevo usuario en el sistema.
     *
     * @param actionEvent Evento del botón.
     */
    public void registrarse(ActionEvent actionEvent) {
        try {
            // Registro del usuario en el banco
            banco.registrarUsuario(
                    txtIdentificacion.getText(),
                    txtNombre.getText(),
                    txtDireccion.getText(),
                    txtCorreo.getText(),
                    txtPassword.getText()
            );

            // Éxito: muestra alerta e ir a pantalla principal
            crearAlerta("Usuario registrado correctamente", Alert.AlertType.INFORMATION);
            irPrincipal();

        } catch (Exception e) {
            // Error: muestra alerta con el mensaje
            crearAlerta(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Muestra una alerta en pantalla.
     *
     * @param mensaje Mensaje de la alerta.
     * @param tipo Tipo de alerta.
     */
    public void crearAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(BancoApp.class.getResourceAsStream("/img/S.png"))));
        alert.setTitle("Alerta");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }

    /**
     * Cierra la ventana actual del registro.
     */
    public void cerrarVentana() {
        Stage stage = (Stage) txtIdentificacion.getScene().getWindow();
        stage.close();
    }

    /**
     * Navega a la ventana principal de inicio.
     */
    public void irPrincipal() {
        navegarVentanaPrincipal("/inicio.fxml", "Banco");
    }

    /**
     * Carga y muestra una nueva ventana a partir del archivo FXML especificado.
     *
     * @param rutaFxml Ruta al archivo FXML.
     * @param tituloVentana Título de la ventana.
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

    public void recargar(ActionEvent actionEvent) {
    }
}