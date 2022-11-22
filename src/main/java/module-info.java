module com.snake {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.snake to javafx.fxml;
    exports com.snake;
}