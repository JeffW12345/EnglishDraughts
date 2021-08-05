
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * This is the game's server class.
 *
 * It accepts two incoming connections, and sets up a thread for each of them.
 *
 */

class Server {
	static final int PORT = 50000;
	private static Server server;
	public static ServerModel model;
	public ServerClientListenerThread thread1, thread2;

	private final Socket firstClientSocket;

	public static Server getServer() {
		return Server.server;
	}

	public static void main(String[] args) throws IOException {
		Server.server = new Server();
	}

	// Adds threads to the clients

	private final Socket secondClientSocket;

	Server() throws IOException {
		Server.server = this;
		Server.model = new ServerModel();

		System.out.println("Server: listening port " + Server.PORT);
		ServerSocket socket = new ServerSocket(Server.PORT);
		firstClientSocket = socket.accept();
		System.out.println("Server: connection established with first client");
		manageFirstClientConnected(firstClientSocket);// creates a thread
		secondClientSocket = socket.accept();
		System.out.println("Server: connection established with second client");
		manageSecondClientConnected(secondClientSocket);// creates a thread
	}

	public void cleanup() throws IOException {
		firstClientSocket.close();
		secondClientSocket.close();
	}

	public ServerClientListenerThread client(int order) {
		if (order == 1) {
			return thread1;
		}
		return thread2;
	}

	public ServerClientListenerThread getClientForOrder(int orderToGet) {
		if (orderToGet == 1) {
			return thread1;
		}
		return thread2;
	}

	// creates a thread for the first client to be connected.
	private void manageFirstClientConnected(Socket firstClientSocket) throws IOException {
		thread1 = new ServerClientListenerThread(firstClientSocket, 1);
		new Thread(thread1, "First server thread listener").start();

	}

	// creates a thread for the second client to be connected.
	private void manageSecondClientConnected(Socket secondClientSocket) throws IOException {
		thread2 = new ServerClientListenerThread(secondClientSocket, 2);
		new Thread(thread2, "Second server thread listener").start();
	}

	// called by client thread that receives an "end" notification from
	// the connected client to tell the other thread to end
	public void notifyClosingThread(ServerClientListenerThread clientClosing) throws IOException {
		otherClient(clientClosing).sendMessageToClient("other_client_disonnected");
		cleanup();
	}

	public ServerClientListenerThread otherClient(ServerClientListenerThread otherThanThisThread) {
		if (thread1 == otherThanThisThread) {
			return thread2;
		}
		return thread1;
	}

	public ServerClientListenerThread thisClient(ServerClientListenerThread thisOne) {
		if (thread1 != thisOne) {
			return thread1;
		}
		return thread2;
	}

	public ServerClientListenerThread getThreadFor(int order) {
		if (order == 1) {
			return thread1;
		}
		return thread2;

	}

}
