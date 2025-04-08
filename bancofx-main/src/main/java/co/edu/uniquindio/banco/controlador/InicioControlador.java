package co.edu.uniquindio.banco.controlador;

import co.edu.uniquindio.banco.BancoApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Controlador para la vista de inicio del sistema bancario.
 * Permite la navegación hacia la vista de inicio de sesión o registro de cliente.
 *
 * @author caflorezvi
 */
public class InicioControlador {

    @FXML
    private Button irIniciarSesion;

    @FXML
    private Button irRegistroCliente;

    /**
     * Evento que se activa al hacer clic en el botón de "Iniciar Sesión".
     * Navega a la ventana de inicio de sesión.
     *
     * @param actionEvent Evento generado por el botón.
     */
    public void irIniciarSesion(ActionEvent actionEvent) {
        navegarVentana("/login.fxml", "Banco - Iniciar Sesión");
    }

    /**
     * Evento que se activa al hacer clic en el botón de "Registrarse".
     * Navega a la ventana de registro de cliente.
     *
     * @param actionEvent Evento generado por el botón.
     */
    public void irRegistroCliente(ActionEvent actionEvent) {
        navegarVentana("/registro.fxml", "Banco - Registro de Cliente");
    }

    /**
     * Abre una nueva ventana a partir de un archivo FXML y cierra la ventana actual.
     *
     * @param nombreArchivoFxml Ruta del archivo FXML que define la interfaz a cargar.
     * @param tituloVentana     Título de la nueva ventana.
     */
    public void navegarVentana(String nombreArchivoFxml, String tituloVentana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(nombreArchivoFxml));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(Objects.requireNonNull(BancoApp.class.getResourceAsStream("/img/S.png"))));
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle(tituloVentana);
            stage.show();

            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cierra la ventana actual donde se encuentra este controlador.
     */
    public void cerrarVentana() {
        Stage ventanaActual = (Stage) irIniciarSesion.getScene().getWindow();
        ventanaActual.close();
    }
}