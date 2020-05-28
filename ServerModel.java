
import java.io.IOException;

/* This class deals with the game logic. It receives messages from the clients via the Controller
 * and sends messages to the clients via the Controller.
 *
 * In the methods below:
 *
 * The board columns and row goes 0 to 7 horizontally and vertically (with [0][0] being the top left
 * square, from the perspective of someone looking up the board from bottom to top.
 *
 * 'Left' and 'right' below are from the perspective of someone looking up the board from
 * bottom to top.'Forwards' and 'backwards' are from the perspective in which the player's men
 * are playing, i.e. bottom to top in white's case, and vice versa for red.
 *
 * The top left corner is row 0, column 0.
*/

public class ServerModel {
	DraughtsSquare square[][] = new DraughtsSquare[8][8];
	boolean isRedPlayerTurn, firstPartOfMoveSelected, secondPartOfMoveSelected,
			movedThisTurn, redWon, whiteWon, gameInProgress,
			modelAlreadyUpdatedWithMove;
	int mandatoryMovePieceColumn, mandatoryMovePieceRow, startingCol,
			startingRow, destCol, destRow;

	private boolean bothClientsConnected;
	private boolean firstMoveNonJump;

	public ServerModel() throws IOException {
		initialSetup();
	}

	void addInitialMenToBoard() {
		// red men
		for (int row = 0; row < 3; row++) {
			for (int column = 1; column < 8; column += 2) {
				if ((row == 0) || (row == 2)) {
					square[column][row]
							.setDraughtsPiece(new DraughtsPiece("man", "red"));
				}
			}
			square[0][1].setDraughtsPiece(new DraughtsPiece("man", "red"));
			square[2][1].setDraughtsPiece(new DraughtsPiece("man", "red"));
			square[4][1].setDraughtsPiece(new DraughtsPiece("man", "red"));
			square[6][1].setDraughtsPiece(new DraughtsPiece("man", "red"));
		}
		// white men
		for (int row = 5; row < 8; row++) {
			for (int column = 0; column < 8; column += 2) {
				if ((row == 5) || (row == 7)) {
					square[column][row].setDraughtsPiece(
							new DraughtsPiece("man", "white"));
				}
			}
			square[1][6].setDraughtsPiece(new DraughtsPiece("man", "white"));
			square[3][6].setDraughtsPiece(new DraughtsPiece("man", "white"));
			square[5][6].setDraughtsPiece(new DraughtsPiece("man", "white"));
			square[7][6].setDraughtsPiece(new DraughtsPiece("man", "white"));

		}
	}

	// See notes at top of class notes for definitions of left, right, forward
	// and backward.

	boolean isLegalNonJumpMovePossible() {
		if (isRedPlayerTurn) {
			for (int row = 0; row < 8; row++) {
				for (int column = 0; column < 8; column++) {
					// If square null, move to next square.
					if (square[column][row].getDraughtsPiece() == null) {
						continue;
					}
					// If a white piece, move to next square.
					if (square[column][row].getDraughtsPiece()
							.getPieceColour() == "white") {
						continue;
					}
					// Can move forward right?
					if ((square[column][row].getDraughtsPiece()
							.getPieceColour() == "red") && (column <= 6)
							&& (row <= 6) && (square[column + 1][row + 1]
									.getDraughtsPiece() == null)) {
						return true;
					}
					// Can move forward left?
					if ((square[column][row].getDraughtsPiece()
							.getPieceColour() == "red") && (column >= 1)
							&& (row <= 6) && (square[column - 1][row + 1]
									.getDraughtsPiece() == null)) {
						return true;
					}
					// Can king move backward right?
					if ((square[column][row].getDraughtsPiece()
							.getPieceColour() == "red")
							&& (square[column][row].getDraughtsPiece()
									.getType() == "king")
							&& (column <= 6) && (row >= 1)
							&& (square[column + 1][row - 1]
									.getDraughtsPiece() == null)) {
						return true;
					}
					// Can king move backward left?
					if ((square[column][row].getDraughtsPiece()
							.getPieceColour() == "red")
							&& (square[column][row].getDraughtsPiece()
									.getType() == "king")
							&& (row >= 1) && (column >= 1)
							&& (square[column - 1][row - 1]
									.getDraughtsPiece() == null)) {
						return true;
					}

				}
			}
		} else {
			for (int row = 0; row < 8; row++) {
				for (int column = 0; column < 8; column++) {
					// If square null, move to next square.
					if (square[column][row].getDraughtsPiece() == null) {
						continue;
					}
					// If a white piece, move to next square.
					if (square[column][row].getDraughtsPiece()
							.getPieceColour() == "red") {
						continue;
					}
					// Can move forward right?
					if ((square[column][row].getDraughtsPiece()
							.getPieceColour() == "white") && (column <= 6)
							&& (row >= 1) && (square[column + 1][row - 1]
									.getDraughtsPiece() == null)) {
						return true;
					}
					// Can move forward left?
					if ((square[column][row].getDraughtsPiece()
							.getPieceColour() == "white") && (column >= 1)
							&& (row >= 1) && (square[column - 1][row - 1]
									.getDraughtsPiece() == null)) {
						return true;
					}
					// Can king move backward right?
					if ((square[column][row].getDraughtsPiece()
							.getPieceColour() == "white")
							&& (square[column][row].getDraughtsPiece()
									.getType() == "king")
							&& (column <= 6) && (row <= 6)
							&& (square[column + 1][row + 1]
									.getDraughtsPiece() == null)) {
						return true;
					}
					// Can king move backward left?
					if ((square[column][row].getDraughtsPiece()
							.getPieceColour() == "white")
							&& (square[column][row].getDraughtsPiece()
									.getType() == "king")
							&& (column >= 1) && (row <= 6)
							&& (square[column - 1][row + 1]
									.getDraughtsPiece() == null)) {
						return true;
					}

				}
			}
		}
		return false;
	}

