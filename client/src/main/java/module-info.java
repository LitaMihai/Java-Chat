module client {
    requires javafx.controls;
    requires javafx.swing;
    requires javafx.fxml;
    requires java.desktop;
    requires mongo.java.driver;
    requires org.slf4j;
    requires json.simple;
    requires javafx.media;
    requires jbcrypt;

    opens com.client.login to javafx.fxml;
    exports com.client.login;
    exports com.client.util to javafx.fxml;
    opens com.client.util to javafx.fxml;
    opens com.client.chatwindow to javafx.fxml;
    opens com.traynotifications.notification to javafx.fxml;
}