import java.util.HashSet;

public class Util {

	/*
	 * Helpful board array functions
	 * A lot of these functions have similar functionality to some of
	 * the Board class functions, but they can now be applied straight
	 * on a board array, instead of having to create a whole new Board class
	 * instance, which is slower.
	 */
	public static int[][] copyBoardArray(int[][] board) {
		int[][] newBoard = new int[Const.BOARD_HEIGHT][Const.BOARD_WIDTH];
		for (int i = 0; i < Const.BOARD_HEIGHT; i++) {
			for (int j = 0; j < Const.BOARD_WIDTH; j++) {
				newBoard[i][j] = board[i][j];
			}
		}
		return newBoard;
	}

	public static void performMoveOnBoard(Move m, int[][] board) {
		if (m.r3!=-1 && m.c3 !=-1){
			board[m.r3][m.c3] = 3;
		}
		board[m.r2][m.c2] = board[m.r1][m.c1];
		board[m.r1][m.c1] = 0;		
	}

	public static int[][] getArrayFromBoard(Board board) {
		int[][] newBoard = new int[Const.BOARD_HEIGHT][Const.BOARD_WIDTH];
		for (int i = 0; i < Const.BOARD_HEIGHT; i++) {
			for (int j = 0; j < Const.BOARD_WIDTH; j++) {
				newBoard[i][j] = board.at(i, j);
			}
		}
		return newBoard;
	}

	public static int euclideanDistSq(int r1, int c1, int r2,int c2){
		return (r2-r1)*(r2-r1)+(c2-c1)*(c2-c1);
	}

	public static int dist(int r1, int c1, int r2,int c2){
		int dr = r2 - r1;
		int ds = (r1+c1-r2-c2)/2;
		if (dr*ds>0){
			return Math.abs(dr)+ Math.abs(ds);
		}
		else{
			return Math.max(Math.abs(dr), Math.abs(ds));
		}
	}

	public static void getBoardLegalMoves(int r, int c, HashSet<Integer> moves, int[][] board){
		moves.clear();
		/* Immediate moves */
		int[][] immediate = new int[][] {{r,c-2},{r-1,c-1},{r-1,c+1},{r,c+2},{r+1,c+1},{r+1,c-1}};		
		/* First add the immediate moves */
		for(int i=0; i<immediate.length; i++){
			int r2 = immediate[i][0];
			int c2 = immediate[i][1];
			if(0<=r2 && r2<17 && 0<=c2 && c2<25 && board[r2][c2]==0)
				moves.add(25*r2+c2);
		}
		/* Now add all jumps recursively */
		jump(r,c,moves, board);
	}

	private static void jump(int r, int c, HashSet<Integer> moves, int[][] board){
		/* Jump moves */
		int[][] jumps = new int[][] {{r,c-2,r,c-4},{r-1,c-1,r-2,c-2},{r-1,c+1,r-2,c+2},{r,c+2,r,c+4},{r+1,c+1,r+2,c+2},{r+1,c-1,r+2,c-2}};
		for(int i=0; i<jumps.length; i++){
			int r1=jumps[i][0];
			int c1=jumps[i][1];
			int r2=jumps[i][2];
			int c2=jumps[i][3];
			if(0<=r2 && r2<17 && 0<=c2 && c2<25 && board[r1][c1]>0 && board[r2][c2]==0 && !moves.contains(25*r2+c2)){
				moves.add(25*r2+c2);
				jump(r2,c2,moves, board);
			}
		}
	}


	/*
	 * progress: return what stage of the game is happening
	 * inputs: State state, int prevProg
	 * 		state: current state of the game
	 * 		prevProg: last known progress of game
	 * outputs: int Const.OPENING, Const.INTERACTING, Const.CLOSING
	 * Notes: Once you enter into a new stage you can not go back to a younger stage
	 * 		  If Unknown and Not Interacting -> defaults to Const.OPENING,
	 * 		  PrevProg should only == Const.UNKNOWN first time called
	 */
	public static int progress(Board board, int prevProg){
		if (prevProg == Const.UNKNOWN){
			if (interacting(board)) return Const.INTERACTING; else return Const.OPENING;
		}
		if (prevProg == Const.OPENING)
			if (interacting(board)) return Const.INTERACTING; else return Const.OPENING;
		if (prevProg == Const.INTERACTING)
			if (!interacting(board)) return Const.CLOSING; else return Const.INTERACTING;
		if (prevProg == Const.CLOSING)
			return Const.CLOSING;
		return prevProg; // If no decision can be made return prevProg
						 // (other option is to simply return unknown, but prevProg should reduce computations)
	}

	private static boolean interacting(Board board){
		Piece[] myPieces = getPieces(board,1);
		Piece[] hisPieces = getPieces(board,2);
		for (Piece mine:myPieces){
			for (Piece his:hisPieces){
				if (euclideanDistSq(mine.r,mine.c,his.r,his.c) < 9) return true;
			}
		}
		
		return false;
	}
	
	private static Piece[] getPieces(Board board, int player){
		Piece[] pieces = new Piece[10]; 
		int index = 0;
		for (int i = 0; i < Const.BOARD_HEIGHT; i++) {
			for (int j = 0; j < Const.BOARD_WIDTH; j++) {
				if (board.at(i, j) == player){
					pieces[index] = new Piece(i,j);
					index++;
				}
			}
		}
		return pieces;
	}

	/*
	 * Other helpful functions
	 */
	public static int flipTurn(int turn) {
		if (turn == 1)
			return 2;
		else
			return 1;
	}

	/*
	 * Helpful print functions for great justice.
	 */
	public static void printGameProgress(int progress) {
		System.err.print("Progress is: ");
		switch (progress) {
			case Const.UNKNOWN:
				System.err.print("Unknown\n");
				break;
			case Const.OPENING:
				System.err.print("Opening\n");
				break;
			case Const.INTERACTING:
				System.err.print("Interacting\n");
				break;
			case Const.CLOSING:
				System.err.print("Closing\n");
				break;
		}
	}

	public static void printBoard(Board board) {
		System.err.println(board.toString('0'));
	}

	// Checks if the given state is a correct representation of the given board
	public static void checkStateConsistency(State state, Board board) {
		System.err.println("Is state consistent? " + state.equalsBoard(board));
	}

	public static void printMove(Move m) {
		System.err.println("Move: "+m.r1+" "+m.c1+" "+m.r2+" "+m.c2+ " "+m.r3+" "+m.c3);
	}
}
