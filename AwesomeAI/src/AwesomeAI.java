import java.util.HashSet;
import java.util.Scanner;


public class AwesomeAI extends Player {
	AlphaBetaHasher abHash;
	ABState currentState;
	boolean opponentMadeAMove;
	//GameHistory gameHistory;
	
	public AwesomeAI(Scanner scanner) {
		super(scanner);
		//gameHistory=new GameHistory();
	}


	@Override
	public String think() {
		//if(getOpponentMove)
		//getOpponentMove();
		//hacked code
		AB_BlackBox box=new AB_BlackBox(getBoard(),getMyturn(),4);
		AB_BlackBox.Message mess=box.gimmeAMove();
		Move m;
		if(mess==AB_BlackBox.Message.NEED_TO_RECOMPUTE){
			System.err.println("Damn!! Something's really wrong with ab algorithm");
		}
		System.err.println("hithere");
		m=mess.getMove();

		board.move(m);
		
		//new code, not yet tested:
		/*
		if (opponentMadeAMove) { // in the event that we are the first move then this will be false
			Move opponentMove = getOpponentsMove(AlphaBetaHasher.reconstructState(currentState), getBoard());
			gameHistory.addMove(opponentMove);
		}
		AB_BlackBox box=new AB_BlackBox(getBoard(),getMyturn(),4);
		AB_BlackBox.Message mess=box.gimmeAMove(gameHistory);
		*/
		
		//prev code:
		/*
		if (opponentMadeAMove) { // in the event that we are the first move then this will be false
			Move opponentMove = getOpponentsMove(AlphaBetaHasher.reconstructState(currentState), getBoard());
			
			currentState = new ABState(opponentMove, currentState);
		}
		
		Move m = alphaBetaSearch(new ABState(currentState.move, currentState.parent), getMyturn());
		// perform the move before sending it
		board.move(m);
		currentState = new ABState(m, currentState);
		
		checkStateConsistency(currentState, getBoard()); // this is returning false for some reason 
		*/
		System.err.println(m.r1+" "+m.c1+" "+m.r2+" "+m.c2 + " "+ m.r3+" "+ m.c3); // print out the move we just did
		
		return m.r1+" "+m.c1+" "+m.r2+" "+m.c2 + " "+ m.r3+" "+ m.c3;
	}
	
	
	public int flipTurn(int turn) {
		if (turn == 1)
			return 2;
		else
			return 1;
	}
	public Move alphaBetaSearch(ABState state, int turn) {
		int v = maxValue(state, turn, -1*Const.INFINITY, Const.INFINITY);
		
		// we found the value we want, so now we need the move that corresponds to this value.
		HashSet<Move> moveSet = getMoveSet(state, turn);
		Move moveToMake = null;
		for (Move move : moveSet) {
			ABState testState = new ABState(move, state);
			if (abHash.hashedStates.containsKey(testState)) {
				if (abHash.hashedStates.get(testState) == v){
					moveToMake = move;
					break;
				}
			}
		}
		if (moveToMake == null)
			System.err.println("Something's not right. No move?");
		return moveToMake;
	}
	
	public int maxValue(ABState state, int turn, int alpha, int beta) {
		if (terminalTest(state, turn))
			return utilityOfState(state, turn);
		
		int v = -1*Const.INFINITY;
		HashSet<Move> moveSet = getMoveSet(state, turn);
		for (Move move : moveSet) {
			if (abHash.hashedStates.containsKey(state)) {
				v = abHash.hashedStates.get(state);
			} else {
				v = Math.max(v, minValue(resultantState(state, move), flipTurn(turn), alpha, beta));
			}
			state.setValue(v);
			abHash.hashedStates.put(state, state.value);
			if (v >= beta)
				return v;
			alpha = Math.max(alpha, v);
		}
		return v;
	}

	public int minValue(ABState state, int turn, int alpha, int beta) {
		if (terminalTest(state, turn))
			return utilityOfState(state, turn);
		
		int v = Const.INFINITY;
		HashSet<Move> moveSet = getMoveSet(state, turn);
		for (Move move : moveSet) {
			if (abHash.hashedStates.containsKey(state)) {
				v = abHash.hashedStates.get(state);
			} else {
				v = Math.min(v, maxValue(resultantState(state, move), flipTurn(turn), alpha, beta));
			}
			state.setValue(v);
			abHash.hashedStates.put(state, state.value);
			if (v <= alpha)
				return v;
			beta = Math.min(beta, v);
		}
		return v;
	}

	/*
	 * Compute what the new board would look like once we take a given move
	 */
	public ABState resultantState(ABState state, Move move) {
		return abHash.hashState(move, state);
	}

