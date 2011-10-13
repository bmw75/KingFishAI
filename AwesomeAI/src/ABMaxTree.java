import java.util.*;
public class ABMaxTree {
	//ABNode contains alpha beta values and a hashmap of moves to the next nodes in the tree
	public static class ABNode{
		private static class ABPair{
			float alpha=Float.NEGATIVE_INFINITY,beta=Float.POSITIVE_INFINITY;
			public ABPair(){}
			public ABPair(float a,float b){
				alpha=a;
				beta=b;
			}
		}
		//HashableMove overrides equals and hashCode, ignoring move status and times
		//as a meaningful variables
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
		//variables
		private boolean fullyExplored;
		private Map<HashableMove,ABNode> branches;
		private ABPair ab;
		//methods
		public ABNode(){
			fullyExplored=false;
			branches=new HashMap<HashableMove,ABNode>();
			ab=new ABPair();
		}
		public ABNode(float alpha,float beta){
			this();
			ab=new ABPair(alpha,beta);
		}
		public boolean isExplored(){
			return fullyExplored;
		}
		public void markAsExplored(){
			fullyExplored=true;
		}
		public void setAlpha(float a){
			ab.alpha=a;
		}
		public float getAlpha(){
			return ab.alpha;
		}
		public void setBeta(float b){
			ab.beta=b;
		}
		public float getBeta(){
			return ab.beta;
		}
		public void addBranch(Move m,ABNode n){
			branches.put(new HashableMove(m),n);
		}
		public boolean hasBranch(Move m){
			return branches.containsKey(new HashableMove(m));
		}
		public ABNode getBranch(Move m){
			return branches.get(new HashableMove(m));
		}
	}
	//this class is the one actually returned
	//and it automatically keeps track of the best alpha value move seen yet
	//can return best move, which may be a null value if it has not seen any moves yet or any good moves
	public static class ABMaxNode extends ABNode{
		private Move bestMove=null;
		public ABMaxNode(){
			super();
		}
		public ABMaxNode(float alpha,float beta){
			super(alpha,beta);
		}
		@Override
		public void addBranch(Move m,ABNode n){
			//keep track of what the best move so far is
			//since the next node is a min one,
			//it will go with the beta choice of path
			//so we are looking for the min node with the highest beta value
			if(bestMove==null || n.getBeta()>getBranch(bestMove).getBeta()){
				bestMove=m;
			}
			super.addBranch(m,n);
		}
		public ABMaxNode traverseDown(Move a,Move b){
			if(hasBranch(a)){
				ABNode n1=getBranch(a);
				if(n1.hasBranch(b)){
					ABNode n2=n1.getBranch(b);
					if(n2 instanceof ABMaxNode){
						return ((ABMaxNode)n2);
					}
				}
			}
			return null;
		}
		public Move getBestMove(){
			return bestMove;
		}
	}
	//TODO: make an ABMinNode to enforce consistency
	//now for the other stuff
	private ABMaxNode root;
	//must initialize tree with a constructed root node
	public ABMaxTree(ABMaxNode r){
		root=r;
	}
	public ABMaxNode update(GameHistory gh){
		//don't update for now, just return the root
		return root;
		/*
		Move a=gh.getNthLastMove(2);
		Move b=gh.getNthLastMove(1);
		if(a!=null && b!=null){
			root=root.traverseDown(a, b);
			return root;
			//TODO: check to see if the expected board state matches the actual ones
		}else{
			return root;
		}
		//if anything fails
		//return null;*/
	}
}
