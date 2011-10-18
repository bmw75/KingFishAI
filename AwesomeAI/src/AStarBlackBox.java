import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AStarBlackBox {
	int statesVisited = 0;
	int turn, targetR, targetC, homeR, homeC, numMoveCutoff;
	ArrayList<Move> winningMoves;

	private static final int homexy[][][] = new int[][][] {
		/* Top home */
		{ {16, 12},
			{15, 11}, {15, 13},
			{14, 10}, {14, 12}, {14, 14},
			{13, 9},  {13, 11}, {13, 13}, {13, 15} },
			/* Bottom home */
			{ {0, 12},
				{1, 11}, {1, 13},
				{2, 10}, {2, 12}, {2, 14},
				{3, 9},  {3, 11}, {3, 13}, {3, 15} },
	};

	public AStarBlackBox(int playerTurn) {
		winningMoves = new ArrayList<Move>();

		turn = playerTurn;
		targetR = turn == 1 ? 0 : 16;
		targetC = 12;

		homeR = turn == 1 ? 16 : 0;
		homeC = 12;
	}

	public void setMoveCutoff(int numMoveLimit) {
		numMoveCutoff = numMoveLimit;
	}

	/*
	 * Runs AStar search and returns a list of all moves
	 * that must be taken to win.
	 * Code based from the A* search given in wikipedia:
	 * en.wikipedia.org/wiki/A*_search_algorithm
	 */
	public ArrayList<Move> aStarSearch(State start) {
		winningMoves.clear();
		statesVisited = 0;
		HashSet<StateNode> hashedNodes = new HashSet<StateNode>(); // Nodes we've already checked
		ArrayList<StateNode> nodesToCheck = new ArrayList<StateNode>();
		StateNode startNode = new StateNode(start, getCostFromStart(start), getHeuristicCost(start));
		nodesToCheck.add(startNode);

		while (!nodesToCheck.isEmpty()) {
			StateNode chosenNode = getNodeWithLowestFScore(nodesToCheck);

			/*
			System.err.println("A* Chosen Node:");
		 	System.err.println("H cost: "	+ chosenNode.getH());
		 	System.err.println("G cost: "	+ chosenNode.getG());
		 	System.err.println("F cost: "	+ chosenNode.getF());
		 	System.err.println("Depth: "	+ chosenNode.getDepth());
		 	System.err.println("States visited: "	+ statesVisited++);
			Util.printState(chosenNode.getState());
			*/

			if(goalTest(chosenNode.getState()) || chosenNode.getDepth() >= numMoveCutoff){
				// reconstruct the winning moves
				setWinningMoveList(chosenNode.getState(), start); 
				return winningMoves;
			}

			nodesToCheck.remove(chosenNode);
			hashedNodes.add(chosenNode);

			HashSet<StateNode> neighborNodes = getNeighborNodes(chosenNode);

			for (StateNode neighbor : neighborNodes) {
				if (!hashedNodes.contains(neighbor)) {
					int newGScore = getCostFromStart(neighbor.getState());
					boolean useNewG = false;

					if (!nodesToCheck.contains(neighbor)) {
						nodesToCheck.add(neighbor);
						useNewG = true;
					} else if (newGScore < neighbor.getG()) {
						useNewG = true;
					}

					if (useNewG) {
						neighbor.setG(newGScore);
						neighbor.setH(getHeuristicCost(neighbor.getState()));
						neighbor.calculateAndSetF();
					}
				}
			}
		}
		return null;
	}

	// backtrack all the way to the start state to get all moves
	public void setWinningMoveList(State s, State start) {
		if (s.getParent().equals(start)) {
			winningMoves.add(s.getMove());
		} else {
			setWinningMoveList(s.getParent(), start);
			winningMoves.add(s.getMove());
		}
	}

	public StateNode getNodeWithLowestFScore(ArrayList<StateNode> nodes) {
		int minF = Integer.MAX_VALUE;
		StateNode minNode = nodes.get(0);

		for (StateNode n : nodes) {
			if (n.getF() < minF) {
				minF = n.getF();
				minNode = n;
			}
		}
		return minNode;
	}

	public HashSet<StateNode> getNeighborNodes(StateNode node) {
		Board board = node.getState().reconstructBoard();
		HashSet<StateNode> neighborNodes = new HashSet<StateNode>();

		// go through all our marbles and get all possible locations we can move to.
		// for each of these possible moves add its StateNode to the HashSet
		// TODO: take into account moves that involve special pieces
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				if (board.at(i, j) == turn) {
					HashSet<Integer> destinations = new HashSet<Integer>();
					board.legalMoves(i, j, destinations);
					for (Integer dest : destinations) {
						int r = dest / 25;
						int c = dest % 25;
						Move m = new Move(0, 0, 0, i, j, r, c, -1, -1);
						State neighborState = new State(m, node.getState());
						StateNode neighborNode = new StateNode(neighborState);
						neighborNode.setDepth(node.getDepth() + 1);
						neighborNodes.add(neighborNode);
					}
				}
			}
		}
		return neighborNodes;
	}

	public boolean goalTest(State state) {
		Board board = state.reconstructBoard();

		// this is essentially a copy of what is in Board.java
		// but it is helpful to have it here in case we want to modify something
		// for debugging purposes

		int marbles[] = new int[2];
		//Check opponent's home
		for(int j=0; j<10; j++){
			int x = homexy[2-turn][j][0];
			int y = homexy[2-turn][j][1];
			if (board.at(x, y) != 0){
				marbles[board.at(x, y)-1] += 1;
			}
		}
		//If all places in the opponent's home are occupied and
		//the marbles of the opponent are less or equal
		//to the marbles of the player then it's gameover.
		if (marbles[0]+marbles[1]==10 && marbles[turn-1] >= marbles[2-turn])
			return true;
		else 
			return false;
	}

	public int getHeuristicCost(State s) {
		Board board = s.reconstructBoard();
		// go through all marbles and add their distances to target area
		int sumDistance = 0;
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				if (board.at(i, j) == turn) {
					sumDistance += Board.dist(i, j, targetR, targetC);
				}
			}
		}
		return sumDistance;
	}

	public int getCostFromStart(State s) {
		Board board = s.reconstructBoard();
		// go through all marbles and add their distances to home area
		int sumDistance = 0;
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				if (board.at(i, j) == turn) {
					sumDistance += Board.dist(i, j, homeR, homeC);
				}
			}
		}
//		return sumDistance;
		return 0;
	}
}

class StateNode {
	private State s;
	private int g, h, f, depth;

	public StateNode(State state) {
		s = state;
		g = 0;
		h = 0;
		f = 0;
		depth = 0;
	}

	public StateNode(State state, int gScore, int hScore) {
		s = state;
		g = gScore;
		h = hScore;
		f = g + h;
		depth = 0;
	}

	public State getState() { return s; }

	public int getG() { return g; }
	public int getH() { return h; }
	public int getF() { return f; }
	public int getDepth() { return depth; }

	public void setG(int gScore) { g = gScore; }
	public void setH(int hScore) { h = hScore; }
	public void setF(int fScore) { f = fScore; }
	public void setDepth(int d) { depth = d; }

	public void calculateAndSetF() { f = g + h; }

	@Override
	public int hashCode() {
		return s.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		StateNode otherStateNode = (StateNode) o;
		return s.equals(otherStateNode.getState());
	}
}