	// We go through all our marbles and get all possible locations we can move to.
	// TODO: take into account moves that involve special pieces
	public HashSet<Move> getMoveSet(ABState state, int turn) {
		Board board = AlphaBetaHasher.reconstructState(state);
		HashSet<Move> moveSet = new HashSet<Move>();
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				if (board.at(i, j) == turn) {
					HashSet<Integer> destinations = new HashSet<Integer>();
					board.legalMoves(i, j, destinations);
					for (Integer dest : destinations) {
						int r = dest / 25;
						int c = dest % 25;
						Move m = new Move(0, 0, 0, i, j, r, c, -1, -1);
						moveSet.add(m);
					}
				}
			}
		}
		return moveSet;
	}

	// make terminal tests that take into account that special pieces may be in target area?
	public boolean terminalTest(ABState state, int turn) {
		Board board = AlphaBetaHasher.reconstructState(state);
		return board.checkWin(turn);
		/*
		int[][] topHalfWinSpots = {{0, 12}, {1, 11}, {1, 13}, {2, 10},
				{2, 12}, {2, 14}, {3, 9}, {3, 11}, {3, 13}, {3, 15}};
		int[][] bottomHalfWinSpots = {{16, 12}, {15, 11}, {15, 13}, {14, 10},
				{14, 12}, {14, 14}, {13, 9}, {13, 11}, {13, 13}, {13, 15}};
		int[][] winCoords = turn == 1 ? topHalfWinSpots : bottomHalfWinSpots;

		for (int[] coord : winCoords) {
			if (board.at(coord[0], coord[1]) != turn) {
				return false;
			}
		}
		return true;
		*/
	}

	/*
	 * We will define the utility of a state as the sum of all distances between our marbles
	 * and their target location. As a result, we want to minimize this utility cost.
	 */
	public int utilityOfState(ABState state, int turn) {
		Board board = AlphaBetaHasher.reconstructState(state);
		
		int targetR = -1;
		int targetC = -1;
		if (turn == 1) {
			targetR = 0;
			targetC = 12;
		} else {
			targetR = 16;
			targetC = 12;
		}

		int utility = 0;
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				if (board.at(i, j) == turn) {
					utility += Board.dist(i, j, targetR, targetC);
				}
			}
		}
		return utility;
	}

	/*
	 * Get the opponent's move by comparing the old board with the new board to see what happened.
	 */
	public Move getOpponentsMove(Board board1, Board board2) {
		int r1 = -1;
		int c1 = -1;
		int r2 = -1;
		int c2 = -1;
		int r3 = -1;
		int c3 = -1;
		
		int oppTurn = getOpponentTurn();
		
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				if (board1.at(i, j) == oppTurn && board2.at(i, j) == 0) {
					r1 = i;
					c1 = j;
				}
				if (board1.at(i, j) == 0  && board2.at(i, j) == oppTurn) {
					r2 = i;
					c2 = j;
				}
				if (board1.at(i, j) == 0 && board2.at(i, j) == 3) {
					r3 = i;
					c3 = j;
				}
			}
		}

		System.err.println("Opponent's move: "+r1+" "+c1+" "+r2+" "+c2+" "+r3+" "+c3);

		return new Move(0, 0, 0, r1, c1, r2, c2, r3, c3);
	}

	public static void main(String args[]){
		int turn = 1;
		AwesomeAI p = new AwesomeAI(new Scanner(System.in));
		
		p.abHash = new AlphaBetaHasher();
		p.currentState = p.abHash.getRoot();
		System.err.println("Init state");
		p.printBoard(p.getBoard());
		p.checkStateConsistency(p.currentState, p.getBoard());
		
		while (true) {
			System.err.println("turn = "+turn+"   myturn = "+p.getMyturn());	
			if (turn == p.getMyturn()){
				System.err.println("It is my turn and I am thinking");	
				System.out.println(p.think());
				int status = p.getStatus();
				if(status<0){
					System.err.println("I lost. Status: " + status);
					break;
				}
				else if(status>0){
					System.err.println("I won");
					break;
				}
			} else {
				int res = p.getOpponentMove();
				
				if (res == -1)
					System.err.println("The server is messed up");
				else if (res == 1) {
					System.err.println("The other player won.");
					break;
				} else { 
					System.err.println("OK lemme think...");
					p.opponentMadeAMove = true;
				}

			}
			System.err.println(p.getBoard().toString('*'));
			turn = 3-turn;
		}	
	}
	
	public void printBoard(Board board) {
		System.err.println(board.toString('0'));
	}
	
	public void printStateConsistency(ABState state, Board board) {
		System.err.println("\n************************************");
		checkStateConsistency(state, board);
		System.err.println("Current board:");
		printBoard(board);
		System.err.println("Current board state:");
		printBoard(AlphaBetaHasher.reconstructState(state));
		System.err.println("************************************\n");
	}
	
	// Checks if the given state is a correct representation of the given board
	public void checkStateConsistency(ABState state, Board board) {
		System.err.println("Is state consistent? " + state.equalsBoard(board));
	}
	
	public int getOpponentTurn() {
		return flipTurn(getMyturn());
	}
}
