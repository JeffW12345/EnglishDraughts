import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JOptionPane;

/*
 * The ClientController class sends messages to the server and updates the view in response to
 * messages from the server and from user button presses. In addition, it implements
 * WindowListener in order to detect when a client GUI has been closed, so that it can send a
 * message to the server that will result in both clients and the server being safely disconnected.
 *
 */

public class ClientController implements WindowListener {

	private final DraughtsBoardView view;
	public ClientThread clientThread;

	private boolean drawOfferSentPending, drawOfferReceivedPending, isRedsTurn = true, amIRed,
			bothClientsConnectedToServer;
	private boolean newGameAgreedByPlayers;

	ClientController(ClientThread clientThread) {
		this.clientThread = clientThread;
		view = new DraughtsBoardView(this);
	}

	public void acceptDrawButtonPressed() {
		String messageToServer = clientThread.nextMessageId() + "," + "draw_accept";
		clientThread.out.println(messageToServer);
		view.getAcceptDrawButton().setEnabled(false);
		view.getOfferNewGameButton().setEnabled(true);
		newGameAgreedByPlayers = false;// To prevent further moves
		acceptDrawButtonPressedMsg();
	}

	String acceptMsg() {
		return "Do you accept the offer?";
	}

	public void acceptNewGameButtonPressed() {
		String messageToServer = clientThread.nextMessageId() + "," + "new_game_accept";
		clientThread.out.println(messageToServer);
		view.getAcceptNewGameButton().setEnabled(false);
		view.getOfferDrawButton().setEnabled(true);
		view.getResignButton().setEnabled(true);
		newGameAgreedByPlayers = true;
		acceptNewGameButtonPressedMsg();
	}

	String colourMessage() {
		if (amIRed) {
			return "You are the red player";
		} else {
			return "You are the white player";
		}
	}

	public boolean isDrawOfferReceivedPending() {
		return drawOfferReceivedPending;
	}

	public boolean isDrawOfferSentPending() {
		return drawOfferSentPending;
	}

	public boolean isRedsTurn() {
		return isRedsTurn;
	}

	public void offerDrawButtonPressed() {
		String messageToServer = clientThread.nextMessageId() + "," + "draw_offer";
		clientThread.out.println(messageToServer);
		view.getOfferDrawButton().setEnabled(false);
		drawOfferSentPending = true;
		offerDrawButtonPressedMsg();
	}

	public void offerNewGameButtonPressed() {
		String messageToServer = clientThread.nextMessageId() + "," + "new_game_offer";
		clientThread.out.println(messageToServer);
		view.getOfferNewGameButton().setEnabled(false);
		offerNewGameButtonPressedMsg();

	}

	public void resignButtonPressed() {
		String messageToServer = clientThread.nextMessageId() + "," + "resign";
		clientThread.out.println(messageToServer);
		view.getOfferNewGameButton().setEnabled(true);
		view.getAcceptNewGameButton().setEnabled(false);
		view.getAcceptDrawButton().setEnabled(false);
		view.getResignButton().setEnabled(false);
		view.getOfferDrawButton().setEnabled(false);
		newGameAgreedByPlayers = false;// To prevent further moves
		resignButtonPressedMsg();
	}

	public void setDrawOfferReceivedPending(boolean drawOfferReceivedPending) {
		this.drawOfferReceivedPending = drawOfferReceivedPending;
	}

	public void setDrawOfferSentPending(boolean drawOfferSentPending) {
		this.drawOfferSentPending = drawOfferSentPending;
	}

	public void setPlayerColour() {
		if (clientThread.getOrder() == 1) {
			amIRed = true;
		} else {
			amIRed = false;
		}
	}

	public void setRedsTurn(boolean isRedsTurn) {
		this.isRedsTurn = isRedsTurn;
	}

	// This method responds sends squares clicked data to the server.
	// In addition, it reduces excess messages to server that could
	// potentially cause race conditions by creating a pop up box
	// if the user clicks a square out of turn or before they have
	// agreed a game with their opponent.

