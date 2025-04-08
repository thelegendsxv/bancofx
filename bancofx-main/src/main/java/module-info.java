module co.edu.uniquindio.banco {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.desktop;

    opens co.edu.uniquindio.banco to javafx.fxml;

    exports co.edu.uniquindio.banco;
    exports co.edu.uniquindio.banco.modelo.entidades;
    exports co.edu.uniquindio.banco.modelo.vo;

    exports co.edu.uniquindio.banco.controlador;
    opens co.edu.uniquindio.banco.controlador to javafx.fxml;
}