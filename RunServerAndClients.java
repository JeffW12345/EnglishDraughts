
import java.io.IOException;

public class RunServerAndClients implements Runnable {
	public static void main(String[] args) throws IOException {

		var serverThreadStarter = new RunServerAndClients();
		var t = new Thread(serverThreadStarter, "server starter");
		t.start();

		var first = new ClientThread();
		first.start("client " + (int) (1000 * Math.random()));
		var second = new ClientThread();
		second.start("client " + (int) (1000 * Math.random()));
	}

	@Override
	public void run() {
		try {
			new Server();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Tested 2");
	}
}
