import javafx.application.Platform;
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
        boolean[] hasAttemptedSolve = {false}; // using array to mutate inside lambda

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
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to solve the puzzle completely. Some pairs could not be connected.");
                    alert.showAndWait();
                    return;
                }

                // Only show 'already solved' message on second+ click
                if (generator.alreadySolved && hasAttemptedSolve[0]) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Puzzle is already solved.");
                    alert.showAndWait();
                }

                hasAttemptedSolve[0] = true;

                // draw the solution
                animateSolution(grid, generator.getGrid(), generator.getArrowMap(), gridSize);

            });

            new Thread(task).start();
        });

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
            case 5 ->
                Color.ORANGE;
            case 6 ->
                Color.PURPLE;
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
            case 5 ->
                Color.SANDYBROWN;
            case 6 ->
                Color.PLUM;
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
        private boolean alreadySolved = false;

        public ColorNumberLinksGenerator(int gridSize) {
            this.gridSize = gridSize;
            this.pairCount = Math.min((gridSize * gridSize) / 10, 6); // allows more colours
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

        // node used for A* pathfinding (includes cost and heuristic estimate)
        class Node implements Comparable<Node> {

            int x, y, cost, estimate;

            Node(int x, int y, int cost, int estimate) {
                this.x = x;
                this.y = y;
                this.cost = cost;
                this.estimate = estimate;
            }

            public int compareTo(Node o) {
                return Integer.compare(this.cost + this.estimate, o.cost + o.estimate);
            }
        }

        // Manhattan distance heuristic credit : https://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html
        private int heuristic(int x, int y, int tx, int ty) {
            return Math.abs(x - tx) + Math.abs(y - ty);
        }

        // returns direction index (used for arrow rendering)
        private int getDirection(int x1, int y1, int x2, int y2) {
            if (x2 == x1 && y2 == y1 - 1) {
                return 0;
            }
            if (x2 == x1 && y2 == y1 + 1) {
                return 1;
            }
            if (x2 == x1 - 1 && y2 == y1) {
                return 2;
            }
            if (x2 == x1 + 1 && y2 == y1) {
                return 3;
            }
            return -1;
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
                        openSet.add(new Node(nx, ny, current.cost + 1, heuristic(nx, ny, targetX, targetY))); // add neighbor to open set with updated cost
                    }
                }
            }

            return false; // no path found between endpoints
        }

        // attempts to solve all pairs using A* search
        public boolean solvePuzzle() {
            if (alreadySolved) {
                return true; // avoid resolving if already done
            }
            arrowMap.clear();
            for (int color = 1; color <= pairCount; color++) {
                int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) { // finds the two endpoints
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
                if (!solveColorLinkAStar(x1, y1, x2, y2, color)) {
                    return false;
                }
            }
            alreadySolved = true;
            return true;
        }

        // validates that the puzzle is solvable by using copy of grid 
        public boolean verify() {
            int[][] tempGrid = new int[gridSize][gridSize];
            for (int i = 0; i < gridSize; i++) {
                System.arraycopy(grid[i], 0, tempGrid[i], 0, gridSize);
            }
            for (int color = 1; color <= pairCount; color++) {
                int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (tempGrid[i][j] == color) {
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
                if (!verifyAStarAndMark(tempGrid, x1, y1, x2, y2, color)) {
                    return false;
                }
            }
            return true;
        }

        // same A* logic used for verification without affecting real grid
        private boolean verifyAStarAndMark(int[][] tempGrid, int startX, int startY, int targetX, int targetY, int color) {

            int[] dx = {0, 0, -1, 1};
            int[] dy = {-1, 1, 0, 0};

            Map<String, int[]> parent = new HashMap<>();
            boolean[][] visited = new boolean[gridSize][gridSize];
            PriorityQueue<Node> openSet = new PriorityQueue<>();

            openSet.add(new Node(startX, startY, 0, heuristic(startX, startY, targetX, targetY)));
            visited[startX][startY] = true;

            while (!openSet.isEmpty()) {
                Node current = openSet.poll();
                if (current.x == targetX && current.y == targetY) {
                    int x = targetX, y = targetY;
                    while (!(x == startX && y == startY)) {
                        int[] prev = parent.get(x + "," + y);

                        if (!(prev[0] == startX && prev[1] == startY)) {
                            tempGrid[prev[0]][prev[1]] = -color;
                        }
                        x = prev[0];
                        y = prev[1];
                    }
                    return true;
                }

                for (int i = 0; i < 4; i++) {
                    int nx = current.x + dx[i];
                    int ny = current.y + dy[i];

                    if (nx >= 0 && nx < gridSize && ny >= 0 && ny < gridSize
                            && (tempGrid[nx][ny] == 0 || (nx == targetX && ny == targetY)) && !visited[nx][ny]) {
                        visited[nx][ny] = true;
                        parent.put(nx + "," + ny, new int[]{current.x, current.y});
                        openSet.add(new Node(nx, ny, current.cost + 1, heuristic(nx, ny, targetX, targetY)));
                    }
                }
            }
            return false;
        }

        public int[][] getGrid() {
            return grid;
        }

        public Map<String, String> getArrowMap() {
            return arrowMap;
        }
    }

// animates the solution paths one by one for each colour using muted colours
    private void animateSolution(GridPane grid, int[][] solvedGrid, Map<String, String> arrows, int gridSize) {
        Task<Void> animationTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // collects all unique colour values
                Set<Integer> colors = new HashSet<>();
                for (int[] row : solvedGrid) {
                    for (int cell : row) {
                        if (cell < 0) {
                            colors.add(Math.abs(cell)); // store positive color code
                        }
                    }
                }

                // loops through each colour animate its own path
                for (int color : colors) {
                    for (int row = 0; row < gridSize; row++) {
                        for (int col = 0; col < gridSize; col++) {
                            int value = solvedGrid[row][col];
                            String key = row + "," + col;

                            // if current cell belongs to current path or is a start point with arrow
                            if (value == -color || (value == color && arrows.containsKey(key))) {
                                int finalRow = row;
                                int finalCol = col;

                                // run visual update 
                                Platform.runLater(() -> {
                                    Rectangle rect = new Rectangle(50, 50);
                                    if (value == -color) {
                                        rect.setFill(getMutedColorFromCode(color)); // muted color for path
                                    } else {
                                        rect.setFill(getColorFromCode(color)); // original color for endpoint
                                    }

                                    StackPane cellPane = new StackPane(rect); // holds the cell and arrow
                                    if (arrows.containsKey(key)) {
                                        Label arrow = new Label(arrows.get(key));
                                        arrow.setStyle("-fx-font-size: 20; -fx-text-fill: black;"); // adds the direction arrow if allowed
                                        cellPane.getChildren().add(arrow);
                                    }
                                    grid.add(cellPane, finalCol, finalRow);
                                });

                                Thread.sleep(50); // used to create the effect of "one by one"
                            }
                        }
                    }
                    Thread.sleep(200); // pausing between different colours
                }
                return null;
            }
        };
        new Thread(animationTask).start(); // has to be done on a separate thread else it will just do the animation and delete the actual puzzle
    }

    public static void main(String[] args) {
        launch(args);
    }
}
