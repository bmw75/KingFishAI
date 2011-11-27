import java.util.HashSet;


public class Special_BlackBox {
	public float horzDistWeight;
	public float vertDistWeight;
	public float stragglerWeight;
	public float chainWeight;
	public float bias;
	
	public Special_BlackBox(){
		horzDistWeight=1; vertDistWeight =1 ; stragglerWeight =1; chainWeight = 1; bias =1;
	}
	
	public Special_BlackBox(float horz, float vert, float strag, float chain, float b){
		horzDistWeight=horz; vertDistWeight =vert ; stragglerWeight =strag; chainWeight = chain; bias =b;
	}
	
	public float[] getSpecial(Board board, int turn){
		//Board board = new Board(board2.toString('0'));
		//float bestUtil=-Float.MAX_VALUE;
		float bestUtil=0;
		float[] bestMove = {-1,-1};
		Evaluate e = new Evaluate(horzDistWeight, vertDistWeight, stragglerWeight, chainWeight, bias);
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				if (board.at(i,j) == 0 && !board.isHome(i,j)) {
					//System.err.println("WE CHECKIN " + i +" , " + j);
					board.setSpecialMarble(turn, i, j);
					float currUtil = e.getUtility(board, turn);
					if (Math.abs(currUtil)>Math.abs(bestUtil)){ //TODO abs may take worse of two if meant to be positive and best is slight negative
						bestUtil=currUtil;
						bestMove[0]=i;
						bestMove[1]=j;
					}
					board.removeSpecialMarble(turn, i, j);
				}
			}
		}
		float[] ans = {bestUtil,bestMove[0],bestMove[1]};
		return ans;
	}
}
