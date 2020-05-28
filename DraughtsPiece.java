
/*
 * For creating draughts pieces objects in the ServerModel class.
 */

public class DraughtsPiece {
	private String type, pieceColour;

	public DraughtsPiece(String type, String pieceColour) {
		this.type = type;
		this.pieceColour = pieceColour;
	}

	public String getPieceColour() {
		return pieceColour;
	}

	public String getType() {
		return type;
	}

	public void setPieceColour(String pieceColour) {
		this.pieceColour = pieceColour;
	}

	public void setType(String type) {
		this.type = type;
	}

}
