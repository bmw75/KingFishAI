import java.util.ArrayList;
import java.util.HashSet;

public class MoveHasher {
	public static Board initialState;

	State root;
	HashSet<State> hashedStates;

	public MoveHasher() {
		initialState = generateNewInitialBoard();
		root = new State(null, null);
		hashedStates = new HashSet<State>();
	}

	public State getRoot() {
		return root;
	}

	// given a move and a state, hashes the resultant state. Returns this state.
	public State hashState(Move m, State parent) {
		State st = new State(m, parent);
		hashedStates.add(st);
		return st;
	}

	/*
	 * Given a state, always call reconstructState to get the board it represents.
	 * reconstructState goes through the series of hashed moves to generate
	 * the resultant board.
	 */
	public static Board reconstructState(State s) {
		if (s.parent == null) {
			return generateNewInitialBoard();
		} else {
			Board b = reconstructState(s.parent);
			b.move(s.move);
			return b;
		}
	}
	
	private static Board generateNewInitialBoard() {
		return new Board(Const.BOARD_FILE);
	}
}
