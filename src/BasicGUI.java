import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BasicGUI extends Application {

    private Scene mainScene;
    private Scene puzzleScene;
    private Scene levelSelectScene;

    @Override
    public void start(Stage primaryStage) {
        // button creation
        Button generatePuzzle = new Button("Generate Puzzle");
        Button selectLevel = new Button("Select Level");

         // place holders for the buttons actions which later will be modified to have AI backtracking etc
        generatePuzzle.setOnAction(e -> openPuzzleScreen(primaryStage, 0)); 
        selectLevel.setOnAction(e -> openLevelSelectScreen(primaryStage)); 

        // this is the VBox container for the buttons to ensure that they are centered
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10, generatePuzzle, selectLevel);
        vbox.setStyle("-fx-alignment: center;");

        mainScene = new Scene(vbox, 300, 200);
        primaryStage.setTitle("Puzzle Game");

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void createPuzzleScene(Stage primaryStage, int level) {

        Button backButton = new Button("Back");
    
        // back button to return to the main screen
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));
    
        // creates 5x5 Numberlink puzzle grid
        GridPane grid = createPuzzleGrid(level);
    
        Button solvePuzzleButton = new Button("Solve Puzzle");
    
        solvePuzzleButton.setOnAction(e -> System.out.println("Puzzle solved"));
    
        // container for the buttons
        javafx.scene.layout.VBox buttonsBox = new javafx.scene.layout.VBox(10, solvePuzzleButton, backButton);
        buttonsBox.setStyle("-fx-alignment: center;");
    
        javafx.scene.layout.VBox puzzleBox = new javafx.scene.layout.VBox(20, grid, buttonsBox);
        puzzleBox.setStyle("-fx-alignment: center;");
    
        puzzleBox.setAlignment(javafx.geometry.Pos.CENTER);
    
        puzzleScene = new Scene(puzzleBox, 400, 450);
    }    

    // the method for creating the 5x5 grid with colored paths for Numberlink (based on the level)
    private GridPane createPuzzleGrid(int level) {
        GridPane grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);

        // grid size
        int size = 5;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Rectangle cell = new Rectangle(50, 50);  // Each cell is a 50x50 rectangle

                // Assign colors based on the selected level
                if (level == 0) {
                    // Generate Puzzle: Randomized paths with distinct colors
                    if (row == 0 && col == 0) {
                        cell.setFill(Color.RED);  // R start
                    } else if (row == 4 && col == 4) {
                        cell.setFill(Color.RED);  // R end
                    } else if (row == 2 && col == 1) {
                        cell.setFill(Color.GREEN);
                    } else if (row == 3 && col == 3) {
                        cell.setFill(Color.GREEN); 
                    } else if (row == 1 && col == 2) {
                        cell.setFill(Color.BLUE); 
                    } else if (row == 4 && col == 0) {
                        cell.setFill(Color.BLUE); 
                    } else {
                        cell.setFill(Color.LIGHTGRAY);  // empty cells
                    }
                }
                else if (level == 1) {
                    // Level 1: Standard paths
                    if (row == 0 && col == 0) {
                        cell.setFill(Color.BLUE);  
                    } else if (row == 4 && col == 4) {
                        cell.setFill(Color.BLUE);  
                    } else if (row == 1 && col == 1) {
                        cell.setFill(Color.RED);  
                    } else if (row == 3 && col == 3) {
                        cell.setFill(Color.RED); 
                    } else if (row == 2 && col == 2) {
                        cell.setFill(Color.GREEN);  
                    } else if (row == 4 && col == 0) {
                        cell.setFill(Color.GREEN);  
                    } else {
                        cell.setFill(Color.LIGHTGRAY);
                    }
                }
                else if (level == 2) {
                    if (row == 0 && col == 0) {
                        cell.setFill(Color.GREEN); 
                    } else if (row == 4 && col == 4) {
                        cell.setFill(Color.GREEN);
                    } else if (row == 1 && col == 3) {
                        cell.setFill(Color.RED);
                    } else if (row == 3 && col == 1) {
                        cell.setFill(Color.RED);
                    } else if (row == 2 && col == 2) {
                        cell.setFill(Color.BLUE);
                    } else if (row == 4 && col == 0) {
                        cell.setFill(Color.BLUE); 
                    } else {
                        cell.setFill(Color.LIGHTGRAY);
                    }
                }
                else if (level == 3) {
                
                    if (row == 0 && col == 0) {
                        cell.setFill(Color.RED);
                    } else if (row == 4 && col == 4) {
                        cell.setFill(Color.RED);
                    } else if (row == 1 && col == 2) {
                        cell.setFill(Color.GREEN);
                    } else if (row == 3 && col == 2) {
                        cell.setFill(Color.GREEN);
                    } else if (row == 2 && col == 0) {
                        cell.setFill(Color.BLUE);
                    } else if (row == 2 && col == 4) {
                        cell.setFill(Color.BLUE);
                    } else {
                        cell.setFill(Color.LIGHTGRAY);
                    }
                }
                else if (level == 4) {
                    if (row == 0 && col == 0) {
                        cell.setFill(Color.BLUE);
                    } else if (row == 4 && col == 4) {
                        cell.setFill(Color.BLUE);
                    } else if (row == 1 && col == 1) {
                        cell.setFill(Color.RED);
                    } else if (row == 3 && col == 3) {
                        cell.setFill(Color.RED);
                    } else if (row == 2 && col == 2) {
                        cell.setFill(Color.GREEN);
                    } else if (row == 4 && col == 0) {
                        cell.setFill(Color.GREEN); 
                    } else {
                        cell.setFill(Color.LIGHTGRAY);
                    }
                }
                else if (level == 5) {
                    if (row == 0 && col == 0) {
                        cell.setFill(Color.RED);
                    } else if (row == 4 && col == 4) {
                        cell.setFill(Color.RED);
                    } else if (row == 2 && col == 2) {
                        cell.setFill(Color.GREEN);
                    } else if (row == 3 && col == 1) {
                        cell.setFill(Color.GREEN);
                    } else if (row == 1 && col == 3) {
                        cell.setFill(Color.BLUE);
                    } else if (row == 4 && col == 0) {
                        cell.setFill(Color.BLUE);
                    } else {
                        cell.setFill(Color.LIGHTGRAY);
                    }
                }
                grid.add(cell, col, row);
            }
        }

        return grid;
    }

       // a method that creates the "Level Selection" scene with level buttons
    private void openLevelSelectScreen(Stage primaryStage) {
        Button level1 = new Button("Level 1");
        Button level2 = new Button("Level 2");
        Button level3 = new Button("Level 3");
        Button level4 = new Button("Level 4");
        Button level5 = new Button("Level 5");

        // Set actions for level button where each prints the current level
        level1.setOnAction(e -> openPuzzleScreen(primaryStage, 1));
        level2.setOnAction(e -> openPuzzleScreen(primaryStage, 2));
        level3.setOnAction(e -> openPuzzleScreen(primaryStage, 3));
        level4.setOnAction(e -> openPuzzleScreen(primaryStage, 4));
        level5.setOnAction(e -> openPuzzleScreen(primaryStage, 5));

        javafx.scene.layout.VBox levelBox = new javafx.scene.layout.VBox(10, level1, level2, level3, level4, level5);
        levelBox.setStyle("-fx-alignment: center;");

        levelSelectScene = new Scene(levelBox, 300, 250);

        primaryStage.setScene(levelSelectScene);
    }

    private void openPuzzleScreen(Stage primaryStage, int level) {
        createPuzzleScene(primaryStage, level);
        primaryStage.setScene(puzzleScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
