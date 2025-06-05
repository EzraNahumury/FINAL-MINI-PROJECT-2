module org.ukdw {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.desktop;
    requires jdk.compiler;

    opens org.ukdw.view to javafx.fxml;
    exports org.ukdw.view;
    opens org.ukdw to javafx.fxml;
    exports org.ukdw;
    opens org.ukdw.data to javafx.fxml;
    exports org.ukdw.data;
}