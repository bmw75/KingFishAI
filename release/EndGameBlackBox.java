import java.util.ArrayList;

/*
 * This black box is used inside our A* search to determine our
 * target row and target column.
 *
 * This class is used to keep a priority queue of the cells in our
 * opponent's home area. This is because the optimal closing move sequence
 * does not fill these cells in order from top to bottom.
 *
 * This blackbox will be used to return which cell in our opponent's home area
 * we should target to fill next depending on its priority, and which cells
 * we are already occupying and which cells our opponent is currently
 * occupying in its home area.
 *
 */

public class EndGameBlackBox {
	ArrayList<Cell> targetCells;
	Cell defaultTargetCell;

	public EndGameBlackBox(int turn) {
		targetCells = new ArrayList<Cell>();
		if (turn == 1) {
			targetCells.add(new Cell(3, 11, 4));
			targetCells.add(new Cell(3, 13, 4));
			targetCells.add(new Cell(3, 9, 4));
			targetCells.add(new Cell(3, 15, 4));
			targetCells.add(new Cell(1, 11, 3));
			targetCells.add(new Cell(1, 13, 3));
			targetCells.add(new Cell(0, 12, 2));
			targetCells.add(new Cell(2, 10, 1));
			targetCells.add(new Cell(2, 12, 1));
			targetCells.add(new Cell(2, 14, 1));
			defaultTargetCell = new Cell(2, 12, 1);
		} else {
			targetCells.add(new Cell(13, 11, 4));
			targetCells.add(new Cell(13, 13, 4));
			targetCells.add(new Cell(13, 9, 4));
			targetCells.add(new Cell(13, 15, 4));
			targetCells.add(new Cell(15, 11, 3));
			targetCells.add(new Cell(15, 13, 3));
			targetCells.add(new Cell(16, 12, 2));
			targetCells.add(new Cell(14, 10, 1));
			targetCells.add(new Cell(14, 12, 1));
			targetCells.add(new Cell(14, 14, 1));
			defaultTargetCell = new Cell(14, 12, 1);
		}
	}

	public int getCellPriority(Cell myCell) {
		for (Cell c : targetCells) {
			if (c.equals(myCell)) {
				return c.getPriority();
			}
		}
		return 0;
	}

	public Cell getNewTargetCell(State currentState) {
		return defaultTargetCell;
		/*
		if (Const.ASTAR_TEST_CELL_PRIORITIES) {
			int[][] board = currentState.reconstructBoardArray();
			// all the cells in targetCells are ordered according to their priority
			// so just return the first one that is not occupied
			for (Cell c : targetCells) {
				if (!c.isOccupied(board)) {
					return c;
				}
			}
			return defaultTargetCell;
		} else {
			return defaultTargetCell;
		}
		*/
	}

	public boolean isTargetCell(Cell c) {
		return targetCells.contains(c);
	}
}
