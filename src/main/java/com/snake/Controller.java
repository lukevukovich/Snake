package com.snake;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.snake.Direction.*;

enum Direction {STOP, UP, DOWN, LEFT, RIGHT};
public class Controller {
    private int height, width;
    private final int UNITS = 30;
    private int speed;
    private int score;
    private int nTail;
    private Direction dir;
    private Timer timer;
    boolean running;
    private Image tile;
    private  Image snake;
    private Image fruit;
    private ImageView snakeImage;
    private ImageView fruitImage;
    private ImageView[] tailImage;
    private Label gameOver;
    private Group board;
    private Group boardGroup;
    private Scene scene;
    private Stage stage;

    //Constructor------------------
    public Controller(Stage st, String t, String s, String f, int h, int w, int sp) {
        height = h;
        width = w;
        speed = sp;

        score = 0;
        nTail = 1;


        dir = STOP;
        running = false;

        tile = new Image(t);
        snake = new Image(s);

        snakeImage = new ImageView(snake);
        snakeImage.setFitHeight(UNITS);
        snakeImage.setFitWidth(UNITS);

        fruit = new Image(f);
        fruitImage = new ImageView(fruit);
        fruitImage.setFitHeight(UNITS);
        fruitImage.setFitWidth(UNITS);

        tailImage = new ImageView[width*height];
        for (int i = 0; i < tailImage.length; i++) {
            tailImage[i] = new ImageView(snake);
            tailImage[i].setVisible(false);
        }

        gameOver = new Label("Game Over");
        gameOver.setVisible(false);

        board = new Group();
        boardGroup = new Group();
        boardGroup.getChildren().addAll(board, fruitImage, snakeImage);

        scene = new Scene(boardGroup);

        stage = st;
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(snake);
        stage.setTitle("SNAKE  |  SCORE: " + score);
    }

