public class EightQueensProblem {
    private static int n = 8;

    private static void PrintBoard(int[][] board) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

public static void main(String[] args) {
    // Initialising the chessboard with 0
    int[][] board = new int[n][n];

    board[0][0] = 1;
    board[1][2] = 1;
    board[2][4] = 1;
    board[3][6] = 1;

    PrintBoard(board);
    }
}