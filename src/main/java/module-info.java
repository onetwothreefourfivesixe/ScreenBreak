module com.reminder {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.base;
    requires java.desktop;

    opens com.reminder to javafx.fxml;

    exports com.reminder;
}