	public void squareClicked(int column, int row) {

		if (!newGameAgreedByPlayers) {
			JOptionPane.showMessageDialog(view.frame, "You need to agree a game before playing.");
			return;
		}
		if (amIRed && !isRedsTurn) {
			JOptionPane.showMessageDialog(view.frame, "It is not your turn, it is white's turn");
			return;
		}
		if (!amIRed && isRedsTurn) {
			JOptionPane.showMessageDialog(view.frame, "It is not your turn, it is red's turn");
			return;
		}

		String messageToServer = clientThread.nextMessageId() + "," + "square_clicked" + "," + column + "," + row;
		clientThread.out.println(messageToServer);
	}

	String turnMsg() {
		if (amIRed && isRedsTurn) {
			return "It is your turn.";
		}
		if (!amIRed && !isRedsTurn) {
			return "It is your turn.";
		} else {
			return "It's the other player's turn.";
		}
	}

	// Uses the string representation of the board obtained from the server to
	// update the view.

	void updateBoard(ClientThread clientThread, String boardRepresentation) {
		var parsed = boardRepresentation.split(",");
		// squares starts at one as the first entry in the string is 'board'
		for (int squares = 1; squares < parsed.length; squares++) {
			// To convert the position in the array into co-ordinates
			// Top left corner is col 0, row 0.
			int col = (squares - 2) % 8;
			int row = (squares - 2) / 8;

			if (parsed[squares].equals("null")) {
				view.setBlank(col, row);
			}
			if (parsed[squares].equals("red_man")) {
				view.addRedMan(col, row);
			}
			if (parsed[squares].equals("white_man")) {
				view.addWhiteMan(col, row);
			}
			if (parsed[squares].equals("red_king")) {
				view.addRedKing(col, row);
			}
			if (parsed[squares].equals("white_king")) {
				view.addWhiteKing(col, row);
			}
		}
		view.frame.repaint();
		view.frame.setVisible(true);
	}

	public void ifSentDrawOfferExpires(ClientThread clientThread) {
		setDrawOfferSentPending(false);
		view.getOfferNewGameButton().setEnabled(false);
		view.getAcceptNewGameButton().setEnabled(false);
		view.getAcceptDrawButton().setEnabled(false);
		view.getResignButton().setEnabled(true);
		view.getOfferDrawButton().setEnabled(true);
		ifWhiteLostMsg();
	}

	public void ifRedLost(ClientThread clientThread) {
		view.getOfferNewGameButton().setEnabled(true);
		view.getResignButton().setEnabled(false);
		view.getOfferDrawButton().setEnabled(false);
		view.getAcceptNewGameButton().setEnabled(false);
		view.getAcceptDrawButton().setEnabled(false);
		newGameAgreedByPlayers = false; // To prevent further moves
		ifRedLostMsg();
	}

	public void ifWhiteLost(ClientThread clientThread) {
		view.getOfferNewGameButton().setEnabled(true);
		view.getResignButton().setEnabled(false);
		view.getOfferDrawButton().setEnabled(false);
		view.getAcceptNewGameButton().setEnabled(false);
		view.getAcceptDrawButton().setEnabled(false);
		newGameAgreedByPlayers = false; // To prevent further moves
		ifWhiteLostMsg();
	}

	public void ifNewGameOfferAcceptedByOtherClient(ClientThread clientThread) {
		view.getResignButton().setEnabled(true);
		view.getOfferDrawButton().setEnabled(true);
		view.getOfferNewGameButton().setEnabled(false);
		view.getAcceptNewGameButton().setEnabled(false);
		view.getAcceptDrawButton().setEnabled(false);
		isRedsTurn = true; // Red starts first in new games.
		drawOfferSentPending = false; // To cancel any pending offers.
		drawOfferReceivedPending = false;
		newGameAgreedByPlayers = true;
		ifNewGameOfferAcceptedByOtherClientMsg();
	}

	public void ifNewGameOfferReceived(ClientThread clientThread) {
		view.getAcceptNewGameButton().setEnabled(true);
		view.getOfferNewGameButton().setEnabled(false);
		view.getAcceptDrawButton().setEnabled(false);
		view.getResignButton().setEnabled(false);
		view.getOfferDrawButton().setEnabled(false);
		ifNewGameOfferReceivedMsg();
	}

