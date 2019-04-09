import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

import javafx.application.Platform;

/**
 * 
 * @author Ryan Luu and Denny Ho 
 *
 */
public class Server {
	private int port;
	private Consumer<ReversiBoard> board;
	private ReversiView view;
	Socket clientConnection;
	ObjectOutputStream output;
	ObjectInputStream input;

	/**
	 * Constructs the Server
	 * 
	 * @param view  : the ReversiView of the connection
	 * @param port  : connection port
	 * @param board : a lambda meant to be called every time a board is received
	 */
	public Server(ReversiView view, int port, Consumer<ReversiBoard> board) {
		this.port = port;
		this.board = board;
		this.view = view;
	}

	/**
	 * Starts connection by creating new Thread and accepting a Client connection.
	 * 
	 * @throws IOException : Input Output steam exception
	 */
	public void startConnection() throws IOException {
		Thread thread = new Thread() {
			public void run() {
				try {
					ServerSocket server = new ServerSocket(port);
					clientConnection = server.accept();
					output = new ObjectOutputStream(clientConnection.getOutputStream());
					input = new ObjectInputStream(clientConnection.getInputStream());

					// Play First AI turn for computer
					Platform.runLater(() -> {
						if (!view.networkSettings.getHumanOrComputer())
							view.turnAI();
					});

					// Read in ReversiBoards continuously
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
