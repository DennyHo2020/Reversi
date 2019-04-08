import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.application.*;
import javafx.application.Platform;

/**
 * 
 * @author Ryan Luu and Denny Ho CSC 335: Project 5 Reversi GUI and Progress
 *         Saving 3/26/2019
 * 
 *         ReversiView's Purpose is to serve as the View in the MVC design
 *         pattern for the Reversi Game. The View in this case is a GUI
 *         represented using JavaFx and it implements basic Save and Load
 *         Features
 *
 */
public class ReversiView extends Application implements Observer {
	// JavaFX Fields
	private BorderPane root;
	private TilePane tile;
	private ReversiModel model = new ReversiModel();
	private ReversiController controller = new ReversiController(model);
	private ReversiBoard board = model.getBoardObj();
	private Label score;

	// Networking Fields
	private NetworkSetup networkSettings;
	private boolean connectionEstablished = false;
	private boolean canPlay = true;
	private Server clientConnection;
	private Client serverConnection;
	private boolean isServer;
	private int player;

	/**
	 * Start Launches the Current JavaFx GUI. First checks to see if File can be
	 * loaded and then sets up basic environment for the GUI.
	 * 
	 * @param primaryStage: primaryStage is the stage that JavaFx uses to display
	 *        our ReversiBoard
	 * @throws Exception: When Something Goes Wrong with the Load
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get("save_game.dat")))) {
			ReversiBoard loadData = (ReversiBoard) ois.readObject();
			model.setBoard(loadData.getBoard());
			controller.updateScore();
		} catch (Exception e) {
		}

		Menu FileBar = new Menu("File");
		primaryStage.setTitle("Reversi");
		this.root = new BorderPane();
		this.score = new Label("White: " + model.getWScore() + " " + "Black: " + model.getBScore());
		MenuBar menuBar = new MenuBar();
		MenuItem menuItem = new MenuItem("New Game");
		MenuItem networkOption = new MenuItem("Networked Game");

		// Resets the Board if NewGame is Pressed in Menu
		menuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				resetBoard(); 
			}
		});

		// Pops up Options for NetWorked Game
		networkOption.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				networkSettings = new NetworkSetup(); 
				isServer = networkSettings.getServerOrClient();
				connectionEstablished = true;
				if (isServer) {
					clientConnection = createServer();
					try {
						clientConnection.startConnection();
					} catch (IOException e1) {
					}
				} else {
					serverConnection = createClient();
					try {
						serverConnection.startConnection();
					} catch (IOException e1) {
					}
					canPlay = false;
				}
			}
		});

		FileBar.getItems().add(menuItem);
		FileBar.getItems().add(networkOption);
		menuBar.getMenus().addAll(FileBar);

		tile = new TilePane();
		tile.setStyle("-fx-background-color: green;");
		tile.setPadding(new Insets(8, 8, 8, 8));
		tile.setPrefColumns(8);
		tile.setPrefRows(8);

		int[][] grid = controller.getGrid();

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				tile.getChildren().add(createPane(grid[i][j], i, j));
			}
		}

		root.setBottom(score);
		root.setCenter(tile);
		root.setTop(menuBar);
		Scene scene = new Scene(root);

		model.addObserver(this);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private Server createServer() {
		return new Server(networkSettings.getPort(), board -> {
			Platform.runLater(() -> {
				changeBoard(board);
			});
		});
	}

	private Client createClient() {
		return new Client("127.0.0.1", networkSettings.getPort(), board -> {
			Platform.runLater(() -> {
				changeBoard(board);
			});
		});
	}

	public void changeBoard(ReversiBoard newBoard) {
		model.setBoard(newBoard.getBoard());
		controller.updateScore();
		canPlay = true;
		model.endTurn();
	}

	/**
	 * StackPane creates the indiviudal StackPane's that Populate the Board in the
	 * GUI
	 * 
	 * @param color: Can be Either 0,1,2 and represents what's currently a Blank,
	 *        Black, or White Piece on the board
	 * @param row: Is the Row at which the piece is located
	 * @param col: Is the Col at which the piece is located
	 * @return a StackPane to be placed on the grid with its corresponding
	 *         information
	 */
	private StackPane createPane(int color, int row, int col) {
		// Create Circle
		Circle circle = new Circle(20);
		if (color == 1)
			circle.setFill(Color.WHITE);
		else if (color == 1)
			circle.setFill(Color.BLACK);
		else if (color == 0)
			circle.setFill(Color.TRANSPARENT);

		// Create StackPane with Circle
		StackPane pane = new StackPane(circle);
		pane.setStyle("-fx-background-color: green;");
		pane.setStyle("-fx-border-color: black;");
		pane.setPadding(new Insets(2, 2, 2, 2));

		// Plays the Game if StackPane is clicked on
		pane.setOnMousePressed((MouseEvent event) -> {
			try {
				if (connectionEstablished && canPlay) {
					if (isServer) {
						if (networkPlay(row, col, 1)) {
							canPlay = false;
							clientConnection.send(board);
						}
					} else {
						if (networkPlay(row, col, 2)) {
							canPlay = false;
							serverConnection.send(board);
						}
					}
				} else if (canPlay){
					play(row, col);
					System.out.print("here2");
				}

			} catch (ReversiIllegalLocationException e) {
				e.printStackTrace();
			} catch (IOException e) {
			}
		});
		return pane;
	}