	// Can the player whose turn it is jump?
	boolean canJump() {
		// if red's turn:
		if (isRedPlayerTurn) {
			for (int row = 0; row < 8; row++) {
				for (int column = 0; column < 8; column++) {
					// If square null, move to next square.
					if (square[column][row].getDraughtsPiece() == null) {
						continue;
					}
					// If a white piece, move to next square.
					if (square[column][row].getDraughtsPiece()
							.getPieceColour() == "white") {
						continue;
					}
					// Can red jump forwards to left?
					if ((column >= 2) && (row <= 5)
							&& (square[column - 1][row + 1]
									.getDraughtsPiece() != null)) {
						if (canRedJumpForwardsToLeft(row, column)) {
							return canRedJumpForwardsToLeft(row, column);
						}
					}
					// Can red jump forwards to right?
					if ((column <= 5) && (row <= 5)
							&& (square[column + 1][row + 1]
									.getDraughtsPiece() != null)) {
						if (canRedJumpForwardsToRight(row, column)) {
							return canRedJumpForwardsToRight(row, column);
						}
					}
					// Can red king jump backwards to left?
					if ((column >= 2) && (row >= 2)
							&& (square[column - 1][row - 1]
									.getDraughtsPiece() != null)) {
						if (canRedKingJumpBackwardsToLeft(row, column)) {
							return canRedKingJumpBackwardsToLeft(row, column);
						}
					}
					// Can red king jump backwards to right?
					if ((column <= 5) && (row >= 2)
							&& (square[column + 1][row - 1]
									.getDraughtsPiece() != null)) {
						if (canRedKingJumpBackwardsToRight(row, column)) {
							return canRedKingJumpBackwardsToRight(row, column);
						}
					}
				}
			}
			// if white to move
		} else {
			for (int row = 0; row < 8; row++) {
				for (int column = 0; column < 8; column++) {
					// If square is null, move to next square.
					if (square[column][row].getDraughtsPiece() == null) {
						continue;
					}
					// If a red piece on the square, move to next square.
					if (square[column][row].getDraughtsPiece()
							.getPieceColour() == "red") {
						continue;
					}
					// Can white jump forward to left?
					if ((column >= 2) && (row >= 2)
							&& (square[column - 1][row - 1]
									.getDraughtsPiece() != null)) {
						if (canWhiteJumpForwardToLeft(row, column)) {
							return canWhiteJumpForwardToLeft(row, column);
						}
					}
					// Can white jump forward to right?
					if ((column <= 5) && (row >= 2)
							&& (square[column + 1][row - 1]
									.getDraughtsPiece() != null)) {
						if (canWhiteJumpForwardsToRight(row, column)) {
							return canWhiteJumpForwardsToRight(row, column);
						}
					}
					// Can white king jump backwards to left?
					if ((column >= 2) && (row <= 5)
							&& (square[column - 1][row + 1]
									.getDraughtsPiece() != null)) {
						if (canWhiteKingJumpBackwardsToLeft(row, column)) {
							return canWhiteKingJumpBackwardsToLeft(row, column);
						}
					}
					// Can white king jump backwards to right?
					if ((column <= 5) && (row <= 5)
							&& (square[column + 1][row + 1]
									.getDraughtsPiece() != null)) {
						if (canWhiteKingTakeBackwardsToRight(row, column)) {
							return canWhiteKingTakeBackwardsToRight(row,
									column);
						}
					}
				}
			}
		}

		return false;
	}

