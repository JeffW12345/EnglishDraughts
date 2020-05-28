
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/* The ServerClientListenerThread constructor takes as an input an int an int �order� value for 1
 * for the first socket to be connected, and �2� for the second socket to be to be connected,
 * as well as the relevant socket.
 *
 * ServerClientListenerThread is also used to send messages to the clients and to receive messages
 * from them. It receives messages on loop that does not terminate while the connection is active,
 * and passes them to the ServerController class to process.
 */

class ServerClientListenerThread implements Runnable {

	private final int order;// 1 = first client, 2 = second client
	private final Socket socket;
	private PrintWriter out;
	boolean ready; // is thread able to receive and send messages?
	private Scanner in;
	private String inputMsgFromClient;
	private final ServerController controller;
	private int outBoundMessageID;

	ServerClientListenerThread(Socket socket, int order) throws IOException {
		this.socket = socket;
		this.order = order;
		ready = false;
		controller = new ServerController(this);
	}

	String clientOutHeader() {
		System.out.print("Server client " + order + ":");
		return "";
	}

	public String getMessageFromClient() {
		inputMsgFromClient = in.nextLine().trim();
		return inputMsgFromClient;
	}

	public int getOrder() {
		return order;
	}

	/*
	 *Gets messages from client
	 */
	@Override
	public void run() {

		try {
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			System.out.println(clientOutHeader() + " listening for messages");
			System.out
					.println(clientOutHeader() + " on port " + socket.getPort()
							+ ": " + socket.getInetAddress().toString());

			// allocate client the order
			// send a message to the client telling it which order it is
			// out.println("hello, " + order);
			ready = true;
			ServerController.setMessageToClient("hello, " + order, order);
			while (true) {
				String inputMsgFromClient = in.nextLine().trim();
				System.out.println(clientOutHeader()
						+ " client to server message received: "
						+ inputMsgFromClient);
				controller.processInboundMsgs(inputMsgFromClient, this);
			}
		}

		catch (

		Exception e) {
			System.out.println(clientOutHeader() + e.getMessage());
			e.printStackTrace(System.err);

		}
	}

	void sendMessageToClient(String msg) {
		if (ready && (msg.length() > 0)) {
			out.println(nextOutboundMessageId() + ", " + msg);
		}
	}

	/*
	 * outBoundMessageID is 0 on the first message, and incremented by 1 for every subsequent
	 * message
	 */

	public int nextOutboundMessageId() {
		int origValue = outBoundMessageID;
		outBoundMessageID++;
		return origValue;
	}

}
