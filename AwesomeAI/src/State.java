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
		int[][] board = this.reconstructBoardArray();
		return Arrays.deepHashCode(board);
	}

	@Override
	public boolean equals(Object o) {
		State otherState = (State) o;
		return equalsBoard(otherState.reconstructBoardArray());
	}

	public boolean equalsBoard(int[][] b) {
		return Arrays.deepEquals(this.reconstructBoardArray(), b);
	}

	public boolean equalsBoard(Board b) {
		int[][] board = this.reconstructBoardArray();
		for (int i = 0; i < Const.BOARD_HEIGHT; i++) {
			for (int j = 0; j < Const.BOARD_WIDTH; j++) {
				if (b.at(i, j) != board[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * reconstructBoardArray will get the board array that the current
	 * state represents. It goes from the current state
	 * all the way to the initial state and reproduces all the moves
	 * in order to generate the board.
	 */
	public int[][] reconstructBoardArray() {
		if (this.parent == null) {
			return Util.copyBoardArray(AwesomeAI.initialBoard);
		} else {
			int[][] b = this.parent.reconstructBoardArray();
			Util.performMoveOnBoard(this.move, b);
			return b;
		}
	}

	private static Board generateNewInitialBoard() {
		return new Board(Const.BOARD_FILE);
	}
}
