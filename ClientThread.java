
/*
 * Sets up a connection with the server.
 *
 * Also sends and receives messages.
 *
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

	ClientThread() {
	}

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
			serverConnect();
			controller = new ClientController(this);

			boolean serverNotYetToldBothClientsConnected = true;
			while (true) {

				String inputMsgFromServer = in.nextLine().trim();
				System.out.println(Thread.currentThread().getName()
						+ " received message: " + inputMsgFromServer);
				var parsed = CommandParser.parseFromMessage(inputMsgFromServer);

				if (parsed[1].toString().contains("hello")) {
					order = parsed[2].asInt();
					controller.setRedsTurn(true);
					if (order == 1) {
						controller.setAmIRed(true);
					} else {
						controller.setAmIRed(false);
					}
					Thread.currentThread().setName("Client listener "
							+ (order == 1 ? "first " : "second ") + "thread");

					// When the second (and therefore final) client has been
					// created, this tells server both clients are connected.

					if ((order == 2) && serverNotYetToldBothClientsConnected) {
						out.println(nextMessageId() + ", " + "both_ready");
						serverNotYetToldBothClientsConnected = false;
					}
					continue;
				}

				// Server telling both clients that they are both ready.
				if (parsed[1].toString()
						.contains("both_ready_acknowledgement")) {
					controller.setBothPlayersReady(true);
					controller.bothPlayersReadyActions();
					continue;
				}

				if (parsed[1].toString().contains("board")) {
					controller.updateBoard(this, inputMsgFromServer);
					continue;
				}

				if (parsed[1].toString().equals("new_turn_red")) {
					controller.setRedsTurn(true);
					if (controller.isDrawOfferSentPending()) {
						controller.ifSentDrawOfferExpires(this);
					}
					if (controller.isDrawOfferReceivedPending()) {
						controller.ifReceivedDrawOfferExpires(this);
					}
				}

				if (parsed[1].toString().equals("new_turn_white")) {
					controller.setRedsTurn(false);
					if (controller.isDrawOfferSentPending()) {
						controller.ifSentDrawOfferExpires(this);
					}
					if (controller.isDrawOfferReceivedPending()) {
						controller.ifReceivedDrawOfferExpires(this);
					}
				}

				if (parsed[1].toString().equals("draw_offer_made")) {
					controller.ifDrawOfferMadeByOtherClient(this);
				}
				if (parsed[1].toString().equals("draw_offer_accepted")) {
					controller.ifDrawOfferAcceptedByOtherClient(this);
				}
				if (parsed[1].toString().equals("other_player_resigned")) {
					controller.ifOtherPlayerResigns(this);
				}
				if (parsed[1].toString().equals("want_a_game")) {
					System.out.println("ORDER DEBUG + " + order);
					controller.ifNewGameOfferReceived(this);
				}
				if (parsed[1].toString().equals("new_game_offer_accepted")) {
					controller.ifNewGameOfferAcceptedByOtherClient(this);
				}

				if (parsed[1].toString().equals("white_lost")) {
					controller.ifWhiteLost(this);
				}
				if (parsed[1].toString().equals("red_lost")) {
					controller.ifRedLost(this);
				}
				if (parsed[1].toString().equals("invalid_move")) {
					controller.invalidMove(this);
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