module MorseLearner.app {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.derpderphurr.morse to javafx.fxml;
    opens com.derpderphurr.morse.tabs to javafx.fxml;

    exports com.derpderphurr.morse;
    exports com.derpderphurr.morse.exercise;
    opens com.derpderphurr.morse.exercise to javafx.fxml;

}