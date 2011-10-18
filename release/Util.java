
public class Util {
	public static void printState(State state) {
		printBoard(state.reconstructBoard());
	}

	public static void printBoard(Board board) {
		System.err.println(board.toString('0'));
	}

	public static void printStateConsistency(State state, Board board) {
		System.err.println("\n************************************");
		checkStateConsistency(state, board);
		System.err.println("Current board:");
		printBoard(board);
		System.err.println("Current state representation:");
		printBoard(state.reconstructBoard());
		System.err.println("************************************\n");
	}

	// Checks if the given state is a correct representation of the given board
	public static void checkStateConsistency(State state, Board board) {
		System.err.println("Is state consistent? " + state.equalsBoard(board));
	}

	public static void printMove(Move m) {
		System.err.println("Move: "+m.r1+" "+m.c1+" "+m.r2+" "+m.c2+ " "+m.r3+" "+m.c3);
	}

	public static int flipTurn(int turn) {
		if (turn == 1)
			return 2;
		else
			return 1;
	}
}
