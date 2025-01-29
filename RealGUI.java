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

        VBox buttonsBox = new VBox(10, solveButton, backButton);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox puzzleBox = new VBox(20, grid, buttonsBox);
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

        levelSelectScene = new Scene(levelBox, 500, 600);
        primaryStage.setScene(levelSelectScene);
    }
    // maps the integer code to a JavaFX Color.
    // 0 -> grey, 1 -> red, 2 -> blue, 3 -> green, 4 -> yellow
    private Color getColorFromCode(int code) {
        switch (code) {
            case 1: return Color.RED;
            case 2: return Color.BLUE;
            case 3: return Color.GREEN;
            case 4: return Color.YELLOW;
            default: return Color.LIGHTGRAY;
        }
    }

    // an inner class which generates Numberlinks.java puzzles
    class ColorNumberLinksGenerator {
        private int[][] grid;
        private int gridSize;
        private int pairCount;
        private Random random;

        public ColorNumberLinksGenerator(int gridSize) {
            this.gridSize = gridSize;
            // gridsize/2 ensures grid sizes 5 to 9 & the paircount will be 2-4
            this.pairCount = gridSize / 2; // pairs aren't > than available colours
            grid = new int[gridSize][gridSize];
            random = new Random();
        }

        // generate a new grid by placing each pair, borrowing concepts from my other file
        public void generate() {
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    grid[i][j] = 0;
                }
            }
            for (int color = 1; color <= pairCount; color++) {
                placePair(color);
            }
        }

        private void placePair(int color) {
            int x1 = random.nextInt(gridSize);
            int y1 = random.nextInt(gridSize);
            while (grid[x1][y1] != 0) {
                x1 = random.nextInt(gridSize);
                y1 = random.nextInt(gridSize);
            }
            grid[x1][y1] = color;

            int x2 = random.nextInt(gridSize);
            int y2 = random.nextInt(gridSize);
            while (grid[x2][y2] != 0 || (x1 == x2 && y1 == y2)) {
                x2 = random.nextInt(gridSize);
                y2 = random.nextInt(gridSize);
            }
            grid[x2][y2] = color;
        }

        // check done to see if the cell at (x,y) is within bounds AND either empty or is the target cell
        public boolean isSafe(int x, int y, int targetX, int targetY, int color) {
            if (x < 0 || x >= gridSize || y < 0 || y >= gridSize) return false;
            return grid[x][y] == 0 || (x == targetX && y == targetY);
        }

        // attempts to solve the connections
        public boolean solveColorLink(int x, int y, int targetX, int targetY, int color) {
            if (x == targetX && y == targetY) return true;

            // marks it as a path as well as movement
            grid[x][y] = color;
            int[] dx = {0, 0, -1, 1};
            int[] dy = {-1, 1, 0, 0};

            // tries all directions
            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];
                if (isSafe(newX, newY, targetX, targetY, color)) {
                    if (solveColorLink(newX, newY, targetX, targetY, color)) {
                        return true;
                    }
                }
            }
            // backtracking is done for unmarking the cell
            grid[x][y] = 0;
            return false;
        }

        // verifies that each pair is valid
        // saves a copy of the original grid, tries to solve each pair and resets
        public boolean verify() {
            int[][] originalGrid = new int[gridSize][gridSize];
            for (int i = 0; i < gridSize; i++) {
                System.arraycopy(grid[i], 0, originalGrid[i], 0, gridSize);
            }

            for (int color = 1; color <= pairCount; color++) {
                int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (grid[i][j] == color) {
                            if (x1 == -1) {
                                x1 = i;
                                y1 = j;
                            } else {
                                x2 = i;
                                y2 = j;
                            }
                        }
                    }
                }
                if (!solveColorLink(x1, y1, x2, y2, color)) {
                    return false;
                }
                // restore the grid for the next color
                for (int i = 0; i < gridSize; i++) {
                    System.arraycopy(originalGrid[i], 0, grid[i], 0, gridSize);
                }
            }
            return true;
        }

        public int[][] getGrid() {
            return grid;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}