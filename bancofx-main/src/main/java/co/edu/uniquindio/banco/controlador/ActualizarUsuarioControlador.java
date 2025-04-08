package co.edu.uniquindio.banco.controlador;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import co.edu.uniquindio.banco.BancoApp;
import co.edu.uniquindio.banco.modelo.entidades.Sesion;
import co.edu.uniquindio.banco.modelo.entidades.Usuario;
import javafx.application.Platform;
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

/**
 * Controlador de la vista de actualización de datos de usuario.
 * Permite que el usuario modifique su información personal y la guarde en su sesión actual.
 * La información incluye: nombre, identificación, dirección, correo y contraseña.
 * Se carga la información desde la sesión activa.
 *
 * @author caflorezvi
 */
public class ActualizarUsuarioControlador {

    /**
     * Usuario actual que está iniciando sesión.
     */
    private Usuario usuario;

    /**
     * Instancia única de la sesión actual (patrón Singleton).
     */
    Sesion sesion = Sesion.getInstancia();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnActualizar;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtDireccion;

    @FXML
    private TextField txtIdentificacion;

    @FXML
    private TextField txtNombre;

    @FXML
    private PasswordField txtPassword;

    /**
     * Método que se ejecuta automáticamente al cargar la vista.
     * Inicializa los campos del formulario con los datos del usuario
     * y configura eventos para actualizar la información.
     */
    @FXML
    void initialize() {
        usuario = sesion.getUsuario();

        assert btnActualizar != null : "fx:id=\"btnActualizar\" was not injected.";
        assert txtCorreo != null : "fx:id=\"txtCorreo\" was not injected.";
        assert txtDireccion != null : "fx:id=\"txtDireccion\" was not injected.";
        assert txtIdentificacion != null : "fx:id=\"txtIdentificacion\" was not injected.";
        assert txtNombre != null : "fx:id=\"txtNombre\" was not injected.";
        assert txtPassword != null : "fx:id=\"txtPassword\" was not injected.";

        cargarDatosUsuario();

        btnActualizar.setOnAction(e -> {
            try {
                actualizarUsuario();
                crearAlerta("Éxito", "Usuario actualizado correctamente.");
            } catch (Exception ex) {
                crearAlerta("Error", ex.getMessage());
            }
        });

        Platform.runLater(() -> {
            Stage stage = (Stage) btnActualizar.getScene().getWindow();
            stage.setOnCloseRequest(event -> irPanelCliente());
        });
    }

    /**
     * Carga los datos del usuario en los campos de texto para ser modificados.
     */
    private void cargarDatosUsuario() {
        if (usuario != null) {
            txtNombre.setText(usuario.getNombre());
            txtIdentificacion.setText(usuario.getId());
            txtDireccion.setText(usuario.getDireccion());
            txtCorreo.setText(usuario.getEmail());
            txtPassword.setText(usuario.getPassword());
        }
    }

    /**
     * Actualiza los atributos del usuario con los datos introducidos en los campos de texto.
     *
     * @throws Exception si algún campo está vacío.
     */
    private void actualizarUsuario() throws Exception {
        String nombre = txtNombre.getText().trim();
        String identificacion = txtIdentificacion.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String correo = txtCorreo.getText().trim();
        String password = txtPassword.getText().trim();

        if (nombre.isEmpty() || identificacion.isEmpty() || direccion.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            throw new Exception("Por favor completa todos los campos.");
        }

        usuario.setNombre(nombre);
        usuario.setId(identificacion);
        usuario.setDireccion(direccion);
        usuario.setEmail(correo);
        usuario.setPassword(password);
    }

    /**
     * Muestra una alerta de tipo informativo.
     *
     * @param titulo  Título de la alerta.
     * @param mensaje Contenido del mensaje.
     */
    private void crearAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(BancoApp.class.getResourceAsStream("/img/S.png"))));
        alert.showAndWait();

    }

    /**
     * Abre el panel del cliente una vez finalizada la actualización.
     */
    private void irPanelCliente() {
        navegarVentana("/panelCliente.fxml", "Banco - Panel Cliente");
    }

    /**
     * Navega a una nueva ventana FXML.
     *
     * @param rutaFxml      Ruta del archivo FXML.
     * @param tituloVentana Título que se mostrará en la ventana.
     */
    private void navegarVentana(String rutaFxml, String tituloVentana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent root = loader.load();

            PanelClienteControlador controlador = loader.getController();
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

    /**
     * Crea una alerta del tipo especificado.
     *
     * @param mensaje Mensaje a mostrar.
     * @param tipo    Tipo de alerta (INFORMATION, WARNING, ERROR, etc).
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
     * Cierra la ventana actual.
     */
    public void cerrarVentana() {
        Stage ventanaActual = (Stage) btnActualizar.getScene().getWindow();
        ventanaActual.close();
    }
}