import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

import javafx.application.Platform;

public class Client {
	private int port;
	private String ip; 
	private Consumer<ReversiBoard> board;
//	private MyThread thread = new MyThread();
	
	Socket serverConnection;
	ObjectOutputStream output;
	ObjectInputStream input;
	
	public Client(String ip, int port, Consumer<ReversiBoard> board) {
		this.ip = ip;
		this.port = port;
		this.board = board;
	}
	
	public void startConnection() throws IOException {
		
		Thread thread = new Thread() {
		@Override 
		public void run() {
			try {
				serverConnection = new Socket(ip, port);
				output = new ObjectOutputStream(serverConnection.getOutputStream());
				input = new ObjectInputStream(serverConnection.getInputStream());
				
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
		serverConnection.close();
	}
	
	public void send(ReversiBoard board) throws IOException {
		output.writeObject(board);
	}	
}
