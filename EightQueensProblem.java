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
    
            // upper diagonal,left side
            for (i = row, j = col; i >= 0 && j >= 0; i--, j--) {
                if (board[i][j] == 1)
                    return false;
            }
    
            // lower diagonal,left side
            for (i = row, j = col; j >= 0 && i < n; i++, j--) {
                if (board[i][j] == 1)
                    return false;
            }
            // don't need to check right side since there are no queens there YET as they haven't been placed
            // only needs to check if there is no conflict at the current position with another one on the left
            return true;
        }    

public static void main(String[] args) {
    // initialising the chessboard with 0, where 1 represents a Queen
    int[][] board = new int[n][n];

    board[0][0] = 1;
    board[1][2] = 1;
    board[2][4] = 1;
    board[3][6] = 1;

    PrintBoard(board);
    }
}