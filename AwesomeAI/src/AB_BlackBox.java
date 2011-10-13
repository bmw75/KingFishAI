import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

//import ABMaxTree.ABNode.HashableMove;


public class AB_BlackBox {
	public static enum Message{
		MOVE_FOUND,NEED_TO_RECOMPUTE;
		//if move found, store it as 
		private Move move=null;
		public void setMove(Move m){
			move=m;
		}
		public Move getMove(){
			if(this==MOVE_FOUND){
				return move;
			}else{
				return null;
			}
		}
	}
	
	//contains the precalculated alpha beta tree; the root node is always a max node
	//can update its state if we're given two moves
	//we can get the current max node; this node is either 
		//unexplored
		//explored partially
		//explored completely
	//if the node is explored completely, the black box should be able to choose the optimal move
	//this means that for completely explored nodes, we need a to return a  "list" of moves associated
	//with their alpha values
	//then search for the max alpha value
	
	/*
	ABMaxTree tree;
	ABMaxTree.ABMaxNode lastNodeFound;
	*/
	
	maxNode root;
	
	public AB_BlackBox(Board b,int player,int depth){
		//recompute(b,player,depth);
		//lastNodeFound=null;
		root=recompute(b,player,depth);
	}
	//main interaction interface
	//public AB_BlackBox.Message gimmeAMove(GameHistory gh){
	public AB_BlackBox.Message gimmeAMove(){
		//BoardState should give us the move history and the current board 
		//this method is assumed to be called only on max's turn
		//thus we traverse down the tree to then next max node
		//check if these moves are consistent with the moves that were available from the last 
		//known state
		//if(tree==null){
			//return Message.NEED_TO_RECOMPUTE;
		//}
		/*
		ABMaxTree.ABMaxNode node=tree.update(gh);
		*/
		//lastNodeFound=node;
		//if(node==null || !node.isExplored()){
		if(root==null){
			return Message.NEED_TO_RECOMPUTE;
		}else{
			Message message=Message.MOVE_FOUND;
			message.setMove(root.getBestMove());
					//node.getBestMove());
			return message;
		}
	}
	//run alpha beta pruning on minmax tree of specified depth
	public maxNode recompute(Board b,int player,int depth){
		if(depth<=0 || b.getTurn()!=player){
			//tree=null;
			System.err.println("wrong turn or depth");
			return null;
		}
		//ABMaxTree.ABMaxNode root = alphaBetaMax(b,depth,Float.NEGATIVE_INFINITY,Float.POSITIVE_INFINITY);
		return abMax(b,depth,Float.NEGATIVE_INFINITY,Float.POSITIVE_INFINITY);
	}
	public maxNode abMax(Board b, int depthLeft, float alpha, float beta){
		int myTurn=b.getTurn();
		int theirTurn=flipTurn(myTurn);
		if(depthLeft==1 || b.checkWin(myTurn) || b.checkWin(theirTurn)){
			float terminalValue=utilityOfState(b,b.getTurn());
			return new maxNode(terminalValue);
		}
		maxNode node=new maxNode(Float.NEGATIVE_INFINITY);
		for(Move move : getMoveSet(b)) {
			///go deeper
			b.move(move);
			minNode child=abMin(b,depthLeft-1,alpha,beta);
			b.backwardMove(move);
			node.addBranch(move,child);
			///analyze results
			node.value=Math.max(node.value, child.value);
			if(node.value>=beta)
				return node;
			alpha=Math.max(node.value, alpha);
		}
		return node;
	}
	public minNode abMin(Board b, int depthLeft, float alpha, float beta){
		int myTurn=b.getTurn();
		int theirTurn=flipTurn(myTurn);
		if(depthLeft==1 || b.checkWin(myTurn) || b.checkWin(theirTurn)){
			float terminalValue=utilityOfState(b,b.getTurn());
			return new minNode(terminalValue);
		}
		minNode node=new minNode(Float.POSITIVE_INFINITY);
		for(Move move : getMoveSet(b)) {
			///go deeper
			b.move(move);
			maxNode child=abMax(b,depthLeft-1,alpha,beta);
			b.backwardMove(move);
			node.addBranch(move,child);
			///analyze results
			node.value=Math.min(node.value, child.value);
			if(node.value<=alpha)
				return node;
			beta=Math.min(node.value, beta);
		}
		return node;
	}
	/*
	private ABMaxTree.ABMaxNode alphaBetaMax(Board b,int depthLeft,float alpha,float beta){
		if(depthLeft==1 || b.checkWin(b.getTurn()) || b.checkWin(flipTurn(b.getTurn()))){
			float terminalValue=utilityOfState(b,b.getTurn());
			return new ABMaxTree.ABMaxNode(terminalValue,terminalValue);
		}
		ABMaxTree.ABMaxNode rootHere=new ABMaxTree.ABMaxNode(Float.NEGATIVE_INFINITY,beta);
		for (Move move : getMoveSet(b)) {
			b.move(move);
			ABMaxTree.ABNode child=alphaBetaMin(b,depthLeft-1,alpha,beta);
			b.backwardMove(move);
			rootHere.addBranch(move, child);
			rootHere.setAlpha(Math.max(rootHere.getAlpha(),child.getBeta()));
			if(rootHere.getAlpha()>=beta)
				return rootHere;
			alpha=Math.max(alpha, rootHere.getAlpha());
		}
		rootHere.markAsExplored();
		return rootHere;
	}
	private ABMaxTree.ABNode alphaBetaMin(Board b,int depthLeft,float alpha,float beta){
		ABMaxTree.ABNode rootHere=new ABMaxTree.ABNode();
		if(depthLeft==1 || b.checkWin(b.getTurn()) || b.checkWin(flipTurn(b.getTurn()))){
			float terminalValue=utilityOfState(b,flipTurn(b.getTurn()));
			rootHere.setBeta(terminalValue);
			rootHere.setAlpha(terminalValue);
			return rootHere;
		}
		rootHere.setAlpha(alpha);
		for (Move move : getMoveSet(b)) {
			b.move(move);
			ABMaxTree.ABNode child=alphaBetaMax(b,depthLeft-1,alpha,beta);
			b.backwardMove(move);
			rootHere.addBranch(move, child);
			rootHere.setBeta(Math.min(rootHere.getBeta(),child.getAlpha()));
			if(rootHere.getBeta()<=alpha)
				return rootHere;
			beta=Math.min(beta, rootHere.getAlpha());
		}
		rootHere.markAsExplored();
		return rootHere;
	}
	*/
	private static int flipTurn(int turn){
		if(turn==1)return 2;
		else return 1;
	}
	

	
	// We go through all our marbles and get all possible locations we can move to.
	// TODO: take into account moves that involve special pieces
	public static HashSet<Move> getMoveSet(Board board) {
		int turn=board.getTurn();
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
					utility -= Board.dist(i, j, targetR, targetC);
				}else if(board.at(i,j)==otherPlayer){
					utility += Board.dist(i, j, oppR, oppC);
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
			return (r1+1)*(c1+1)*(r2+1)*(c2+1)*(r3+3)*(c3+3);
		}
		public boolean equals(Object o){
			if(o instanceof HashableMove){
				return equals((HashableMove)o);
			}
			return false;
		}
		public boolean equals(HashableMove m){
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
		Map<HashableMove,minNode> branches;
		float value;
		public maxNode(float v){
			branches=new HashMap<HashableMove,minNode>();
			value=v;
		}
		public void addBranch(Move m,minNode n){
			branches.put(new HashableMove(m),n);
		}
		public Move getBestMove(){
			float bestMin=Float.NEGATIVE_INFINITY;
			Move bestMove=null;
			for(HashableMove hm:branches.keySet()){
				if(branches.get(hm).value>bestMin){
					bestMin=branches.get(hm).value;
					bestMove=hm;
				}
			}
			return bestMove;
		}
	}
	private static class minNode{
		Map<HashableMove,maxNode> branches;
		float value;
		public minNode(float v){
			branches=new HashMap<HashableMove,maxNode>();
			value=v;
		}
		public void addBranch(Move m,maxNode n){
			branches.put(new HashableMove(m),n);
		}
	}

}
