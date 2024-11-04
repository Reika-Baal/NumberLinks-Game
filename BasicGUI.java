
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BasicGUI extends Application {

    private Scene mainScene;
    private Scene puzzleScene;

    @Override
    public void start(Stage primaryStage) {
        // button creation
        Button generatepuzzle = new Button("Generate Puzzle");
        Button solvebutton = new Button("Solve Puzzle");

        // place holders for the buttons actions which later will be modified to have AI backtracking etc
        generatepuzzle.setOnAction(e -> openPuzzleScreen(primaryStage));
        solvebutton.setOnAction(e -> System.out.println("Solve puzzle clicked!"));

        // this is the VBox container for the buttons to ensure that they are centered
        VBox vbox = new VBox(10, generatepuzzle, solvebutton);
        vbox.setStyle("-fx-alignment: center;");

        mainScene = new Scene(vbox, 300, 200);
        primaryStage.setTitle("Puzzle Game");

        createPuzzleScene(primaryStage);

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    // a meethod to create the generate puzzle with the implamentation of a back button
    private void createPuzzleScene(Stage primaryStage) {

        Button backButton = new Button("Back");

        // when the Back button is pressed it goes back to the main scene
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        // used to centre
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(backButton);

        puzzleScene = new Scene(stackPane, 300, 200);
    }

    private void openPuzzleScreen(Stage primaryStage) {
        primaryStage.setScene(puzzleScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
