import java.io.IOException;

public class ServerController {
	private final ServerClientListenerThread client;

	public ServerController(ServerClientListenerThread client) throws IOException {
		this.client = client;
	}

	/*
	 * Parses messages from the clients and sends them to the model for processing,
	 * as well as sending messages to the clients in response to messages received.
	 *
	 */

	public void processInboundMsgs(String msg, ServerClientListenerThread listenerThread) {

		var parsed = CommandParser.parseFromMessage(msg); // Array of message
															// parts.
		int order = client.getOrder(); // Client 1 or client 2?
		parsed[0].asInt();
		String command = parsed[1].toString();

		if (command.equals("end")) {
			// Tell other client we are ending.
			// Used as part of functionality to shut everything down if a GUI
			// is closed.
			setMessageToOtherClient("end", order);
			System.exit(0);
		}
		// A square on the board has been clicked.
		if (command.equals("square_clicked")) {
			// Columns and rows are both 0 to 7, starting top left of board as
			// it
			// appears on screen.
			int col = parsed[2].asInt();
			int row = parsed[3].asInt();
			String rep = model().interpretUserMoveClicks(order == 1, col, row);
			setMessageToClients(rep);
			return;
		}
		// If a draw has been offered by either client.
		if (command.equals("draw_offer")) {
			setMessageToOtherClient("draw_offer_made", order);
			return;
		}
		// If a new game has been offered by either client.
		if (command.equals("new_game_offer")) {
			setMessageToOtherClient("want_a_game", order);
			return;
		}
		// If a draw offer has been accepted by either client.
		if (command.equals("draw_accept")) {
			setMessageToOtherClient("draw_offer_accepted", order);
			model().newGameAcceptedActions();
			return;
		}

		// If a new game offer has been accepted by either client.
		if (command.equals("new_game_accept")) {
			setMessageToOtherClient("new_game_offer_accepted", order);
			model().newGameAcceptedActions();
			return;
		}

		// If either client has resigned.
		if (command.equals("resign")) {
			if (order == 1 || order == 2) {
				setMessageToOtherClient("other_player_resigned", order);
				return;
			}
		}

		// Indicates that both clients are connected to the server.
		if (command.equals("both_ready")) {
			ServerController.setMessageToClients("both_ready_acknowledgement");
			model().setBothClientsConnected(true);
			return;
		}

		throw new Error("Unrecognised message: " + parsed[1]);

	}

	private ServerModel model() {

		return Server.model;
	}

	// Sends a message to the specified client.
	public static void setMessageToClient(String messageToClient, int order) {
		Server.getServer().getThreadFor(order).sendMessageToClient(messageToClient);
	}

	public static void setMessageToOtherClient(String messageToClient, int current) {
		Server.getServer().getThreadFor(current == 1 ? 2 : 1).sendMessageToClient(messageToClient);
	}

	// Sends a message to both clients.
	public static void setMessageToClients(String messageToClient) {
		ServerController.setMessageToClient(messageToClient, 1);
		ServerController.setMessageToClient(messageToClient, 2);
	}

	public static void redInvalidMove() {
		setMessageToClient("invalid_move", 1);

	}

	public static void whiteInvalidMove() {
		setMessageToClient("invalid_move", 2);
	}

	public static void stillRedTurn() {
		setMessageToClients("still_red_turn");
	}

	public static void stillWhiteTurn() {
		setMessageToClients("still_white_turn");

	}

	public static void nowRedTurn() {
		ServerController.setMessageToClients("now_red_turn");

	}

	public static void nowWhiteTurn() {
		ServerController.setMessageToClients("now_white_turn");
	}

	public static void redWon() {
		setMessageToClients("red_won_game");
	}

	public static void whiteWon() {
		setMessageToClients("white_won_game");
	}

	public static void nowRedNewTurn() {
		setMessageToClients("new_turn_red");

	}

	public static void nowWhiteNewTurn() {
		setMessageToClients("new_turn_white");

	}
}
