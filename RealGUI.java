import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.Random;

public class RealGUI extends Application {

    private Scene mainScene;
    private Scene puzzleScene;
    private Scene levelSelectScene;

    @Override
    public void start(Stage primaryStage) {
        Button generatePuzzle = new Button("Generate Puzzle");
        Button selectLevel = new Button("Select Level");

        // uses level 1 when clicking generate puzzle as a placeholder
        generatePuzzle.setOnAction(e -> openPuzzleScreen(primaryStage, 1));
        selectLevel.setOnAction(e -> openLevelSelectScreen(primaryStage));

        VBox vbox = new VBox(10, generatePuzzle, selectLevel);
        vbox.setAlignment(Pos.CENTER);

        mainScene = new Scene(vbox, 500, 600);
        primaryStage.setTitle("Real Puzzle Game");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    // determines the size of the grid using the level 
    private void openPuzzleScreen(Stage primaryStage, int level) {
        int gridSize = level + 4;
        ColorNumberLinksGenerator generator = new ColorNumberLinksGenerator(gridSize);
        
        // generate a puzzle until a solvable one
        do {
            generator.generate();
        } while (!generator.verify());

        int[][] puzzleGrid = generator.getGrid();

        // creating a GridPane to display the puzzle
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Rectangle cell = new Rectangle(50, 50);
                int colorCode = puzzleGrid[row][col];
                cell.setFill(getColorFromCode(colorCode));
                grid.add(cell, col, row);
            }
        }
        grid.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        Button solveButton = new Button("Solve Puzzle");
        solveButton.setOnAction(e -> System.out.println("Puzzle solved")); // placeholder until the solve button actually solves it

        VBox puzzleBox = new VBox(20, grid, new VBox(10, solveButton, backButton));
        puzzleBox.setAlignment(Pos.CENTER);
        puzzleScene = new Scene(puzzleBox, 500, 600);
        primaryStage.setScene(puzzleScene);
    }

    private void openLevelSelectScreen(Stage primaryStage) {
        Button level1 = new Button("Level 1");
        Button level2 = new Button("Level 2");
        Button level3 = new Button("Level 3");
        Button level4 = new Button("Level 4");
        Button level5 = new Button("Level 5");

        level1.setOnAction(e -> openPuzzleScreen(primaryStage, 1));
        level2.setOnAction(e -> openPuzzleScreen(primaryStage, 2));
        level3.setOnAction(e -> openPuzzleScreen(primaryStage, 3));
        level4.setOnAction(e -> openPuzzleScreen(primaryStage, 4));
        level5.setOnAction(e -> openPuzzleScreen(primaryStage, 5));

        VBox levelBox = new VBox(10, level1, level2, level3, level4, level5);
        levelBox.setAlignment(Pos.CENTER);

        levelSelectScene = new Scene(levelBox, 300, 250);
        primaryStage.setScene(levelSelectScene);
    }
}