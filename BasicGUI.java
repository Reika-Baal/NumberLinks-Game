
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BasicGUI extends Application {

    private Scene mainScene;
    private Scene puzzleScene;
    private Scene levelSelectScene;

    @Override
    public void start(Stage primaryStage) {
        // button creation
        Button generatepuzzle = new Button("Generate Puzzle");
        Button selectLevel = new Button("Select Level");

        // place holders for the buttons actions which later will be modified to have AI backtracking etc
        generatepuzzle.setOnAction(e -> openPuzzleScreen(primaryStage));
        selectLevel.setOnAction(e -> openLevelSelectScreen(primaryStage));

        // this is the VBox container for the buttons to ensure that they are centered
        VBox vbox = new VBox(10, generatepuzzle, selectLevel);
        vbox.setStyle("-fx-alignment: center;");

        mainScene = new Scene(vbox, 300, 200);
        primaryStage.setTitle("Puzzle Game");

        createPuzzleScene(primaryStage);

        createLevelSelectScene(primaryStage);

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    // this is the VBox container for the buttons to ensure that they are centered
    private void createPuzzleScene(Stage primaryStage) {

                        = Button backButton = new Button("Back");

        // when the Back button is pressed it goes back to the main scene
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        Button solvePuzzleButton = new Button("Solve Puzzle");

        solvePuzzleButton.setOnAction(e -> System.out.println("Puzzle solved"));

        // big empty box (Rectangle) used to test as a placeholder for the actual numberlinks later
        Rectangle bigBox = new Rectangle(200, 100, Color.LIGHTGRAY);  // size box
        bigBox.setArcHeight(20); // Optional: round the corners ; if i want to change I just get rid of 20
        bigBox.setArcWidth(20);

        // the text object for the number 1
        Text letterOne = new Text("1");
        letterOne.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        // creating a StackPane to layer the letter inside the box
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(bigBox, letterOne);

        // container that arranges the elements vertically
        VBox puzzleBox = new VBox(20, stackPane, solvePuzzleButton, backButton);
        puzzleBox.setStyle("-fx-alignment: center;");

        puzzleScene = new Scene(puzzleBox, 300, 200);
    }

    // a method that creates the "Level Selection" scene with level buttons
    private void createLevelSelectScene(Stage primaryStage) {

        Button level1 = new Button("Level 1");
        Button level2 = new Button("Level 2");
        Button level3 = new Button("Level 3");
        Button level4 = new Button("Level 4");
        Button level5 = new Button("Level 5");

        // set actions for level buttons which right now they just print the level number
        level1.setOnAction(e -> System.out.println("Level 1 selected"));
        level2.setOnAction(e -> System.out.println("Level 2 selected"));
        level3.setOnAction(e -> System.out.println("Level 3 selected"));
        level4.setOnAction(e -> System.out.println("Level 4 selected"));
        level5.setOnAction(e -> System.out.println("Level 5 selected"));

        VBox levelBox = new VBox(10, level1, level2, level3, level4, level5);
        levelBox.setStyle("-fx-alignment: center;");

        // back button to return to the main screen
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        levelBox.getChildren().add(backButton);
        levelSelectScene = new Scene(levelBox, 300, 300);
    }

    private void openPuzzleScreen(Stage primaryStage) {
        primaryStage.setScene(puzzleScene);
    }

    // method used to open the "Select Level" scene
    private void openLevelSelectScreen(Stage primaryStage) {
        primaryStage.setScene(levelSelectScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
