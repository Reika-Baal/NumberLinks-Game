import java.util.Random;

public class NumberLinks {
    private int[][] grid;
    private int size;
    private Random random;

    public NumberLinks(int size) {
        this.size = size;
        this.grid = new int[size][size];
        this.random = new Random();
    }

    public void generate() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = 0;
            }
        }

        int pairCount = size / 2; // number of pairs generated
        for (int num = 1; num <= pairCount; num++) {
            placePair(num);
        }
    }

    // randomly place the first point
    private void placePair(int number) {
        int x1 = random.nextInt(size);
        int y1 = random.nextInt(size);

        while (grid[x1][y1] != 0) {
            x1 = random.nextInt(size);
            y1 = random.nextInt(size);
        }

        grid[x1][y1] = number;

        int x2 = random.nextInt(size);
        int y2 = random.nextInt(size);

        while (grid[x2][y2] != 0 || (x1 == x2 && y1 == y2)) {
            x2 = random.nextInt(size);
            y2 = random.nextInt(size);
        }

        grid[x2][y2] = number;
    }

    // this is used to check if we are within the grind bounds as we do not want any exceptions 
    public boolean isSafe(int x, int y, int targetX, int targetY, int currentNumber) {
        if (x < 0 || x >= size || y < 0 || y >= size) return false;

        // important as it first checks if the cell is unoccupied and then checks if other number paths are blocking it
        return grid[x][y] == 0 || (x == targetX && y == targetY);
    }

    public boolean solveNumberlink(int x, int y, int targetX, int targetY, int currentNumber) {
        if (x == targetX && y == targetY) return true; 

        // marking it as part of the path
        grid[x][y] = currentNumber;

        // movement
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        // all moves
        for (int i = 0; i < 4; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];

            if (isSafe(newX, newY, targetX, targetY, currentNumber)) {
                if (solveNumberlink(newX, newY, targetX, targetY, currentNumber)) {
                    return true;
                }
            }
        }

        // backtracking is done for unmarking the cell
        grid[x][y] = 0;
        return false;
    }

    public boolean verify() {
        int[][] originalGrid = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(grid[i], 0, originalGrid[i], 0, size);
        }

        // verifies that each pair is valid
        for (int num = 1; num <= size / 2; num++) {
            int x1 = -1, y1 = -1, x2 = -1, y2 = -1;

            // looks for the cooridinate for the pairs
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (grid[i][j] == num) {
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

            // solve for the current pair
            if (!solveNumberlink(x1, y1, x2, y2, num)) {
                return false;
            }

            // restore the grid for the next pair
            for (int i = 0; i < size; i++) {
                System.arraycopy(originalGrid[i], 0, grid[i], 0, size);
            }
        }

        return true;
    }

    public void printGrid() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Used to make the grid and can be dynamically changed    
    public static void main(String[] args) {
        int gridSize = 6;
        NumberLinks generator = new NumberLinks(gridSize);

        do {
            generator.generate();
        } while (!generator.verify());

        System.out.println("Generated a solvable Numberlink puzzle:");
        generator.printGrid();
    }
}