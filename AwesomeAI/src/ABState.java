import java.util.Arrays;


public class ABState {
	int alpha;
	int beta;
	int value;
	Move move;
	ABState parent;

	public ABState(Move m, ABState p) {
		value = -1;
		move = m;
		parent = p;

	}
	public ABState(int a, int b, Move m, ABState p) {
		alpha = a;
		beta = b;
		move = m;
		parent = p;
		value = -1;
	}

	public void setAlphaBeta(int a, int b) {
		alpha = a;
		beta = b;
	}

	public void setValue(int v) {
		value = v;
	}
	
	@Override
	public int hashCode() {
		Board b = AlphaBetaHasher.reconstructState(this);
		int[][] board = new int[17][25];
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				board[i][j] = b.at(i, j);
			}
		}
		return Arrays.deepHashCode(board);
	}
	
	@Override
	public boolean equals(Object o) {
		ABState otherState = (ABState) o;
		return equalsBoard(AlphaBetaHasher.reconstructState(otherState));
	}
	
	public boolean equalsBoard(Board b) {
		Board myBoard = AlphaBetaHasher.reconstructState(this);
		int[][] board1 = new int[17][25];
		int[][] board2 = new int[17][25];
		
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				board1[i][j] = myBoard.at(i, j);
				board2[i][j] = b.at(i, j);
			}
		}
		return Arrays.deepEquals(board1, board2);
	}
}