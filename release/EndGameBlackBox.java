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

	public EndGameBlackBox() {
		targetCells = new ArrayList<Cell>();

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

