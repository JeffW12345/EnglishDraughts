
/*
 * Sets up a connection with the server. Also sends to server and receives messages from it.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientThread implements Runnable {
	static final int PORT = 50000;
	public PrintWriter out;
	public Scanner in;
	private Socket socket;
	public int order;
	ClientController controller;
	private int messageID = 0;

	public int getOrder() {
		return order;
	}

	// Records value of messageId before updating it, then increments the
	// messageId and returns the original value of messageID
	public int nextMessageId() {
		int origValue = messageID;
		messageID++;
		return origValue;
	}

	@Override
	public void run() {

		try {
			serverConnect(); // Sets up a connection with the server.
			controller = new ClientController(this);
			boolean serverNotYetToldBothClientsConnected = true;
			// The functionality within the 'while' loop deals with incoming messages from
			// the server.
			while (true) {
				String inputMsgFromServer = in.nextLine().trim();
				// Console update to check everything working as it should be.
				System.out.println(
						Thread.currentThread().getName() + " received message from server: " + inputMsgFromServer);
				// When the server connects with a client, it sends them a 'hello' message.
				// The first client to connect gets sent the message: 0, hello, 1. The '1' shows
				// that it is the first client.
				// This first client is the red player, so this message is used to tell the
				// clients whether they are red or white.
				var parsed = CommandParser.parseFromMessage(inputMsgFromServer);
				if (parsed[1].toString().contains("hello")) {
					order = parsed[2].asInt();
					controller.setRedsTurn(true);
					if (order == 1) {
						controller.setAmIRed(true);
					} else {
						controller.setAmIRed(false);
					}
					Thread.currentThread().setName("Client listener " + (order == 1 ? "first " : "second ") + "thread");

					// When the second (and therefore final) client has been
					// created, this tells server both clients are connected.

					if ((order == 2) && serverNotYetToldBothClientsConnected) {
						out.println(nextMessageId() + ", " + "both_ready");
						serverNotYetToldBothClientsConnected = false;
					}
					continue;
				}

				// Server telling both clients that they are both ready.
				if (parsed[1].toString().contains("both_ready_acknowledgement")) {
					controller.setBothPlayersReady(true);
					controller.bothPlayersReadyActions();
					continue;
				}

				// Updating the board graphics after a move has been made.
				// The server passes in a representation of the board as a message.
				if (parsed[1].toString().contains("board")) {
					controller.updateBoard(inputMsgFromServer);
					continue;
				}
				// Actions if it has just become red's turn (excluding first move).
				if (parsed[1].toString().equals("new_turn_red")) {
					controller.setRedsTurn(true);
					if (controller.isDrawOfferSentPending()) {
						controller.ifSentDrawOfferExpires();
					}
					if (controller.isDrawOfferReceivedPending()) {
						controller.ifReceivedDrawOfferExpires();
					}
				}
				// Actions if it has just become white's turn
				if (parsed[1].toString().equals("new_turn_white")) {
					controller.setRedsTurn(false);
					if (controller.isDrawOfferSentPending()) {
						controller.ifSentDrawOfferExpires();
					}
					if (controller.isDrawOfferReceivedPending()) {
						controller.ifReceivedDrawOfferExpires();
					}
				}

				if (parsed[1].toString().equals("draw_offer_made")) {
					controller.ifDrawOfferMadeByOtherClient();
				}
				if (parsed[1].toString().equals("draw_offer_accepted")) {
					controller.ifDrawOfferAcceptedByOtherClient();
				}
				if (parsed[1].toString().equals("other_player_resigned")) {
					controller.ifOtherPlayerResigns();
				}
				if (parsed[1].toString().equals("want_a_game")) {

					controller.ifNewGameOfferReceived();
				}
				if (parsed[1].toString().equals("new_game_offer_accepted")) {
					controller.ifNewGameOfferAcceptedByOtherClient();
				}

				if (parsed[1].toString().equals("white_lost")) {
					controller.ifWhiteLost();
				}
				if (parsed[1].toString().equals("red_lost")) {
					controller.ifRedLost();
				}
				if (parsed[1].toString().equals("invalid_move")) {
					controller.invalidMove();
				}
				if (parsed[1].toString().equals("end")) {
					System.exit(0);
				}
			}
		} catch (

		IOException e) {
			e.printStackTrace();
		}

	}

	public void serverConnect() throws UnknownHostException, IOException {
		socket = new Socket("127.0.0.1", ClientThread.PORT);
		in = new Scanner(socket.getInputStream());
		out = new PrintWriter(socket.getOutputStream(), true);

	}

	public void start(String name) {
		new Thread(this, name).start();

	}

}
