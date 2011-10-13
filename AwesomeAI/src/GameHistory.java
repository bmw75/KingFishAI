
public class GameHistory {
	java.util.List<Move> moveHistory = new java.util.ArrayList<Move>();
	
	//TODO: make a getter for the board; copy or pointer??
	
	public Move getNthLastMove(int n){
		return getNthMove(moveHistory.size()-(n-1));
	}
	public Move getNthMove(int n){
		if(n<=0 || n>moveHistory.size()){
			//TODO: throw error
			return null;
		}else{
			return moveHistory.get(n-1);
		}
	}
	public void addMove(Move m){
		moveHistory.add(m);
	}
}
