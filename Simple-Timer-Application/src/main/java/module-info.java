module com.kirbysmashyeet.simpletimerapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.kirbysmashyeet.simpletimerapplication to javafx.fxml;
    exports com.kirbysmashyeet.simpletimerapplication;
}