
/*
 * For creating draughts square objects in the ServerModel class.
 *
 */

class DraughtsSquare {
	private int column = 0, row = 0;
	private DraughtsPiece piece;

	public DraughtsSquare(int x, int y) {
		column = x;
		row = y;
		piece = null;// As no pieces when board created
	}

	int getColumn() {
		return (column);
	}

	DraughtsPiece getDraughtsPiece() {
		return piece;
	}

	int getRow() {
		return (row);
	}

	void setColumn(int intColumn) {
		column = intColumn;
	}

	void setDraughtsPiece(DraughtsPiece p) {
		piece = p;
	}

	void setRow(int intRow) {
		row = intRow;
	}

}