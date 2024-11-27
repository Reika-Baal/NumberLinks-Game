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

    // empty grid
    public void generate() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = 0;
            }
        }
        // pairs that will be generated
        int pairCount = size / 2;
        for (int num = 1; num <= pairCount; num++) {
            placePair(num);
        }
    }

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

        generator.generate();
        generator.printGrid();
    }
}