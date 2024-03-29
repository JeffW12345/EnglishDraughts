// Launches the server and two clients locally. Used for testing and debugging - not submitted as part of the project.
// Creates three threads. First creates a server thread, then creates a thread for each client. 
// Does the equivalent of typing javac *.java, followed by java Server, followed by java Client twice (in separate browsers). 

import java.io.IOException;

public class RunServerAndClients implements Runnable {

	public static void main(String[] args) throws IOException {
		RunServerAndClients serverThreadStarter = new RunServerAndClients();
		var t = new Thread(serverThreadStarter, "server starter");
		t.start();
		ClientThread first = new ClientThread();
		first.start("client 1 " + (int) (1000 * Math.random()));
		ClientThread second = new ClientThread();
		second.start("client 2 " + (int) (1000 * Math.random()));
	}

	@Override
	public void run() {
		try {
			new Server();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
