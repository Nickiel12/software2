module com.example.software2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires mysql.connector.j;

    opens com.example.software2 to javafx.fxml;
    exports com.example.software2;
    exports com.example.software2.models;
    opens com.example.software2.models to javafx.fxml;
}