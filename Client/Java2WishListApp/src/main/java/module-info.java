module com.mycompany.java2wishlistapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.mycompany.java2wishlistapp to javafx.fxml;
    exports com.mycompany.java2wishlistapp;
}
