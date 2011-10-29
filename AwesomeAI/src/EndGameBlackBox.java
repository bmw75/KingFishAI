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
	int currentTarget;

	public EndGameBlackBox(int turn) {
		currentTarget= 0;
		targetCells = new ArrayList<Cell>();
		if (turn == 1) {
			targetCells.add(new Cell(13, 9));
			targetCells.add(new Cell(13, 11));
			targetCells.add(new Cell(13, 13));
			targetCells.add(new Cell(13, 15));
			targetCells.add(new Cell(14, 10));
			targetCells.add(new Cell(14, 12));
			targetCells.add(new Cell(14, 14));
			targetCells.add(new Cell(15, 11));
			targetCells.add(new Cell(15, 13));
			targetCells.add(new Cell(16, 12));
		} else {
			targetCells.add(new Cell(3, 9));
			targetCells.add(new Cell(3, 11));
			targetCells.add(new Cell(3, 13));
			targetCells.add(new Cell(3, 15));
			targetCells.add(new Cell(2, 10));
			targetCells.add(new Cell(2, 12));
			targetCells.add(new Cell(2, 14));
			targetCells.add(new Cell(1, 11));
			targetCells.add(new Cell(1, 13));
			targetCells.add(new Cell(0, 12));
		}
	}

	class Cell {
		private int r, c;
		private boolean occupied;

		public Cell(int row, int col) {
			r = row;
			c = col;
			occupied = false;
		}

		public int getRow() { return r; }
		public int getCol() { return c; }

		public void setRow(int row) {
			r = row;
		}
		public void setCol(int col) {
			c = col;
		}
	}
}

