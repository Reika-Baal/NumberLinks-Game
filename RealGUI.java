import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.concurrent.Task;
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

        ProgressBar progressBar = new ProgressBar();
        progressBar.setVisible(false);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: purple;");

        // when solve is clicked solves the puzzle by marking the connecting paths and redraw the grid
        solveButton.setOnAction(e -> {
            progressBar.setVisible(true);
            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    return generator.solvePuzzle();
                }
            };

            task.setOnSucceeded(ev -> {
                boolean success = task.getValue();
                progressBar.setVisible(false);

                if (!success) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Unexpected Failure.");
                    alert.showAndWait();
                    return;
                }

                VBox buttonsBox = new VBox(10, solveButton, backButton);
                buttonsBox.setAlignment(Pos.CENTER);

                VBox puzzleBox = new VBox(20, grid, buttonsBox, progressBar);
                puzzleBox.setAlignment(Pos.CENTER);

                puzzleScene = new Scene(puzzleBox, 500, 600);
                primaryStage.setScene(puzzleScene);
            }

    private void openLevelSelectScreen(Stage primaryStage) {
        VBox levelBox = new VBox(10);
        for (int i = 1; i <= 5; i++) {
            Button levelBtn = new Button("Level " + i);
            int level = i;
            levelBtn.setOnAction(e -> openPuzzleScreen(primaryStage, level));
            levelBox.getChildren().add(levelBtn);
        }
        levelBox.setAlignment(Pos.CENTER);
        levelSelectScene = new Scene(levelBox, 500, 600);
        primaryStage.setScene(levelSelectScene);
    }

    // maps the integer code to a JavaFX colour
    private Color getColorFromCode(int code) {
        return switch (code) {
            case 1 ->
                Color.RED;
            case 2 ->
                Color.BLUE;
            case 3 ->
                Color.GREEN;
            case 4 ->
                Color.YELLOW;
            default ->
                Color.LIGHTGRAY;
        };
    }

    // maps integer codes to muted colours
    private Color getMutedColorFromCode(int code) {
        return switch (code) {
            case 1 ->
                Color.PINK;
            case 2 ->
                Color.LIGHTBLUE;
            case 3 ->
                Color.LIGHTGREEN;
            case 4 ->
                Color.LIGHTYELLOW;
            default ->
                Color.LIGHTGRAY;
        };
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

        // fills the grid with empty cells and places pairs
        public void generate() {
            for (int i = 0; i < gridSize; i++) {
                Arrays.fill(grid[i], 0);
            }
            for (int color = 1; color <= pairCount; color++) {
                placePair(color);
            }
        }

        // randomly places a color pair making sure they are not adjacent or the same
        private void placePair(int color) {
            int x1, y1, x2, y2;
            do {
                x1 = random.nextInt(gridSize);
                y1 = random.nextInt(gridSize);
            } while (grid[x1][y1] != 0);
            grid[x1][y1] = color;

            do {
                x2 = random.nextInt(gridSize);
                y2 = random.nextInt(gridSize);
            } while (grid[x2][y2] != 0 || (x1 == x2 && y1 == y2)
                    || (Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1));
            grid[x2][y2] = color;
        }

        // checks if a tile can be visited during pathfinding
        public boolean isSafe(int x, int y, int targetX, int targetY, int color) {
            return x >= 0 && x < gridSize && y >= 0 && y < gridSize
                    && (grid[x][y] == 0 || (x == targetX && y == targetY));
        }

        // solves a single color pair using A* and stores the path
        private boolean solveColorLinkAStar(int startX, int startY, int targetX, int targetY, int color) {

            // movement 
            int[] dx = {0, 0, -1, 1};
            int[] dy = {-1, 1, 0, 0};
            String[] directions = {"←", "→", "↑", "↓"};

            // tracking visited cells
            boolean[][] visited = new boolean[gridSize][gridSize];
            Map<String, int[]> parent = new HashMap<>(); // map used to recreate path after reaching the goal

            // priority queue ordered by cost + heuristic ,A* search
            PriorityQueue<Node> openSet = new PriorityQueue<>();
            openSet.add(new Node(startX, startY, 0, heuristic(startX, startY, targetX, targetY)));
            visited[startX][startY] = true;
            while (!openSet.isEmpty()) {
                Node current = openSet.poll();

                // if reached target recreates the path
                if (current.x == targetX && current.y == targetY) {
                    int x = targetX, y = targetY;
                    // backtracking from target to start using parent map
                    while (!(x == startX && y == startY)) {
                        int[] prev = parent.get(x + "," + y);
                        int dir = getDirection(prev[0], prev[1], x, y); // store arrow direction for GUI
                        if (dir != -1) {
                            arrowMap.put(prev[0] + "," + prev[1], directions[dir]);
                        }

                        if (!(prev[0] == startX && prev[1] == startY)) { // mark grid with negative color for path but not at endpoints
                            grid[prev[0]][prev[1]] = -color;
                        }

                        // moves to previous cell
                        x = prev[0];
                        y = prev[1];
                    }

                    return true;
                }

                // all directions
                for (int i = 0; i < 4; i++) {
                    int nx = current.x + dx[i];
                    int ny = current.y + dy[i];

                    // if move is valid and not yet visited
                    if (isSafe(nx, ny, targetX, targetY, color) && !visited[nx][ny]) {
                        visited[nx][ny] = true;
                        parent.put(nx + "," + ny, new int[]{current.x, current.y});
                    }
                }
            }

            return false; // no path found between endpoints
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
