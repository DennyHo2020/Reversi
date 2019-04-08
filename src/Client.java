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

	Socket serverConnection;
	ObjectOutputStream output;
	ObjectInputStream input;

	/**
	 * Constructs the Client
	 * 
	 * @param ip : the ip address 
	 * @param port  : connection port
	 * @param board : a lambda meant to be called every time a board is received
	 */
	public Client(String ip, int port, Consumer<ReversiBoard> board) {
		this.ip = ip;
		this.port = port;
		this.board = board;
	}

	/**
	 * Starts connection by creating new Thread and connecting to Server.
	 * 
	 * @throws IOException : Input Output steam exception
	 */
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

	/**
	 * Sends the board to the output stream
	 * 
	 * @param board : ReversiBoard that had been updated by a move
	 * @throws IOException : Input/Output steam exception
	 */
	public void send(ReversiBoard board) throws IOException {
		output.writeObject(board);
	}
}
