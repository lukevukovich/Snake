package com.snake;

import javafx.application.Application;
import javafx.stage.Stage;

public class Setup extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        String tile = "white.png", snake = "red.png", fruit = "green.png";
        int height = 20, width = 20, speed = 100;

        Controller controller = new Controller(stage, tile, snake, fruit, height, width, speed);
        controller.build();

    }
}
