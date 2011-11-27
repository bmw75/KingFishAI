import java.util.HashSet;


public class Evaluate {
	public float horzDistWeight;
	public float vertDistWeight;
	public float stragglerWeight;
	public float chainWeight;
	public float bias;
	
	public Evaluate(){
		horzDistWeight=1; vertDistWeight =1 ; stragglerWeight =1; chainWeight = 1; bias =1;
	}
	
	public Evaluate(float horz, float vert, float strag, float chain, float b){
		horzDistWeight=horz; vertDistWeight =vert ; stragglerWeight =strag; chainWeight = chain; bias =b;
	}
	public float getUtility(Board board, int turn){

		int opp = turn==1 ? 2 : 1;
		
		float vertDistUtil=getVertUtil(board, turn);// / ((float).75*getVertUtil(board, opp));
		float horzDistUtil=getHorzUtil(board, turn);// / ((float).75*getHorzUtil(board, opp));
		float stragglerUtil = 0; //getStragglerUtil(board, turn) / getStragglerUtil(board, opp);
		float chainUtil = getChainUtil(board, turn);// / getChainUtil(board, opp);
		
		//System.err.println("Chain Util = " +chainUtil );
		return (vertDistWeight*vertDistUtil + horzDistWeight*horzDistUtil + stragglerWeight*stragglerUtil + chainWeight*chainUtil + bias);
	}
	
	private float getVertUtil(Board board, int turn){
//		int goalR;
//		
//		if (turn == 1) {
//			goalR = 2;
//		} else {
//			goalR = 14;
//		}
//
//		float utility = 0;
//		for (int i = 0; i < 17; i++) {
//			for (int j = 0; j < 25; j++) {
//				int at=board.at(i, j);
//				//count only pieces belonging to the players
//				if(at==turn){
//					if (turn==1){
//						utility += 17-i;
//					}else{
//						utility += i+1;
//					}
//				}
//			}
//		}
//		return utility;
//	}
//		int goalR;
//		
//		if (turn == 1) {
//			goalR = 0;
//		} else {
//			goalR = 16;
//		}
//		int goalC = 12;
//
//		float utility = 0;
//		for (int i = 0; i < 17; i++) {
//			boolean isInTargetTriangle = Util.isInTargetTriangle(i, turn);
//			for (int j = 0; j < 25; j++) {
//				float utilToAdd=0;
//				float dy =0;
//				float dx=0;
//				int at=board.at(i, j);
//				//count only pieces belonging to the players
//				if(at==turn){
//					if (turn==1){
//						dy= 16-i;
//					}else{
//						dy= i;
//					}
//					if (j <= goalC){
//						dx= goalC-(goalC-j);
//					}else{
//						dx= j-goalC;
//					}
//					if (isInTargetTriangle) {
//						if(dx==0 && dy==0){
//							utilToAdd=90;//TODO
//						}else if(Math.abs(dx)<=2 && dy!=0){
//							utilToAdd=60;
//						}else{
//							utilToAdd=30;
//						}
//					}else{
//						//we're not in the winning corner
//						//prioritize the score to make it sort first by y distance
//						utilToAdd=(Math.abs(dy));//+Math.abs(dx));
//					}
//					utility+=utilToAdd;
//				}
//			}
//		}
//		return utility;
//	}
		int topR, oppTopR;
		int bottomR, oppBottomR;
		int middleR, middleC;
		int oppMiddleR, oppMiddleC;
		
		if (turn == 1) {
			topR = 0;
			bottomR = 3;
			middleR = 2;
			middleC = 12;

			oppTopR = 13;
			oppBottomR = 16;
			oppMiddleR = 14;
			oppMiddleC = 12;
		} else {
			topR = 13;
			bottomR = 16;
			middleR = 14;
			middleC = 12;

			oppTopR = 0;
			oppBottomR = 3;
			oppMiddleR = 2;
			oppMiddleC = 12;
		}

		float utility = 0;
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				int at=board.at(i, j);
				//count only pieces belonging to the players
				if(at==1 || at==2){
					boolean isInTargetTriangle = Util.isInTargetTriangle(i, at);
					int dx,dy;
					if(at==turn){
						dy=(middleR-i);
						dx=(middleC-j);
					}else{
						dy=(oppMiddleR-i);
						dx=(oppMiddleC-j);
					}
					//change utility function based on where we are
					//if we're in the goal space, try to go for the center
					//where score is 0 at the center of triangle
					//1 on the hexagon around
					//and 2 at the corners
					float utilToAdd;
					//are we in the target triangle?
					if (isInTargetTriangle) {
//					if(Math.abs(dy)<=1){
						//yes
						//weigh distance laterally and sideways as well
						//use hexagonal grid distance
						//abs(dx)==2 means lateral pieces
						if(dx==0 && dy==0){
							utilToAdd=0;
						}else if(Math.abs(dx)<=2 && dy!=0){
							utilToAdd=-1;
						}else{
							utilToAdd=-3;
						}
						if(at!=turn){
							utilToAdd=-utilToAdd;
						}
					}else{
						//we're not in the winning corner
						//prioritize the score to make it sort first by y distance
						utilToAdd=(-(Math.abs(dy)*13+Math.abs(dx)));
						if(at!=turn){
							utilToAdd=-utilToAdd;
						}
					}
					//update utility
					utility+=utilToAdd;
				}
			}
		}
		return utility;
	}
	
	private float getHorzUtil(Board board, int turn){
		int goalC = 12;

		float utility = 0;
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				int at=board.at(i, j);
				//count only pieces belonging to the players
				if(at==turn){
					if (j <= goalC){
						utility += goalC-(goalC-j);
					}else{
						utility += j-goalC;
					}
				}
			}
		}
		return utility;
	}
	
	private float getStragglerUtil(Board board, int turn){
		//TODO implement if needed
		return 0;
	}
	
	private float getChainUtil(Board board, int turn){
//		not quite chain util, instead number of possible moves. idea is longer chain -> more jumps -> more possible moves
		HashSet<Integer> moveCount = new HashSet<Integer>();
		float utility = 0;
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				int at=board.at(i, j);
				//count only pieces belonging to the players
				if(at==turn){
					utility+=jump(i,j,board,moveCount);
					//board.legalMoves(i, j, moveCount);
					//utility+=moveCount.size();
				}
			}
		}
		return utility;		
	}

	private int jump(int r, int c, Board board, HashSet<Integer> moves){
		/* Jump moves */
		int count = 0;
		int[][] jumps = new int[][] {{r,c-2,r,c-4},{r-1,c-1,r-2,c-2},{r-1,c+1,r-2,c+2},{r,c+2,r,c+4},{r+1,c+1,r+2,c+2},{r+1,c-1,r+2,c-2}};
		for(int i=0; i<jumps.length; i++){
			int r1=jumps[i][0];
			int c1=jumps[i][1];
			int r2=jumps[i][2];
			int c2=jumps[i][3];
			if(0<=r2 && r2<17 && 0<=c2 && c2<25 && board.at(r1,c1)>0 && board.at(r2,c2)==0 && !moves.contains(25*r2+c2)){
				moves.add(25*r2+c2);
				count++;
				count+=jump(r2,c2,board,moves);
			}
		}
		return count;
	}
}
