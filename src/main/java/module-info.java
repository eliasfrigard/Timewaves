module com.timeline {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.codec;
    requires java.sql;
    requires org.controlsfx.controls;
    requires jakarta.mail;
    requires jakarta.activation;
    requires fuzzywuzzy;
    requires javafx.swing;
    requires javafx.graphics;

    opens com.timeline to javafx.fxml;
    exports com.timeline;
    exports com.timeline.customNodes;
    exports sql;
}