    //Game Board------------------
    private void drawBoard() {
        Random rand = new Random();
        int snakeX = (width/2) * UNITS, snakeY = (height/2) * UNITS;

        int x = 0, fruitX = 0, fruitY = 0;
        while (x == 0) {
            fruitX = rand.nextInt(width) * UNITS;
            fruitY = rand.nextInt(height) * UNITS;

            if (snakeX != fruitX && snakeY != fruitY)
                x++;
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ImageView tileImage = new ImageView(tile);
                tileImage.setFitHeight(UNITS);
                tileImage.setFitWidth(UNITS);
                tileImage.setX(j*UNITS);
                tileImage.setY(i*UNITS);
                board.getChildren().add(tileImage);

                if (j * UNITS == snakeX && i * UNITS == snakeY)
                    setSnakePos(snakeX, snakeY);
                if (j * UNITS == fruitX && i * UNITS == fruitY)
                    setFruitPos(fruitX, fruitY);
            }
        }
    }

    private void setSnakePos(double x, double y) {
        snakeImage.setX(x);
        snakeImage.setY(y);
    }

    private void setFruitPos(double x, double y) {
        fruitImage.setX(x);
        fruitImage.setY(y);
    }

    //Snake------------------
    private void snakeMove(KeyEvent key) {
        String choice = key.getText();
        switch (choice) {
            case "w":
                if (dir != DOWN || nTail == 1) {
                    snakeImage.setY(snakeImage.getY() - UNITS);
                    dir = UP;
                }
                else
                    setSnakePos(snakeImage.getX(), snakeImage.getY() + UNITS);
                break;
            case "a":
                if (dir != RIGHT || nTail == 1) {
                    snakeImage.setX(snakeImage.getX() - UNITS);
                    dir = LEFT;
                }
                else
                    setSnakePos(snakeImage.getX() + UNITS, snakeImage.getY());
                break;
            case "s":
                if (dir != UP || nTail == 1) {
                    snakeImage.setY(snakeImage.getY() + UNITS);
                    dir = DOWN;
                }
                else
                    setSnakePos(snakeImage.getX(), snakeImage.getY() - UNITS);
                break;
            case "d":
                if (dir != LEFT || nTail == 1) {
                    snakeImage.setX(snakeImage.getX() + UNITS);
                    dir = RIGHT;
                }
                else
                    setSnakePos(snakeImage.getX() - UNITS, snakeImage.getY());
                break;
            case "x":
                cancelAutoMove();
                stage.close();
                break;
        }
    }

    //Tail------------------
    private void addTail() {
        tailImage[nTail].setVisible(true);
        tailImage[nTail].setFitHeight(UNITS);
        tailImage[nTail].setFitWidth(UNITS);
        tailImage[nTail].setX(fruitImage.getX());
        tailImage[nTail].setY(fruitImage.getY());
        boardGroup.getChildren().add(tailImage[nTail]);

        nTail += 1;
    }

    private void tailMove() {
        double prevX = tailImage[0].getX();
        double prevY = tailImage[0].getY();
        double prev2X, prev2Y;

        tailImage[0].setX(snakeImage.getX());
        tailImage[0].setY(snakeImage.getY());

        for (int i = 1; i < nTail; i++) {
            prev2X = tailImage[i].getX();
            prev2Y = tailImage[i].getY();

            tailImage[i].setX(prevX);
            tailImage[i].setY(prevY);

            prevX = prev2X;
            prevY = prev2Y;
        }

    }

    //Fruit------------------
    private void fruitMove() {
        if (snakeImage.getX() == fruitImage.getX() && snakeImage.getY() == fruitImage.getY()) {
            if (running)
                cancelAutoMove();
            beginAutoMove();

            addTail();

            int x = 0;
            int fruitX = 0, fruitY = 0;
            cancelAutoMove();
            while (x < nTail - 1) {
                x = 0;
                Random rand = new Random();
                fruitX = rand.nextInt(width) * UNITS;
                fruitY = rand.nextInt(height) * UNITS;
                for (int i = 0; i <= nTail; i++) {
                    if (tailImage[i].getX() != fruitX && tailImage[i].getY() != fruitY)
                        x++;
                }
            }
            beginAutoMove();
            setFruitPos(fruitX, fruitY);

            score += 10;
            stage.setTitle("SNAKE  |  SCORE: " + score);
        }

    }

    //Auto Move Functions------------------
    private void autoMove() {
        switch (dir) {
            case UP:
                snakeImage.setY(snakeImage.getY() - UNITS);
                break;
            case LEFT:
                snakeImage.setX(snakeImage.getX() - UNITS);
                break;
            case DOWN:
                snakeImage.setY(snakeImage.getY() + UNITS);
                break;
            case RIGHT:
                snakeImage.setX(snakeImage.getX() + UNITS);
                break;
        }
    }

    private void beginAutoMove()
    {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                running = true;
                Platform.runLater(() -> {
                    autoMove();
                    tailMove();
                    fruitMove();
                    biteTail();
                    outOfBounds();
                    //roundRobin();
                    System.gc();
                });
            }
        };

        timer.scheduleAtFixedRate(task, speed, speed);
    }

    private void cancelAutoMove()
    {
        this.timer.cancel();
        running = false;
    }

    //Edge Of Board------------------

    private void roundRobin() {
        if (snakeImage.getX() < 0) {
            snakeImage.setX((width - 1) * UNITS);
            tailImage[0].setX(snakeImage.getX());
        }
        else if (snakeImage.getX() > (width - 1) * UNITS) {
            snakeImage.setX(0);
            tailImage[0].setX(snakeImage.getX());
        }
        else if (snakeImage.getY() < 0) {
            snakeImage.setY((height - 1) * UNITS);
            tailImage[0].setY(snakeImage.getY());
        }
        else if (snakeImage.getY() > (height - 1) * UNITS) {
            snakeImage.setY(0);
            tailImage[0].setY(snakeImage.getY());
        }
    }

    private void outOfBounds() {
        if (snakeImage.getX()/UNITS < 0 || snakeImage.getX()/UNITS > width - 1 ||
                snakeImage.getY()/UNITS < 0 || snakeImage.getY()/UNITS > height - 1) {
            snakeImage.setOpacity(0);
            cancelAutoMove();
            gameOver();
        }
    }

    //End Game------------------
    private void gameOver() {
        gameOver.setVisible(true);
        gameOver.setAlignment(Pos.CENTER);
        gameOver.setFont(Font.font("comic sans ms", 60));
        gameOver.setTextFill(Color.WHITE);
        gameOver.setBackground(new Background(new BackgroundFill(Color.color(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        gameOver.setPrefSize(width*UNITS, height*UNITS);
        gameOver.setOpacity(.85);
        boardGroup.getChildren().add(gameOver);

        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getText().equals("x"))
                stage.close();
        });

        stage.setScene(scene);
    }

    private void biteTail() {
        if (nTail > 2) {
            for (int i = 2; i < nTail; i++) {
                if (tailImage[1].getX() == tailImage[i].getX() &&
                        tailImage[1].getY() == tailImage[i].getY()) {
                    snakeImage.setOpacity(0);
                    cancelAutoMove();
                    gameOver();
                }
            }
        }
    }

    private void manualExit() {
        stage.setOnCloseRequest(windowEvent -> {
            cancelAutoMove();
        });
    }

    //Build Game------------------
    public void build() {
        drawBoard();
        manualExit();
        beginAutoMove();

        scene.setOnKeyPressed(keyEvent -> {
            snakeMove(keyEvent);
            tailMove();
            fruitMove();
            biteTail();
            outOfBounds();
            //roundRobin();
            System.gc();
        });

        stage.show();
    }

}