	public boolean canRedJumpForwardsToLeft(int row, int column) {
		if ((square[column][row].getDraughtsPiece().getPieceColour() == "red")
				&& (square[column - 1][row + 1].getDraughtsPiece()
						.getPieceColour() == "white")
				&& (square[column - 2][row + 2].getDraughtsPiece() == null)) {
			mandatoryMovePieceColumn = column;
			mandatoryMovePieceRow = row;
			return true;
		}
		return false;
	}

	public boolean canRedJumpForwardsToRight(int row, int column) {
		if ((square[column][row].getDraughtsPiece().getPieceColour() == "red")
				&& (square[column + 1][row + 1].getDraughtsPiece()
						.getPieceColour() == "white")
				&& (square[column + 2][row + 2].getDraughtsPiece() == null)) {
			mandatoryMovePieceColumn = column;
			mandatoryMovePieceRow = row;
			return true;
		}
		return false;
	}

	public boolean canRedKingJumpBackwardsToLeft(int row, int column) {
		if ((square[column][row].getDraughtsPiece().getPieceColour() == "red")
				&& (square[column][row].getDraughtsPiece().getType() == "king")
				&& (square[column - 1][row - 1].getDraughtsPiece()
						.getPieceColour() == "white")
				&& (square[column - 2][row - 2].getDraughtsPiece() == null)) {
			mandatoryMovePieceColumn = column;
			mandatoryMovePieceRow = row;
			return true;
		}
		return false;
	}

	public boolean canRedKingJumpBackwardsToRight(int row, int column) {
		if ((square[column][row].getDraughtsPiece().getPieceColour() == "red")
				&& (square[column][row].getDraughtsPiece().getType() == "king")
				&& (square[column + 1][row - 1].getDraughtsPiece()
						.getPieceColour() == "white")
				&& (square[column + 2][row - 2].getDraughtsPiece() == null)) {
			mandatoryMovePieceColumn = column;
			mandatoryMovePieceRow = row;
			return true;
		}
		return false;
	}

	public boolean canWhiteJumpForwardsToRight(int row, int column) {
		if ((square[column][row].getDraughtsPiece().getPieceColour() == "white")
				&& (square[column + 1][row - 1].getDraughtsPiece()
						.getPieceColour() == "red")
				&& (square[column + 2][row - 2].getDraughtsPiece() == null)) {
			mandatoryMovePieceColumn = column;
			mandatoryMovePieceRow = row;
			return true;
		}
		return false;
	}

	public boolean canWhiteJumpForwardToLeft(int row, int column) {
		if ((square[column][row].getDraughtsPiece().getPieceColour() == "white")
				&& (square[column - 1][row - 1].getDraughtsPiece()
						.getPieceColour() == "red")
				&& (square[column - 2][row - 2].getDraughtsPiece() == null)) {
			mandatoryMovePieceColumn = column;
			mandatoryMovePieceRow = row;
			return true;
		}
		return false;
	}

	public boolean canWhiteKingJumpBackwardsToLeft(int row, int column) {
		if ((square[column][row].getDraughtsPiece().getPieceColour() == "white")
				&& (square[column][row].getDraughtsPiece().getType() == "king")
				&& (square[column - 1][row + 1].getDraughtsPiece()
						.getPieceColour() == "red")
				&& (square[column - 2][row + 2].getDraughtsPiece() == null)) {
			mandatoryMovePieceColumn = column;
			mandatoryMovePieceRow = row;
			return true;
		}
		return false;
	}

	public boolean canWhiteKingTakeBackwardsToRight(int row, int column) {
		if ((square[column][row].getDraughtsPiece().getPieceColour() == "white")
				&& (square[column][row].getDraughtsPiece().getType() == "king")
				&& (square[column + 1][row + 1].getDraughtsPiece()
						.getPieceColour() == "red")
				&& (square[column + 2][row + 2].getDraughtsPiece() == null)) {
			mandatoryMovePieceColumn = column;
			mandatoryMovePieceRow = row;
			return true;
		}
		return false;
	}

	/*Creates a DraughtsSquare object for each square of the board.*/

