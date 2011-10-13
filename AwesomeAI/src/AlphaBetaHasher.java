import java.util.ArrayList;
import java.util.HashMap;


public class AlphaBetaHasher {
	public static Board initialState;
	
	ABState root;
	HashMap<ABState, Integer> hashedStates; // maps states to their minimax values

	public AlphaBetaHasher() {
		initialState = generateNewInitialBoard();
		root = new ABState(0, 0, null, null);
		hashedStates = new HashMap<ABState, Integer>();
	}

	public ABState getRoot() {
		return root;
	}

	// given a move and a state, hashes the resultant state. Returns this state.
	public ABState hashState(Move m, ABState parent) {
		ABState st = new ABState(0, 0, m, parent);
		hashedStates.put(st, st.value);
		return st;
	}

	//for adding to the root
	public ABState hashState(Move m) {
		ABState st = new ABState(0,0,m,root);
		hashedStates.put(st, st.value);
		return st;
	}

	/*
	 * given a state, always call reconstructState to get the board it represents.
	 * reconstructState goes through the series of hashed moves to generate the resultant board.
	 */
	public static Board reconstructState(ABState s) {
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