	public void ifOtherPlayerResigns(ClientThread clientThread) {
		view.getOfferNewGameButton().setEnabled(true);
		view.getAcceptNewGameButton().setEnabled(false);
		view.getAcceptDrawButton().setEnabled(false);
		view.getResignButton().setEnabled(false);
		view.getOfferDrawButton().setEnabled(false);
		newGameAgreedByPlayers = false; // To prevent further moves
		ifOtherPlayerResignsMsg();
	}

	public void ifDrawOfferAcceptedByOtherClient(ClientThread clientThread) {
		view.getOfferNewGameButton().setEnabled(true);
		view.getAcceptNewGameButton().setEnabled(false);
		view.getAcceptDrawButton().setEnabled(false);
		view.getResignButton().setEnabled(false);
		view.getOfferDrawButton().setEnabled(false);
		newGameAgreedByPlayers = false; // To prevent further moves
		ifDrawOfferAcceptedByOtherClientMsg();
	}

	public void ifDrawOfferMadeByOtherClient(ClientThread clientThread) {
		setDrawOfferReceivedPending(true);
		view.getAcceptDrawButton().setEnabled(true);
		view.getOfferDrawButton().setEnabled(false);
		view.getResignButton().setEnabled(false);
		view.getAcceptNewGameButton().setEnabled(false);
		view.getOfferNewGameButton().setEnabled(false);
		ifDrawOfferMadeByOtherClientMsg();
	}

	public void ifReceivedDrawOfferExpires(ClientThread clientThread) {
		ifReceivedDrawOfferExpiresMsg();
		setDrawOfferReceivedPending(false);
		view.getAcceptDrawButton().setEnabled(false);
		view.getOfferDrawButton().setEnabled(true);
		view.getResignButton().setEnabled(true);
		view.getAcceptNewGameButton().setEnabled(false);
		view.getOfferNewGameButton().setEnabled(false);
		ifReceivedDrawOfferExpiresMsg();
	}

	public boolean isAmIRed() {
		return amIRed;
	}

	public void setAmIRed(boolean amIRed) {
		this.amIRed = amIRed;
	}

	public void setWelcomeMessage() {

		view.setTopLineMessage("Welcome to English Draughts!");
		view.setMiddleLineMessage("");
		view.setBottomLineMessage(colourMessage());
	}

	public void ifReceivedDrawOfferExpiresMsg() {
		view.setTopLineMessage("The draw offer has expired.");
		view.setMiddleLineMessage(turnMsg());
		view.setBottomLineMessage("");
		view.updateLabels();
	}

	public void ifDrawOfferMadeByOtherClientMsg() {
		view.setTopLineMessage("You've been offered a draw.");
		view.setMiddleLineMessage("Click the button to accept.");
		view.setBottomLineMessage("");
		view.updateLabels();
	}

	public void ifDrawOfferAcceptedByOtherClientMsg() {
		view.setTopLineMessage("Your offer was accepted!");
		view.setMiddleLineMessage("It's a draw.");
		view.setBottomLineMessage("");
		view.updateLabels();
	}

	public void ifOtherPlayerResignsMsg() {
		view.setTopLineMessage("Your opponent has resigned!");
		view.setMiddleLineMessage("You've won - Well done.");
		view.setBottomLineMessage("");
		view.updateLabels();
	}

	public void ifNewGameOfferReceivedMsg() {
		view.setTopLineMessage("Your opponent has offered a game.");
		view.setMiddleLineMessage("Click the button to accept.");
		view.setBottomLineMessage("");
		view.updateLabels();
	}

	public void ifNewGameOfferAcceptedByOtherClientMsg() {
		view.setTopLineMessage("The other client has accepted.");
		view.setMiddleLineMessage("Let battle commence!");
		view.setBottomLineMessage(colourMessage());
		view.updateLabels();
	}

