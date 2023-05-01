module client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires mongo.java.driver;
    requires org.slf4j;

    opens com.client.login to javafx.fxml;
    exports com.client.login;
    opens com.client.chatwindow to javafx.fxml;
    opens com.traynotifications.notification to javafx.fxml;
}