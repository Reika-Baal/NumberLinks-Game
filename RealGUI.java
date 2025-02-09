import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.*;

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
        StackPane[][] cellPanes = new StackPane[gridSize][gridSize];

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Rectangle cell = new Rectangle(50, 50);
                int colorCode = puzzleGrid[row][col];
                cell.setFill(getColorFromCode(colorCode));
                StackPane cellPane = new StackPane(cell);
                cellPanes[row][col] = cellPane;
                grid.add(cellPane, col, row);
            }
        }
        grid.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        Button solveButton = new Button("Solve Puzzle");
        // when solve is clicked solves the puzzle by marking the connecting paths and redraw the grid
        solveButton.setOnAction(e -> {
            if (generator.solvePuzzle()) {
                grid.getChildren().clear();
                int[][] solvedGrid = generator.getGrid();
                Map<String, String> arrows = generator.getArrowMap();
                for (int row = 0; row < gridSize; row++) {
                    for (int col = 0; col < gridSize; col++) {
                        Rectangle cell = new Rectangle(50, 50);
                        int code = solvedGrid[row][col];
                        cell.setFill(code < 0 ? getMutedColorFromCode(Math.abs(code)) : getColorFromCode(code));
                        StackPane cellPane = new StackPane(cell);
                        String key = row + "," + col;
                        if (arrows.containsKey(key)) {
                            Label arrow = new Label(arrows.get(key));
                            arrow.setStyle("-fx-font-size: 20; -fx-text-fill: black;");
                            cellPane.getChildren().add(arrow);
                        }
                        grid.add(cellPane, col, row);
                    }
                }
            }
        });

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

    // maps the integer code to a JavaFX colour
    private Color getColorFromCode(int code) {
        switch (code) {
            case 1: return Color.RED;
            case 2: return Color.BLUE;
            case 3: return Color.GREEN;
            case 4: return Color.YELLOW;
            default: return Color.LIGHTGRAY;
        }
    }

    // maps integer codes to muted colors
    private Color getMutedColorFromCode(int code) {
        switch (code) {
            case 1: return Color.PINK;
            case 2: return Color.LIGHTBLUE;
            case 3: return Color.LIGHTGREEN;
            case 4: return Color.LIGHTYELLOW;
            default: return Color.LIGHTGRAY;
        }
    }

    // an inner class which generates Numberlinks.java puzzles
    class ColorNumberLinksGenerator {
        private int[][] grid;
        private int gridSize;
        private int pairCount;
        private Random random;
        private Map<String, String> arrowMap = new HashMap<>();

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
            // generate the first endpoint at a random empty cell
            int x1 = random.nextInt(gridSize);
            int y1 = random.nextInt(gridSize);
            while (grid[x1][y1] != 0) {
                x1 = random.nextInt(gridSize);
                y1 = random.nextInt(gridSize);
            }
            grid[x1][y1] = color;
        
            // generate the second endpoint but ensure it's not the same as the first,not adjacent to the first and placed in an empty cell
            int x2 = random.nextInt(gridSize);
            int y2 = random.nextInt(gridSize);
            while (grid[x2][y2] != 0 || (x1 == x2 && y1 == y2) ||
                   (Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1)) {
                x2 = random.nextInt(gridSize);
                y2 = random.nextInt(gridSize);
            }
            grid[x2][y2] = color;
        }
        
        // checks if a move is within bounds and either on an empty tile or the target tile
        public boolean isSafe(int x, int y, int targetX, int targetY, int color) {
            return x >= 0 && x < gridSize && y >= 0 && y < gridSize &&
                   (grid[x][y] == 0 || (x == targetX && y == targetY));
        }
        
        // recursive method that solves for a single colour, leaving a trail of colour values and arrow directions
        private boolean solveColorLinkMarking(int x, int y, int targetX, int targetY, int color, boolean isStart) {
            if (x == targetX && y == targetY) return true;
        
            if (!isStart) grid[x][y] = -color; // mark path cell temporarily with negative value
        
            int[] dx = {0, 0, -1, 1}; 
            int[] dy = {-1, 1, 0, 0};
            String[] arrows = {"←", "→", "↑", "↓"};
        
            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];
                if (isSafe(newX, newY, targetX, targetY, color)) {
                    if (solveColorLinkMarking(newX, newY, targetX, targetY, color, false)) {
                        arrowMap.put(x + "," + y, arrows[i]);
                            if (x == targetX && y == targetY) return true; // map direction for GUI rendering
                        
                        return true;
                    }
                }
            }
        
            // if path didn't work then unmarks and backtracks
            if (!isStart) grid[x][y] = 0;
            return false;
        }
        
        // solves the puzzle for all colour pairs and stores arrow directions
        public boolean solvePuzzle() {
            arrowMap.clear();
            for (int color = 1; color <= pairCount; color++) {
                int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
        
                // find the two endpoints of the current colour
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (grid[i][j] == color) {
                            if (x1 == -1) {
                                x1 = i; y1 = j;
                            } else {
                                x2 = i; y2 = j;
                            }
                        }
                    }
                }
        
                // try solving for this pair
                if (!solveColorLinkMarking(x1, y1, x2, y2, color, true)) return false;
            }
            return true;
        }
        
        // verifies a puzzle is solvable by trying to solve all pairs without affecting the original grid
        public boolean verify() {
            int[][] tempGrid = new int[gridSize][gridSize];
            for (int i = 0; i < gridSize; i++) {
                System.arraycopy(grid[i], 0, tempGrid[i], 0, gridSize);
            }
        
            int[][] originalGrid = grid;
            grid = tempGrid; // use a copy to stop altering the real puzzle
            boolean solvable = true;
        
            for (int color = 1; color <= pairCount; color++) {
                int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
        
                // locate endpoints for the current colour
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
        
                // try solving for this pair
                if (!solveColorLinkMarking(x1, y1, x2, y2, color, true)) {
                    solvable = false;
                    break;
                }
            }
        
            grid = originalGrid; // restore original state
            return solvable;
        }        

        public int[][] getGrid() {
            return grid;
        }

        public Map<String, String> getArrowMap() {
            return arrowMap;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}