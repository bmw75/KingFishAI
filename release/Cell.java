
/*
 * All cells are specified by a row, column and a priority.
 * The priority refers to how important it is to occupy this cell.
 * They are set to lowest priority if unspecified.
 * (e.g. in EndGameBlackBox we spcifically designate priorities to the
 * target area cells because we want them to be occupied in different order).
 */
public class Cell {
	private int r, c, p;

	public Cell(int row, int col) {
		r = row;
		c = col;
		p = 0;
	}

	public Cell(int row, int col, int priority) {
		r = row;
		c = col;
		p = priority;
	}

	public int getRow() { return r; }
	public int getCol() { return c; }
	public int getPriority() { return p; }

	public boolean isOccupied(int[][] board) {
		return board[r][c] != 0;
	}

	public void setRow(int row) {
		r = row;
	}

	public void setCol(int col) {
		c = col;
	}

	public void setPriority(int priority) {
		p = priority;
	}

	@Override
	public boolean equals(Object o) {
		Cell otherCell = (Cell) o;
		return otherCell.getRow() == r && otherCell.getCol() == c;
	}

	@Override
	public String toString() {
		return "[" + r + ", " + c + "]";
	}
}
