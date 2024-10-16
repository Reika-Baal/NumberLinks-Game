public class EightQueensProblem {
    private static int n = 8; // 8x8 chessboard size

    private static void PrintBoard(int[][] board) {
        // loop to print out the chessboard
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    // function that checks the constraint of the board and if a queen can be placed via [row][col]
    private static boolean isSafe(int[][] board, int row, int col) {
        int i, j;

        // left side
        for (i = 0; i < col; i++) {
            if (board[row][i] == 1)
                return false;
        }

        // upper diagonal, left side
        for (i = row, j = col; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j] == 1)
                return false;
        }

        // lower diagonal, left side
        for (i = row, j = col; j >= 0 && i < n; i++, j--) {
            if (board[i][j] == 1)
                return false;
        }

        // don't need to check right side since there are no queens there YET as they haven't been placed
        // only needs to check if there is no conflict at the current position with another one on the left
        return true;
    }    

    // recursive function to solve the problem
    private static boolean solvenQueens(int[][] board, int col) {
        // base case if all queens are placed
        if (col >= n) {
            return true;
        }

        // tries placing a queen in each row of the current column
        for (int i = 0; i < n; i++) {
            // condition is valid to be placed
            if (isSafe(board, i, col)) {
                board[i][col] = 1;

                if (solvenQueens(board, col + 1)) {
                    return true;
                }

                // if placing queen doesn't lead to a solution then it backtracks
                board[i][col] = 0;
            }
        }

        // no queen can be placed in column, return false
        return false;
    }

    public static void main(String[] args) {
        // board with initialization of 0
        int[][] board = new int[n][n];

        if (!solvenQueens(board, 0)) {
            System.out.println("There is no solution to this variation.");
        } else {
            PrintBoard(board);
        }
    }
}
