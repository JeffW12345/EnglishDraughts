
// Creates an instance of ClientThread (which implements Runnable) and starts
// a thread relating to that object.

public class Client {
	public static void main(String[] args) {
		var client = new ClientThread();
		client.start("Client ref: " + (int) (10000 * Math.random()));
	}
}