	public void ifWhiteLostMsg() {
		if (amIRed) {
			view.setTopLineMessage("Well done! You've won");
			view.setMiddleLineMessage("");
			view.setBottomLineMessage("");
			view.updateLabels();
		} else {
			view.setTopLineMessage("I'm afraid you've lost.");
			view.setMiddleLineMessage("Better luck next time.");
			view.setBottomLineMessage("");
			view.updateLabels();
		}

	}

	public void ifRedLostMsg() {
		if (!amIRed) {
			view.setTopLineMessage("Well done! You've won");
			view.setMiddleLineMessage("");
			view.setBottomLineMessage("");
			view.updateLabels();
		} else {
			view.setTopLineMessage("I'm afraid you've lost.");
			view.setMiddleLineMessage("Better luck next time.");
			view.setBottomLineMessage("");
			view.updateLabels();
		}
	}

	public void offerDrawButtonPressedMsg() {
		view.setTopLineMessage("You've offered a draw.");
		view.setMiddleLineMessage("Waiting for a response...");
		view.setBottomLineMessage("Expires end of turn.");
		view.updateLabels();
	}

	public void resignButtonPressedMsg() {
		view.setTopLineMessage("You have resigned.");
		view.setMiddleLineMessage("Better luck next time.");
		view.setBottomLineMessage("Press 'New Game' to play again.");
		view.updateLabels();
	}

	public void offerNewGameButtonPressedMsg() {
		view.setTopLineMessage("You've offered a new game");
		view.setMiddleLineMessage("Waiting for your opponent.");
		view.setBottomLineMessage("");
		view.updateLabels();
	}

	public void acceptNewGameButtonPressedMsg() {
		view.setTopLineMessage("You've accepted a new game.");
		view.setMiddleLineMessage("Good luck!");
		view.setBottomLineMessage(colourMessage());
		view.updateLabels();
	}

	public void acceptDrawButtonPressedMsg() {
		view.setTopLineMessage("You have agreed to a draw.");
		view.setMiddleLineMessage("The game is over.");
		view.setBottomLineMessage("");
		view.updateLabels();
	}

	public void stillRedMoveMsg() {
		view.setTopLineMessage("It is still red's move");
		view.setMiddleLineMessage("There is a jump available.");
		view.setBottomLineMessage("");
		view.updateLabels();
	}

	public void stillWhiteMoveMsg() {
		view.setTopLineMessage("It is still white's move");
		view.setMiddleLineMessage("There is a jump available.");
		view.setBottomLineMessage("");
		view.updateLabels();
	}

	public void whiteMoveOverMsg() {
		view.setTopLineMessage("White's move is over.");
		view.setMiddleLineMessage("It's now red's turn.");
		view.setBottomLineMessage("");
		view.updateLabels();
	}

	public void redMoveOverMsg() {
		view.setTopLineMessage("Red's move is over.");
		view.setMiddleLineMessage("It's now white's turn.");
		view.setBottomLineMessage("");
		view.updateLabels();
	}

	public boolean isBothPlayersReady() {
		return bothClientsConnectedToServer;
	}

	public void setBothPlayersReady(boolean bothPlayersReady) {
		bothClientsConnectedToServer = bothPlayersReady;
	}

	public void bothPlayersReadyActions() {
		if (bothClientsConnectedToServer) {
			view.initialSetup();
			view.setMiddleLineMessage("Both players are connected");
			if (amIRed) {
				view.setBottomLineMessage("You are the red player.");
			} else {
				view.setBottomLineMessage("You are the white player.");
			}
			view.updateLabels();
		}

	}

	public void invalidMove(ClientThread thread) {
		JOptionPane.showMessageDialog(view.frame, "Invalid move. Please try again");
	}

	// The empty methods below are included as a result of the fact that the
	// class implements WindowListener.
	@Override
	public void windowOpened(WindowEvent e) {

	}

	// Sends message to server to cleanly disconnect both clients and shut down
	// the
	// server in the event of a client's GUI being closed.
	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		clientThread.out.println(clientThread.nextMessageId() + "," + "end");
		System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}
}
