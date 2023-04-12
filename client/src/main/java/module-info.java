module client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires slf4j.api;

    opens com.client.login to javafx.fxml;
    exports com.client.login;
    opens com.client.chatwindow to javafx.fxml;
    opens com.traynotifications.notification to javafx.fxml;
}