	/**
	 * play is where the game is actually played. Checks to see if the game is over
	 * and then alternates between player and computer till there is a winner,
	 * loser, or tie. If not valid moves are able to be made on one side, that turn
	 * is skipped and automatically given to the opposing party.
	 * 
	 * @param row : is the current row location of the player click
	 * @param col : is the current col location of the player click
	 * @throws ReversiIllegalLocationException : If an illegal Location is chosen
	 *                                         and can't be placed
	 */
	private boolean networkPlay(int row, int col, int player) throws ReversiIllegalLocationException {
		boolean exitFlag = false;
		boolean success = false;
		if (controller.isGameOver()) {
			exitFlag = true;
			gameOverfunction();
		}
		else {
			System.out.print("Yes!" + player);
			controller.updateScore();
			controller.updateValidMoves(model.getCurrentPlayer());
			if (model.getValidMoves() > 0) {
				boolean legalMove = false;
				int r = row;
				int c = col;
				// Check if move is legal
				if (controller.isValidMove(row, col, player)) {
					success = true;
					controller.humanTurn(r, c, player);
					controller.updateScore();
					//controller.updateValidMoves(model.getCurrentPlayer());
				}
				else
					return false;
			}

		}
		model.endTurn();
		board = model.getBoardObj();
		return success;
	}

	/**
	 * play is where the game is actually played. Checks to see if the game is over
	 * and then alternates between player and computer till there is a winner,
	 * loser, or tie. If not valid moves are able to be made on one side, that turn
	 * is skipped and automatically given to the opposing party.
	 * 
	 * @param row : is the current row location of the player click
	 * @param col : is the current col location of the player click
	 * @throws ReversiIllegalLocationException : If an illegal Location is chosen
	 *                                         and can't be placed
	 */
	private void play(int row, int col) throws ReversiIllegalLocationException {
		boolean exitFlag = false;
		if (controller.isGameOver()) {
			exitFlag = true;
			gameOverfunction();
		}

		else {
			controller.updateScore();
			controller.updateValidMoves(model.getCurrentPlayer());
			if (model.getCurrentPlayer() == ReversiModel.W) {
				if (model.getValidMoves() > 0) {
					boolean legalMove = false;
					int r = row;
					int c = col;
					// Check if move is legal
					if (controller.isValidMove(row, col, ReversiModel.W)) {
						controller.humanTurn(r, c, 1);
						controller.updateScore();
						controller.updateValidMoves(model.getCurrentPlayer());
						// Checks to see if there are still legal moves to be made by Player
						if (model.getValidMoves() <= 0)
							model.setCurrentPlayer(ReversiModel.W);
						else {
							controller.computerTurn(2);
							controller.updateScore();
							controller.updateValidMoves(model.getCurrentPlayer());
							while (model.getValidMoves() <= 0 && !controller.isGameOver()) {
								controller.computerTurn(2);
								controller.updateScore();
								controller.updateValidMoves(model.getCurrentPlayer());
							}
							if (controller.isGameOver() && exitFlag == false) {
								exitFlag = true;
								gameOverfunction();
							}
						}
					}
				}
				if (controller.isGameOver() && exitFlag == false) {
					exitFlag = true;
					gameOverfunction();
				}
			}
		}
		model.endTurn();
		board = model.getBoardObj();
	}
	
