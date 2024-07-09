module com.example.bank_management_system {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;

    opens com.example.bank_management_system to javafx.fxml;
    exports com.example.bank_management_system;
}