	void createSquares() {
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				square[column][row] = new DraughtsSquare(column, row);
			}
		}
	}

	void initialSetup() {
		createSquares();
		addInitialMenToBoard();
		firstPartOfMoveSelected = false;
		secondPartOfMoveSelected = false;
		movedThisTurn = false;
		isRedPlayerTurn = true;
	}

	// Covers jump and non-jump moves.
	boolean isMoveLegal() {

		if (canJump() && isRequestedJumpLegal() && isRequestedMoveAJump()
				&& !firstMoveNonJump) {
			return true;
		}
		if (!canJump() && isNonJumpMoveLegal() && !isRequestedMoveAJump()) {
			return true;
		}
		firstPartOfMoveSelected = false;
		secondPartOfMoveSelected = false;
		if (isRedPlayerTurn) {
			ServerController.redInvalidMove();
		} else {
			ServerController.whiteInvalidMove();
		}
		return false;
	}

	// If the move is not a valid jump, it will be prevented by
	// isRequestedJumpLegal().

	private boolean isRequestedMoveAJump() {
		if ((Math.abs(destCol - startingCol) == 2)
				&& (Math.abs(destRow - startingRow) == 2)) {
			return true;
		}
		return false;
	}

	boolean isNonJumpMoveLegal() {
		// if red's turn
		if (isRedPlayerTurn) {
			if (isSquareEmpty(startingCol, startingRow)) {
				return false;
			}
			if (!isSquareEmpty(destCol, destRow)) {
				return false;
			}
			if (square[startingCol][startingRow].getDraughtsPiece()
					.getPieceColour() != "red") {
				return false;
			}
			if ((square[startingCol][startingRow].getDraughtsPiece()
					.getType() == "man") && ((destRow - startingRow) != 1)) {
				return false;
			}
			if ((square[startingCol][startingRow].getDraughtsPiece()
					.getType() == "man")
					&& (Math.abs(destCol - startingCol) != 1)) {
				return false;
			}
			if ((square[startingCol][startingRow].getDraughtsPiece()
					.getType() == "king")
					&& (Math.abs(destCol - startingCol) != 1)) {
				return false;
			}
			if ((square[startingCol][startingRow].getDraughtsPiece()
					.getType() == "king")
					&& (Math.abs(destRow - startingRow) != 1)) {
				return false;
			}
		}
		// if white's turn
		else {

			if (isSquareEmpty(startingCol, startingRow)) {
				return false;
			}
			if (!isSquareEmpty(destCol, destRow)) {
				return false;
			}
			if (square[startingCol][startingRow].getDraughtsPiece()
					.getPieceColour() != "white") {
				return false;
			}
			if ((square[startingCol][startingRow].getDraughtsPiece()
					.getType() == "man") && ((destRow - startingRow) != -1)) {
				return false;
			}
			if ((square[startingCol][startingRow].getDraughtsPiece()
					.getType() == "man")
					&& (Math.abs(destCol - startingCol) != 1)) {
				return false;
			}
			if ((square[startingCol][startingRow].getDraughtsPiece()
					.getType() == "king")
					&& (Math.abs(destCol - startingCol) != 1)) {
				return false;
			}
			if ((square[startingCol][startingRow].getDraughtsPiece()
					.getType() == "king")
					&& (Math.abs(destRow - startingRow) != 1)) {
				return false;
			}
		}
		return true;
	}

	boolean isSquareEmpty(int col, int row) {
		if (square[col][row].getDraughtsPiece() == null) {
			return true;
		}
		return false;
	}

	/*
	 * Checks during each player's move whether the other side has run out of pieces.
	 *
	 * A player loses when they are out of pieces.
	 */

	public boolean isOpponentOutOfPieces() {
		if (isRedPlayerTurn) {
			for (int row = 0; row < 8; row++) {
				for (int column = 0; column < 8; column++) {
					if (square[column][row].getDraughtsPiece() == null) {
						continue;
					}
					if (square[column][row].getDraughtsPiece()
							.getPieceColour() == "white") {
						return false;
					}
				}
			}
		} else {
			for (int row = 0; row < 8; row++) {
				for (int column = 0; column < 8; column++) {
					if (square[column][row].getDraughtsPiece() == null) {
						continue;
					}
					if (square[column][row].getDraughtsPiece()
							.getPieceColour() == "red") {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void actionsIfOpponentOutOfPieces() {
		if (isRedPlayerTurn) {
			redWon = true;
		}
		if (!isRedPlayerTurn) {
			whiteWon = true;
		}
		winnerActions();
	}

	// Game is over when the player whose turn it is to move is out of legal
	// moves.

	boolean isCurrentPlayerOutOfMoves() {
		if (canJump() || isLegalNonJumpMovePossible()) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isGameIsOverAtStartOfMove() {
		// Is player out of moves (meaning they have lost)?
		if (isCurrentPlayerOutOfMoves() && !movedThisTurn) {
			return true;
		}
		return false;
	}

	public void ifGameIsOverAtStartOfMove() {
		if (isRedPlayerTurn) {
			whiteWon = true;
		}
		if (!isRedPlayerTurn) {
			redWon = true;
		}
		winnerActions();
	}

	boolean isTurnOver() {
		// If the player has already moved this turn, can they jump (meaning
		// they play again)?
		if (!canJump() && movedThisTurn) {
			return true;
		}
		if (canJump() && movedThisTurn && firstMoveNonJump) {
			return true;
		}
		return false;
	}

	public void actionsIfTurnOver() {
		movedThisTurn = false; // To reset
		if (isRedPlayerTurn) {
			isRedPlayerTurn = false;
		} else {
			isRedPlayerTurn = true;
		}
		if (isRedPlayerTurn) {
			ServerController.nowRedNewTurn();
		} else {
			ServerController.nowWhiteNewTurn();
		}
	}

	// Is the requested jump legal?

	boolean isRequestedJumpLegal() {
		// if red player turn
		if (square[startingCol][startingRow].getDraughtsPiece() == null) {
			return false;
		}
		if (isRedPlayerTurn) {
			if (square[startingCol][startingRow].getDraughtsPiece()
					.getPieceColour() != "red") {
				return false;
			}
			// Any red piece - king or man - taking forwards to the left.
			if ((destCol == (startingCol - 2))
					&& (destRow == (startingRow + 2))) {
				isForwardLeftRedJumpLegal();
			}
			// Red king taking backwards to the left
			if ((destCol == (startingCol - 2))
					&& (destRow == (startingRow - 2))) {
				isBackwardLeftRedJumpLegal();
			}
			// Any red piece - king or man - taking forwards to the right
			if ((destCol == (startingCol + 2))
					&& (destRow == (startingRow + 2))) {
				isForwardsRightRedJumpLegal();
			}
			// Red king taking backwards to the right
			if ((destCol == (startingCol + 2))
					&& (destRow == (startingRow - 2))) {
				isBackwardsRightRedJumpLegal();
			}

		}
		// if white turn
		else {
			if (square[startingCol][startingRow].getDraughtsPiece()
					.getPieceColour() != "white") {
				return false;
			}
			// Any white piece - king or man - taking forwards to the left.
			if ((destCol == (startingCol - 2))
					&& (destRow == (startingRow - 2))) {
				isForwardsLeftWhiteJumpLegal();
			}
			// white king taking backwards to the left
			if ((destCol == (startingCol - 2))
					&& (destRow == (startingRow + 2))) {
				isBackwardsLeftWhiteJumpLegal();
			}
			// Any white piece - king or man - taking forwards to the right
			if ((destCol == (startingCol - 2))
					&& (destRow == (startingRow - 2))) {
				isForwardsRightWhiteJumpLegal();
			}
			// white king taking backwards to the right
			if ((destCol == (startingCol + 2))
					&& (destRow == (startingRow + 2))) {
				isBackwardsRightWhiteJumpLegal();
			}
		}
		return true;
	}

	private boolean isBackwardsRightWhiteJumpLegal() {
		if ((square[startingCol][startingRow].getDraughtsPiece()
				.getPieceColour() == "white")
				&& (square[startingCol][startingRow].getDraughtsPiece()
						.getType() == "king")
				&& (square[startingCol + 1][startingRow + 1].getDraughtsPiece()
						.getPieceColour() == "white")
				&& (square[startingCol + 2][startingRow + 2]
						.getDraughtsPiece() == null)) {
			return true;
		}
		return false;
	}

	private boolean isForwardsRightWhiteJumpLegal() {
		if ((square[startingCol][startingRow].getDraughtsPiece()
				.getPieceColour() == "white")
				&& (square[startingCol - 1][startingRow - 1].getDraughtsPiece()
						.getPieceColour() == "white")
				&& (square[startingCol - 2][startingRow - 2]
						.getDraughtsPiece() == null)) {
			return true;
		}
		return false;
	}

	private boolean isBackwardsLeftWhiteJumpLegal() {
		if ((square[startingCol][startingRow].getDraughtsPiece()
				.getPieceColour() == "white")
				&& (square[startingCol][startingRow].getDraughtsPiece()
						.getType() == "king")
				&& (startingCol >= 2) && (startingRow <= 5)
				&& (square[startingCol + 1][startingRow + 1].getDraughtsPiece()
						.getPieceColour() == "white")
				&& (square[startingCol + 2][startingRow + 2]
						.getDraughtsPiece() == null)) {
			return true;
		}
		return false;
	}

	private boolean isForwardsLeftWhiteJumpLegal() {
		if ((square[startingCol][startingRow].getDraughtsPiece()
				.getPieceColour() == "white")
				&& (startingCol >= 2)
				&& (square[startingCol - 1][startingRow - 1].getDraughtsPiece()
						.getPieceColour() == "white")
				&& (square[startingCol - 2][startingRow - 2]
						.getDraughtsPiece() == null)) {
			return true;
		}
		return false;
	}

	private boolean isBackwardsRightRedJumpLegal() {
		if ((square[startingCol][startingRow].getDraughtsPiece()
				.getPieceColour() == "red")
				&& (square[startingCol][startingRow].getDraughtsPiece()
						.getType() == "king")
				&& (startingCol <= 5) && (startingRow >= 2)
				&& (square[startingCol - 1][startingRow + 1].getDraughtsPiece()
						.getPieceColour() == "white")
				&& (square[startingCol - 2][startingRow + 2]
						.getDraughtsPiece() == null)) {
			return true;
		}
		return false;
	}

	private boolean isForwardsRightRedJumpLegal() {
		if ((square[startingCol][startingRow].getDraughtsPiece()
				.getPieceColour() == "red")
				&& (startingCol <= 5)
				&& (square[startingCol + 1][startingRow + 1].getDraughtsPiece()
						.getPieceColour() == "white")
				&& (square[startingCol + 2][startingRow + 2]
						.getDraughtsPiece() == null)) {
			return true;
		}
		return false;
	}

	private boolean isBackwardLeftRedJumpLegal() {
		if ((square[startingCol][startingRow].getDraughtsPiece()
				.getPieceColour() == "red")
				&& (square[startingCol][startingRow].getDraughtsPiece()
						.getType() == "king")
				&& (square[startingCol - 1][startingRow - 1].getDraughtsPiece()
						.getPieceColour() == "white")
				&& (square[startingCol - 2][startingRow - 2]
						.getDraughtsPiece() == null)) {
			return true;
		}
		return false;
	}

	public boolean isForwardLeftRedJumpLegal() {
		if ((square[startingCol][startingRow].getDraughtsPiece()
				.getPieceColour() == "red")
				&& (square[startingCol - 1][startingRow + 1].getDraughtsPiece()
						.getPieceColour() == "white")
				&& (square[startingCol - 2][startingRow + 2]
						.getDraughtsPiece() == null)) {
			return true;
		}
		return false;
	}

	/* The method below processes the first and second second clicks on squares
	 * by the player whose turn it is, to determine the player's requested move.
	 * The first click is interpreted as the square their piece is moving from, and
	 * the second square the destination square.
	 *
	 * In the case of a player having multiple 'leaps' available during a particular
	 * turn, the method below would be employed multiple times (and the relevant variables
	 * reset after each move within the turn).
	 *
	 * This method does not determine whether the move is legal.
	 *
	 */

	public String interpretUserMoveClicks(boolean isPieceClickFromClient1,
			int col, int row) {

		// Client 1 is always the red player.
		Boolean isTurnOfPlayerWhoClicked = ((isRedPlayerTurn
				&& isPieceClickFromClient1)
				|| (!isRedPlayerTurn && !isPieceClickFromClient1));
		// Is player jumping on first move?
		if (!movedThisTurn & canJump()) {
			firstMoveNonJump = false;
		}
		if (!movedThisTurn & !canJump()) {
			firstMoveNonJump = true;
		}
		// Second part of move
		if (isTurnOfPlayerWhoClicked && firstPartOfMoveSelected
				&& !secondPartOfMoveSelected) {
			destCol = col;
			destRow = row;
			secondPartOfMoveSelected = true;
		}
		// First part of move. Only possible to move if both clients
		// have established a connection and the players have agreed
		// to a game.
		if (isTurnOfPlayerWhoClicked && !firstPartOfMoveSelected
				&& bothClientsConnected && gameInProgress
				&& !secondPartOfMoveSelected) {
			startingCol = col;
			startingRow = row;
			firstPartOfMoveSelected = true;
		}
		// Further actions depending on whether the move is legal.
		if (isTurnOfPlayerWhoClicked && firstPartOfMoveSelected
				&& secondPartOfMoveSelected) {
			if (isMoveLegal()) {
				if (!movedThisTurn && isGameIsOverAtStartOfMove()) {
					ifGameIsOverAtStartOfMove();
				}
				movedThisTurn = true;
				playerMoveModelUpdate();
				String boardRepresentation = updateBoard();
				if (isOpponentOutOfPieces()) {
					actionsIfOpponentOutOfPieces();
				}
				if (isTurnOver()) {
					actionsIfTurnOver();
				}
				firstPartOfMoveSelected = false; // To reset
				secondPartOfMoveSelected = false; // To reset
				return boardRepresentation;

			} else {
				firstPartOfMoveSelected = false;
				secondPartOfMoveSelected = false;
				movedThisTurn = false;
			}
		}
		return "";
	}

	// Updates the move in the model if it is legal.
	// There is a bug in the code which could not be fixed prior to submission,
	// whereby jumps sometimes do not happen (thereby effectively stalling the
	// game as players must jump if there is a jump move available). Also,
	// sometimes
	// the jump takes place, but the piece that is doing the taking disappears
	// from the board.

	public void playerMoveModelUpdate() {
		boolean isJump = false;
		if (isRedPlayerTurn) {
			if (isMoveLegal()) {
				if (Math.abs(startingCol - destCol) == 2) {
					isJump = true;
				}
				if (isJump) {
					// Jumps right and forwards
					if ((startingCol < destCol) && (startingRow < destRow)) {
						DraughtsPiece toKeep = square[startingCol][startingRow]
								.getDraughtsPiece();
						// Remove piece from current square.
						square[startingCol][startingRow].setDraughtsPiece(null);
						// Put same piece in destination square (and crown if
						// reaches final row)
						if (destRow != 7) {
							square[destCol][destRow].setDraughtsPiece(toKeep);
						} else {
							square[destCol][destRow].setDraughtsPiece(
									new DraughtsPiece("king", "red"));
						}
						// Remove taken piece from board
						square[startingCol + 1][startingRow + 1]
								.setDraughtsPiece(null);
						return;
					}
					// Jumps left and forwards
					if ((startingCol > destCol) && (startingRow < destRow)) {
						var toKeep = square[startingCol][startingRow]
								.getDraughtsPiece();
						// Remove piece from current square.
						square[startingCol][startingRow].setDraughtsPiece(null);
						// Put same piece in destination square (and crown if
						// reaches final row)
						if (destRow != 7) {
							square[destCol][destRow].setDraughtsPiece(toKeep);
						} else {
							square[destCol][destRow].setDraughtsPiece(
									new DraughtsPiece("king", "red"));
						}
						// Remove taken piece from board
						square[startingCol - 1][startingRow + 1]
								.setDraughtsPiece(null);
						return;
					}
					// Jumps right and backwards - Kings only
					if ((startingCol < destCol) && (startingRow < destRow)) {
						var toKeep = square[startingCol][startingRow]
								.getDraughtsPiece();
						// Remove piece from current square.
						square[startingCol][startingRow].setDraughtsPiece(null);
						// Put same piece in destination square
						square[destCol][destRow].setDraughtsPiece(toKeep);
						// Remove taken piece from board
						square[startingCol + 1][startingRow - 1]
								.setDraughtsPiece(null);
						return;

					}
					// Jumps left and backwards - Kings only
					if ((startingCol > destCol) && (startingRow < destRow)) {
						var toKeep = square[startingCol][startingRow]
								.getDraughtsPiece();
						// Remove piece from current square.
						square[startingCol][startingRow].setDraughtsPiece(null);
						// Put same piece in destination square
						square[destCol][destRow].setDraughtsPiece(toKeep);
						// Remove taken piece from board
						square[startingCol - 1][startingRow - 1]
								.setDraughtsPiece(null);
						return;
					}
					// Code below deals with non-jump moves
				} else {
					var toKeep = square[startingCol][startingRow]
							.getDraughtsPiece();
					// Remove piece being moved from existing square.
					square[startingCol][startingRow].setDraughtsPiece(null);
					// Add piece being to its new square
					if (destRow != 7) {
						square[destCol][destRow].setDraughtsPiece(toKeep);
					} else {
						square[destCol][destRow].setDraughtsPiece(
								new DraughtsPiece("king", "red"));
					}
				}
			}
		}
		// if white move
		else {
			isJump = false;
			if (isMoveLegal()) {
				if (Math.abs(startingCol - destCol) == 2) {
					isJump = true;
				}
				if (isJump) {
					// Jumps right and forwards
					if ((startingCol < destCol) && (startingRow > destRow)) {
						var toKeep = square[startingCol][startingRow]
								.getDraughtsPiece();
						// Remove piece from current square.
						square[startingCol][startingRow].setDraughtsPiece(null);
						// Put same piece in destination square (and crown if
						// reaches final row)
						if (destRow != 0) {
							square[destCol][destRow].setDraughtsPiece(toKeep);
						} else {
							square[destCol][destRow].setDraughtsPiece(
									new DraughtsPiece("king", "white"));
						}
						// Remove taken piece from board
						square[startingCol + 1][startingRow - 1]
								.setDraughtsPiece(null);
						return;
					}
					// Jumps left and forwards
					if ((startingCol > destCol) && (startingRow > destRow)) {
						var toKeep = square[startingCol][startingRow]
								.getDraughtsPiece();
						// Remove piece from current square.
						square[startingCol][startingRow].setDraughtsPiece(null);
						// Put same piece in destination square (and crown if
						// reaches final row)
						if (destRow != 0) {
							square[destCol][destRow].setDraughtsPiece(toKeep);
						} else {
							square[destCol][destRow].setDraughtsPiece(
									new DraughtsPiece("king", "white"));
						}
						// Remove taken piece from board
						square[startingCol - 1][startingRow - 1]
								.setDraughtsPiece(null);
						return;
					}
					// Jumps right and backwards - Kings only
					if ((startingCol < destCol) && (startingRow < destRow)) {
						var toKeep = square[startingCol][startingRow]
								.getDraughtsPiece();
						// Remove piece from current square.
						square[startingCol][startingRow].setDraughtsPiece(null);
						// Put same piece in destination square
						square[destCol][destRow].setDraughtsPiece(toKeep);
						// Remove taken piece from board
						square[startingCol + 1][startingRow + 1]
								.setDraughtsPiece(null);
						return;

					}
					// Jumps left and backwards - Kings only
					if ((startingCol > destCol) && (startingRow < destRow)) {
						var toKeep = square[startingCol][startingRow]
								.getDraughtsPiece();
						// Remove piece from current square.
						square[startingCol][startingRow].setDraughtsPiece(null);
						// Put same piece in destination square
						square[destCol][destRow].setDraughtsPiece(toKeep);
						// Remove taken piece from board
						square[startingCol - 1][startingRow + 1]
								.setDraughtsPiece(null);
						return;
					}
					// Code below deals with non-jump moves
				} else {
					var toKeep = square[startingCol][startingRow]
							.getDraughtsPiece();
					// Remove piece being moved from existing square.
					square[startingCol][startingRow].setDraughtsPiece(null);
					// Add piece being to its new square
					if (destRow != 0) {
						square[destCol][destRow].setDraughtsPiece(toKeep);
					} else {
						square[destCol][destRow].setDraughtsPiece(
								new DraughtsPiece("king", "white"));
					}
				}

			}
		}
	}

	public void turnNotYetOverMsg() {
		if (isRedPlayerTurn) {
			ServerController.stillRedTurn();
		} else {
			ServerController.stillWhiteTurn();
		}
	}

	public void turnNowOverMsg() {
		if (isRedPlayerTurn) {
			ServerController.nowRedTurn();
		} else {
			ServerController.nowWhiteTurn();
		}
	}

	/* After each move, a comma-delimited String representation of the board is sent to the clients,
	 * so that the board can be updated.
	 */
	public String updateBoard() {

		String boardRepresentation = "board,";
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {

				if (square[column][row].getDraughtsPiece() == null) {
					boardRepresentation += "null,";

					continue;
				}
				if ((square[column][row].getDraughtsPiece().getType() == "man")
						&& (square[column][row].getDraughtsPiece()
								.getPieceColour() == "red")) {
					boardRepresentation += "red_man,";

					continue;
				}
				if ((square[column][row].getDraughtsPiece().getType() == "man")
						&& (square[column][row].getDraughtsPiece()
								.getPieceColour() == "white")) {
					boardRepresentation += "white_man,";

					continue;
				}
				if ((square[column][row].getDraughtsPiece().getType() == "king")
						&& (square[column][row].getDraughtsPiece()
								.getPieceColour() == "red")) {
					boardRepresentation += "red_king,";

					continue;
				}
				if ((square[column][row].getDraughtsPiece().getType() == "king")
						&& (square[column][row].getDraughtsPiece()
								.getPieceColour() == "white")) {
					boardRepresentation += "white_king,";
				}
			}
		}
		return boardRepresentation.substring(0,
				boardRepresentation.length() - 1);
	}

	public void winnerActions() {
		// To reset
		gameInProgress = false;
		firstPartOfMoveSelected = false;
		secondPartOfMoveSelected = false;
		// To inform clients
		if (redWon) {
			ServerController.redWon();
		} else {
			ServerController.whiteWon();
		}
	}

	public void newGameAcceptedActions() {
		initialSetup();
		gameInProgress = true;
		initialSetup();
		String newBoardRepresentation = updateBoard();// To remove previous game
														// board state.
		ServerController.setMessageToClients(newBoardRepresentation);
	}

	public boolean isMovedThisTurn() {
		return movedThisTurn;
	}

	public boolean isRedPlayerTurn() {
		return isRedPlayerTurn;
	}

	public void setBothClientsConnected(boolean bothClientsConnected) {
		this.bothClientsConnected = bothClientsConnected;
	}

}
