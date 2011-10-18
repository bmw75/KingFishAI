import java.util.Arrays;

public class State {
	Move move;
	State parent;

	/*
	 * A state is represented as a move and a pointer to its parent state.
	 * This way any board state can be reconstructed by reproducing
	 * all the moves.
	 */
	public State(Move m, State p) {
		move = m;
		parent = p;
	}

	public State getParent() {
		return parent;
	}

	public Move getMove() {
		return move;
	}

	@Override
	public int hashCode() {
		Board b = MoveHasher.reconstructState(this);
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
		State otherState = (State) o;
		return equalsBoard(otherState.reconstructBoard());
	}

	public boolean equalsBoard(Board b) {
		Board myBoard = this.reconstructBoard();
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

	/*
	 * reconstructBoard will get the board that the current
	 * state represents. It goes from the current state
	 * all the way to the initial state and reproduces all the moves
	 * along the way in order to generate the board.
	 */
	public Board reconstructBoard() {
		if (this.parent == null) {
			return generateNewInitialBoard();
		} else {
			Board b = this.parent.reconstructBoard();
			b.move(this.move);
			return b;
		}
	}

	private static Board generateNewInitialBoard() {
		return new Board(Const.BOARD_FILE);
	}
}
