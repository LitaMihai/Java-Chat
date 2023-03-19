module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.client.login to javafx.fxml;
    exports com.client.login;
}