import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BasicGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        // button creation
        Button generatepuzzle = new Button("Generate Puzzle");
        Button solvebutton = new Button("Solve Puzzle");

        // place holders for the buttons actions which later will be modified to have AI backtracking etc
        generatepuzzle.setOnAction(e -> System.out.println("Generated puzzle!"));
        solvebutton.setOnAction(e -> System.out.println("Solve puzzle clicked!"));

        // vertical box placeholder
        VBox vbox = new VBox(10, generatepuzzle, solvebutton);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setTitle("Puzzle Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}