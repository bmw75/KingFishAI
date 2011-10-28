import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AB_BlackBox {
	public static enum Message{
		MOVE_FOUND,NEED_TO_RECOMPUTE;
		//if move found, store it as 
		private Move move=null;
		public void setMove(Move m){
			if(this==MOVE_FOUND){
				move=m;
			}else{
				System.err.println("Why are you assigning a move if ab search needs to recompute?");
			}
		}
		public Move getMove(){
			if(this==MOVE_FOUND){
				return move;
			}else{
				System.err.println("Why are you querrying for a move? I told you there's none.");
				return null;
			}
		}
	}
	
	int thisPlayer;
	int otherPlayer;
	//hi!!
	public AB_BlackBox(int whichPlayer){
		thisPlayer=whichPlayer;
		otherPlayer=3-thisPlayer;
	}
	//main interaction interface
	//public AB_BlackBox.Message gimmeAMove(GameHistory gh){
	public AB_BlackBox.Message gimmeAMove(Board b,int depth){
		Move best=recompute(b,depth);
		if(best==null){
			return Message.NEED_TO_RECOMPUTE;
		}else{
			Message message=Message.MOVE_FOUND;
			message.setMove(best);
			return message;
		}
	}
	//run alpha beta pruning on minmax tree of specified depth
	public Move recompute(final Board b,int depth){
		if(depth<=0){
			System.err.println("Nonpositive depth for alpha/beta search.");
			return null;
		}
		if(b.getTurn()!=thisPlayer){
			System.err.println("Wrong turn on the board for alpha/beta search.");
			return null;
		}

		//custom version of abMax below
		int depthLeft=depth;
		float alpha=Float.NEGATIVE_INFINITY;
		float beta=Float.POSITIVE_INFINITY;
		
		if(depthLeft==0 || b.checkWin(thisPlayer) || b.checkWin(otherPlayer)){
			//we were given a terminal state!!
			System.err.println("Terminal node given to alpha/beta search.");
			return null;
		}
		float nodeValue=Float.NEGATIVE_INFINITY;
		Move bestMove=null;
		for(Move move : getMoveSet(b)) {
			///go deeper
			b.move(move);
			float childValue=abMin(b,depthLeft-1,alpha,beta);
			b.backwardMove(move);
			///analyze results
			if(bestMove==null || childValue>nodeValue){
				bestMove=move;
			}
			nodeValue=Math.max(nodeValue, childValue);
			//update alpha
			alpha=Math.max(nodeValue, alpha);
		}
		//if still no moves
		if(bestMove==null){
			System.err.println("No moves state given to alpha/beta search");
		}
		return bestMove;
	}
	private float abMax(final Board b, int depthLeft, float alpha, float beta){

		if(depthLeft==0 || b.checkWin(thisPlayer) || b.checkWin(otherPlayer)){
			float terminalValue=utilityOfState(b,thisPlayer);
			return terminalValue;
		}
		float nodeValue=Float.NEGATIVE_INFINITY;
		for(Move move : getMoveSet(b)) {
			///go deeper
			b.move(move);
			float childValue=abMin(b,depthLeft-1,alpha,beta);
			b.backwardMove(move);
			///analyze results
			nodeValue=Math.max(nodeValue, childValue);
			if(nodeValue>=beta)
				return nodeValue;
			alpha=Math.max(nodeValue, alpha);
		}
		return nodeValue;
	}
	private float abMin(final Board b, int depthLeft, float alpha, float beta){
		
		if(depthLeft==0 || b.checkWin(thisPlayer) || b.checkWin(otherPlayer)){
			float terminalValue=utilityOfState(b,thisPlayer);
			return terminalValue;
		}
		float nodeValue=Float.POSITIVE_INFINITY;
		for(Move move : getMoveSet(b)) {
			///go deeper
			b.move(move);
			float childValue=abMax(b,depthLeft-1,alpha,beta);
			b.backwardMove(move);
			///analyze results
			nodeValue=Math.min(nodeValue, childValue);
			if(nodeValue<=alpha)
				return nodeValue;
			beta=Math.min(nodeValue, beta);
		}
		return nodeValue;
	}
	private static int flipTurn(int turn){
		if(turn==1)return 2;
		else return 1;
	}
	
	// We go through all our marbles and get all possible locations we can move to.
	// TODO: take into account moves that involve special pieces
	public static List<Move> getMoveSet(Board board) {
		int turn=board.getTurn();
		List<Move> moveSet = new LinkedList<Move>();
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
	
	//higher utility is better
	public static int utilityOfState(Board board, int turn) {
		int targetR = -1;
		int targetC = -1;
		int oppR = -1;
		int oppC = -1;
		if (turn == 1) {
			targetR = 0;
			targetC = 12;
			oppR = 16;
			oppC = 12;
		} else {
			targetR = 16;
			targetC = 12;
			oppR = 0;
			oppC = 12;
		}
		int otherPlayer=flipTurn(turn);

		int utility = 0;
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				if (board.at(i, j) == turn) {
					utility -= Math.abs(i-targetR);
				}else if(board.at(i,j)==otherPlayer){
					utility += Math.abs(i-oppR);
				}
			}
		}
		return utility;
	}
	
	///hacking below
	////////////////////////////////////
	private static class HashableMove extends Move{
		public HashableMove(Move m){
			super(m.status, m.t1, m.t2, m.r1, m.c1, m.r2, m.c2, m.r3, m.c3);
		}
		public int hashCode(){
			//non-valid moves containing (-1) will hash to zero, others to distinct numbers
			return (r1+1)*(c1+1)*(r2+1)*(c2+1)*(r3+4)*(c3+4);
		}
		public boolean equals(Object o){
			if(o instanceof HashableMove){
				return equals((HashableMove)o);
			}
			return false;
		}
		public boolean equals(HashableMove m){
			//consider only these values as important
			return 
				this.r1 == m.r1 &&
				this.c1 == m.c1 &&
				this.r2 == m.r2 &&
				this.c2 == m.c2 &&
				this.r3 == m.r3 &&
				this.c3 == m.c3;
		}
	}
	private static class maxNode{
		private static class Branch{
			Move move;
			minNode child;
			private Branch(Move m,minNode c){
				move=m;
				child=c;
			}
		}
		List<Branch> branches;
		float value;
		Branch bestMove;
		public maxNode(float v){
			branches=new ArrayList<Branch>(10);
			value=v;
			bestMove=null;
		}
		public void addBranch(Move m,minNode n){
			Branch b=new Branch(m,n);
			if(bestMove==null || n.value>bestMove.child.value){
				bestMove=b;
			}
			branches.add(b);
		}
		public Move getBestMove(){
			return bestMove.move;
		}
	}
	private static class minNode{
		private static class Branch{
			Move move;
			maxNode child;
			public Branch(Move m,maxNode c){
				move=m;
				child=c;
			}
		}
		List<Branch> branches;
		float value;
		public minNode(float v){
			branches=new ArrayList<Branch>(10);
			value=v;
		}
		public void addBranch(Move m,maxNode n){
			branches.add(new Branch(m,n));
		}
	}

}