	/**
	 * update's purpose is to let the GUI know whenever there is a change in the
	 * model by showing the changes on the JavaFX GUI. Everytime there is a change,
	 * an attempt to save the changed state of the ReversiBoard will be made.
	 * 
	 * @param o: Is the Oberservable default parameter that allows us to communicate
	 *        with the model
	 * @param arg: Is a new instance of the ReversiBoard Class that we create every
	 *        time a change is made.
	 */
	@Override
	public void update(Observable o, Object arg) {
		try {
			((ReversiBoard) arg).save(); // Saves the File
		} catch (Exception e) {
			System.out.println("Couldn't Save: " + e.getMessage());
		}
		tile.getChildren().clear();
		int[][] grid = controller.getGrid();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				tile.getChildren().add(createPane(grid[i][j], i, j));
			}
		}
		// Updates Score
		this.score = new Label("White: " + model.getWScore() + " " + "Black: " + model.getBScore());
		this.root.setBottom(score);

	}
	
	/**
	 * resetBoard resets the Board once invoked by creating a brand new model and
	 * controller and then deleting the current SavaData.
	 */
	void resetBoard() {
		this.model = new ReversiModel();
		this.controller = new ReversiController(this.model);
		model.addObserver(this);
		tile.getChildren().clear();
		int[][] grid = controller.getGrid();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				tile.getChildren().add(createPane(grid[i][j], i, j));
			}
		}
		this.score = new Label("White: " + model.getWScore() + " " + "Black: " + model.getBScore());
		this.root.setBottom(score);
		deleteSaveData();
	}

	/**
	 * gameOverFunction displays the PopUp Message at the end of the Game with the
	 * relevant winning message
	 */
	private void gameOverfunction() {
		Alert endScreen = new Alert(AlertType.CONFIRMATION);
		endScreen.setTitle("GameOver");
		endScreen.setHeaderText(
				"And the Winner is with a score of White: " + model.getWScore() + " Black: " + model.getBScore());
		ButtonType buttonTypeCancel = new ButtonType("Alright", ButtonData.CANCEL_CLOSE);
		endScreen.getButtonTypes().setAll(buttonTypeCancel);

		// Blacks win
		if (model.getBScore() > model.getWScore()) {
			endScreen.setContentText("BLACKS. Computer Wins!");
			Optional<ButtonType> result = endScreen.showAndWait();
			// Whites win
		} else if (model.getWScore() > model.getBScore()) {
			endScreen.setContentText("WHITES. You Win!");
			Optional<ButtonType> result = endScreen.showAndWait();
			// Tie Game
		} else if (model.getWScore() == model.getBScore()) {
			endScreen.setContentText("TIE. Everyone is a Winner and Loser!");
			Optional<ButtonType> result = endScreen.showAndWait();
		}
		deleteSaveData();

	}

	/**
	 * deleteSaveData Looks for the file in the directory and deletes it
	 */
	private void deleteSaveData() {
		File file = new File("save_game.dat");
		if (file.exists()) {
			file.delete();
		}
	}
}
