import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.ArrayList;

public class AwesomeAI extends Player {
	public static int[][] initialBoard = new int[Const.BOARD_HEIGHT][Const.BOARD_WIDTH];
	State currentState;
	boolean opponentMadeAMove;
	ArrayList<Move> aStarWinningMoveList;
  AStarBlackBox aStarBlackBox;

	public AwesomeAI(Scanner scanner) {
		super(scanner);
	}

	@Override
	public String think() {
		if (opponentMadeAMove) { // if we are the first move then this will be false at first
			Move opponentMove = getOpponentsMove(currentState.reconstructBoardArray(), Util.getArrayFromBoard(getBoard()));
			currentState = new State(opponentMove, currentState);
		}
		
		Move m = null;

		if (!aStarWinningMoveList.isEmpty()) {
			m = aStarWinningMoveList.remove(0);
		}

		if (m == null) {
			System.err.println("Empty move list happened. Calculating new sequence.");
			aStarWinningMoveList = aStarBlackBox.aStarSearch(currentState);
			m = aStarWinningMoveList.remove(0);
		}
  	if (!board.validateSimpleMove(m.r1,m.c1,m.r2,m.c2,m.r3,m.c3,getMyturn())) {
			System.err.println("Invalid move happened. There were still " +
					(aStarWinningMoveList.size()) + " moves left. Calculating new sequence.");
			aStarWinningMoveList = aStarBlackBox.aStarSearch(currentState);
			m = aStarWinningMoveList.remove(0);
		}

//    System.err.println("Is move valid? " + board.validateSimpleMove(m.r1,m.c1,m.r2,m.c2,m.r3,m.c3,getMyturn()));

		// perform the move before sending it
		board.move(m);
		currentState = new State(m, currentState);
		
//		Util.checkStateConsistency(currentState, getBoard());

		System.err.println("My move: " + m.r1+" "+m.c1+" "+m.r2+" "+m.c2 + " "+ m.r3+" "+ m.c3);
	
		return m.r1+" "+m.c1+" "+m.r2+" "+m.c2 + " "+ m.r3+" "+ m.c3;
	}
	
	/*
	 * Get the opponent's move by comparing the old board with the new board to see what happened.
	 */
	public Move getOpponentsMove(int[][] board1, int[][] board2) {
		int r1 = -1;
		int c1 = -1;
		int r2 = -1;
		int c2 = -1;
		int r3 = -1;
		int c3 = -1;

		int oppTurn = getOpponentTurn();

		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				if (board1[i][j] == oppTurn && board2[i][j] == 0) {
					r1 = i;
					c1 = j;
				}
				if (board1[i][j] == 0  && board2[i][j] == oppTurn) {
					r2 = i;
					c2 = j;
				}
				if (board1[i][j] == 0 && board2[i][j] == 3) {
					r3 = i;
					c3 = j;
				}
			}
		}

		System.err.println("Opponent's move: "+r1+" "+c1+" "+r2+" "+c2+" "+r3+" "+c3);

		return new Move(0, 0, 0, r1, c1, r2, c2, r3, c3);
	}

	public int getOpponentTurn() {
		return Util.flipTurn(getMyturn());
	}

	public static void main(String args[]){
		int turn = 1;
		Scanner sc = new Scanner(System.in);
		createInitialBoardFile(sc);
		AwesomeAI p = new AwesomeAI(sc);
		
		p.currentState = new State(null, null); // first state: no move, no parent
		System.err.println("Init state");
		Util.printBoard(p.getBoard());
		Util.checkStateConsistency(p.currentState, p.getBoard());
		for (int i = 0; i < Const.BOARD_HEIGHT; i++) {
			for (int j = 0; j < Const.BOARD_WIDTH; j++) {
				AwesomeAI.initialBoard[i][j] = p.getBoard().at(i, j);
			}
		}

		p.aStarBlackBox = new AStarBlackBox(p.getMyturn());
		p.aStarBlackBox.setMoveCutoff(5);
		p.aStarWinningMoveList = p.aStarBlackBox.aStarSearch(p.currentState);
		
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
					Util.printBoard(p.getBoard());
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

	// creates the file initboard.txt which we will use as our initial board
	public static void createInitialBoardFile(Scanner sc) {
		StringBuilder boardBuilder = new StringBuilder("");
		System.in.mark(439);
		for (int i = 0; i < 18; i++) {
			boardBuilder.append(sc.nextLine());
			boardBuilder.append("\n");
		}

		try {
			System.in.reset();
			sc.nextLine();
			BufferedWriter out = new BufferedWriter(new FileWriter(Const.BOARD_FILE));
			out.write(boardBuilder.toString());
			out.close();
		} catch (IOException e) {
			System.err.println(e.toString());
		}
	}
}
