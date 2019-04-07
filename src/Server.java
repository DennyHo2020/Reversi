import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server {
	private int port;
	private Consumer<ReversiBoard> board;
	Socket clientConnection;
	ObjectOutputStream output;
	ObjectInputStream input;
	//private MyThread thread = new MyThread();
	
	public Server(int port, Consumer<ReversiBoard> board) {
		this.port = port;
		this.board = board;
	}
	

	public void startConnection() throws IOException {
		//private Socket clientConnection;
		//private ObjectOutputStream output;
		//private ObjectInputStream input;
		Thread thread = new Thread() {
		public void run() {
			try {
				ServerSocket server = new ServerSocket(port);
				clientConnection = server.accept();
				output = new ObjectOutputStream(clientConnection.getOutputStream());
				input = new ObjectInputStream(clientConnection.getInputStream());
				while (true) {
					ReversiBoard received = (ReversiBoard) input.readObject();
					board.accept(received);
				}
				
			} catch (Exception e) {
				
			}
		}
		};
		thread.start();
	}
	
	public void endConnection() throws IOException {
		clientConnection.close();
	}
	
	public void send(ReversiBoard board) throws IOException {
		output.writeObject(board);
	}


	
//	private class MyThread extends Thread {
//		private Socket clientConnection;
//		private ObjectOutputStream output;
//		private ObjectInputStream input;
//		
//		@Override 
//		public void run() {
//			try {
//				ServerSocket server = new ServerSocket(port);
//				clientConnection = server.accept();
//				output = new ObjectOutputStream(clientConnection.getOutputStream());
//				input = new ObjectInputStream(clientConnection.getInputStream());
//				while (true) {
//					ReversiBoard received = (ReversiBoard) input.readObject();
//					board.accept(received);
//				}
//				
//			} catch (Exception e) {
//				
//			}
//		}
//		
//	}
	
	
}
