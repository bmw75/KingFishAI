import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AStarBlackBox {
	boolean useClosingHeuristic = false;

	EndGameBlackBox endGameBlackBox; // used to determine our target row/col
	Cell targetCell;
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
		endGameBlackBox = new EndGameBlackBox(playerTurn);
		winningMoves = new ArrayList<Move>();
		turn = playerTurn;

		targetR = turn == 1 ? 0 : 16;
		targetC = 12;
    targetCell = new Cell(targetR, targetC);
		homeR = turn == 1 ? 16 : 0;
		homeC = 12;
	}

	public void useClosingHeuristic(boolean b) {
		useClosingHeuristic = b;
	}

	public void setMoveCutoff(int numMoveLimit) {
		numMoveCutoff = numMoveLimit;
	}

	public int getMoveCutoff() {
		return numMoveCutoff;
	}

	/*
	 * Runs AStar search and returns a list of all moves
	 * that must be taken to win.
	 * Code based from the A* search given in wikipedia:
	 * en.wikipedia.org/wiki/A*_search_algorithm
	 */
	public ArrayList<Move> aStarSearch(State start) {
		if (useClosingHeuristic && Const.USE_ASTAR_CLOSING_HEURISTIC) {
			System.err.println("Using closing heuristic...");
		}
		System.err.println("Using Astrizzles...");

		winningMoves.clear();
		statesVisited = 0;
		HashSet<StateNode> hashedNodes = new HashSet<StateNode>(); // Nodes we've already checked
		ArrayList<StateNode> nodesToCheck = new ArrayList<StateNode>();
		StateNode startNode = new StateNode(start, 0, getHeuristicCost(start));
		nodesToCheck.add(startNode);

		while (!nodesToCheck.isEmpty()) {
			statesVisited++;
			StateNode chosenNode;
			chosenNode = getNodeWithLowestFScore(nodesToCheck);
			targetCell = endGameBlackBox.getNewTargetCell(chosenNode.getState());
      /*
			targetR = targetCell.getRow();
			targetC = targetCell.getCol();
      */
			/*
			System.err.println("A* Chosen Node:");
		 	System.err.println("H cost: "	+ chosenNode.getH());
		 	System.err.println("G cost: "	+ chosenNode.getG());
		 	System.err.println("Depth: "	+ chosenNode.getDepth());
		 	System.err.println("States visited: "	+ statesVisited);
			Util.printState(chosenNode.getState());
		 	System.err.println("F cost: "	+ chosenNode.getF());
			*/
			/*
		 	System.err.println("F cost: "	+ chosenNode.getF());
		 	System.err.println("States visited: "	+ statesVisited);
			*/

			if(goalTest(chosenNode.getState()) || chosenNode.getDepth() >= numMoveCutoff){
				// reconstruct the winning moves
				System.err.println("Returning " + chosenNode.getDepth() + " moves.");
		 	  System.err.println("States visited: "	+ statesVisited);
				setWinningMoveList(chosenNode.getState(), start); 
				return winningMoves;
			}

			nodesToCheck.remove(chosenNode);
			hashedNodes.add(chosenNode);

			HashSet<StateNode> neighborNodes = getNeighborNodes(chosenNode);

			for (StateNode neighbor : neighborNodes) {
				if (!hashedNodes.contains(neighbor)) {
					// g (cost from goal) will simply be a count of number of moves
					int newGScore = chosenNode.getG() + 1;
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
		System.err.println("Null at aStar search. This should not happen.");
		return null;
	}

	// backtrack all the way to the start state to get all moves
	public void setWinningMoveList(State s, State start) {
		if (s.getParent().equals(start)) {
			System.err.println("Number of states visited: " + statesVisited);
			statesVisited = 0;
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

	// given a node, get all possible new stateNodes we can move to
	public HashSet<StateNode> getNeighborNodes(StateNode node) {
		int[][] board = node.getState().reconstructBoardArray();
		HashSet<StateNode> neighborNodes = new HashSet<StateNode>();

		// go through all our marbles and get all possible locations we can move to.
		// for each of these possible moves add its StateNode to the HashSet
		// TODO: take into account moves that involve special pieces
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 25; j++) {
				if (board[i][j] == turn) {
					HashSet<Integer> destinations = new HashSet<Integer>();
					Util.getBoardLegalMoves(i, j, destinations, board);
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
		int[][] board = state.reconstructBoardArray();

		// this is essentially a copy of what is in Board.java
		// but it is helpful to have it here in case we want to modify something
		// for debugging purposes

		int marbles[] = new int[2];
		//Check opponent's home
		for(int j=0; j<10; j++){
			int x = homexy[2-turn][j][0];
			int y = homexy[2-turn][j][1];
			if (board[x][y] != 0){
				marbles[board[x][y]-1] += 1;
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

	// Heuristic cost: go through all marbles, add their distances to target area
	public int getHeuristicCost(State s) {
		int[][] board = s.reconstructBoardArray();
		int sumDistance = 0;

		if (useClosingHeuristic && Const.USE_ASTAR_CLOSING_HEURISTIC) {
			// we want an optimistic search.
			// assume you can take all the jumps possible.
			for (int i = 0; i < 17; i++) {
				for (int j = 0; j < 25; j++) {
					if (board[i][j] == turn) {
						// loop through the board again
						// count how many pieces are at our level or ahead of us
						// Optimistically, we'd jump over all of them.
						int jumpablePieces = 0;
						if (turn == 1) {
							for (int y = 0; y <= i; y++) {
								for (int x = 0; x < 25; x++) {
									if ((board[y][x] == 1 || board[y][x] == 2) && !(y==i && x==j)) {
										jumpablePieces++;
									}
								}
							}
						} else {
							for (int y = i; y < 17; y++) {
								for (int x = 0; x < 25; x++) {
									if ((board[y][x] == 1 || board[y][x] == 2) && !(y==i && x== j)) {
										jumpablePieces++;
									}
								}
							}
						}

						sumDistance += Math.max(1, 1+Util.dist(i, j, targetR, targetC) - jumpablePieces*2);
					}
				}
			}
		} else {

			int targetTopR, targetBottomR;
			if (turn == 1) {
				targetTopR = 0;
				targetBottomR = 3;
			} else {
				targetTopR = 13;
				targetBottomR = 16;
			}

			for (int i = 0; i < 17; i++) {
				for (int j = 0; j < 25; j++) {
					if (board[i][j] == turn) {
						int dx = targetCell.getCol() - j;
						int dy = targetCell.getRow() - i;
						boolean isInTargetTriangle = (i <= targetBottomR && i >= targetTopR);
						if (isInTargetTriangle) {
							sumDistance += Util.dist(i, j, targetR, targetC);
							/*
								 if ((turn == 1 && i == 3) || (turn == 2 && i == 13)) {
							// get the 4-cell row first
							sumDistance -= 3;
							} else if (Math.abs(dx) <= 3 && Math.abs(dy) == 1) {
							// get the 4-cell and 2-cell rows first
							sumDistance -= 2;
							} else if (Math.abs(dx) <= 2 && dy != 0) {
							// aim for the other cells, excluding center row
							sumDistance -= 1;
							}
							*/
						} else {
							sumDistance += Util.euclideanDistSq(i, j, targetR, targetC);
						}

						/*
							 if (dx == 0 && dy == 0) {
						// get to the center row quickly
						sumDistance -= 3;
						} else if (Math.abs(dx) <= 2 && dy != 0) {
						// get the surrounding hexagon quickly
						sumDistance -= 1;
						}
						*/
					}
				}
			}
		}
		return sumDistance;
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
