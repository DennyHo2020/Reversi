import java.util.Observable;

/**
 * ReversiModel holds game data for Reversi.
 * 
 * Game data includes the state of the board, the scores, the current player, and legal moves.
 * 
 * @author DennyHo and Ryan Luu
 *
 */
public class ReversiModel extends Observable {
	public static int BLANK = 0;
	public static int W = 1;
	public static int B = 2;

	public static int BOARD_DIMENSION = 8;	
	private int[][] board;
	
	private int validMoves = 4;
	private int currentPlayer = 1;
	private int wScore = 2;
	private int bScore = 2;
	
	/**
	 * Constructor that uses a preset board.
	 * 
	 * Meant for testing.
	 * 
	 * @param board : preset board that is an 2D array of ints
	 */
	public ReversiModel(int[][] board) {
		this.board = board;
	}
	/**
	 * Constructor for ReversiModel
	 *
	 * Initializes the board and sets the first four tokens.
	 */
	public ReversiModel() {
		board = new int[BOARD_DIMENSION][BOARD_DIMENSION];
		board[3][3] = W; 
		board[3][4] = B; 
		board[4][3] = B; 
		board[4][4] = W; 
	}
	/**
	 * Places a white token on the board at given row col
	 * 
	 * @param row : row for token to be placed
	 * @param col : col for token to be placed
	 * @throws ReversiIllegalLocationException : exception for off board placement
	 */
	public void placeW(int row, int col) {
		if (!(row < 0 || row > BOARD_DIMENSION-1 || col < 0 || col > BOARD_DIMENSION-1)) {
			board[row][col] = W;
		}
//		setChanged();
//        notifyObservers(new ReversiBoard(board)); 
	}
	/**
	 * Places a Black token on the board at given row col
	 * 
	 * @param row : row for token to be placed
	 * @param col : col for token to be placed
	 * @throws ReversiIllegalLocationException : exception for off board placement
	 */	
	public void placeB(int row, int col) {
		if (!(row < 0 || row > BOARD_DIMENSION-1 || col < 0 || col > BOARD_DIMENSION-1)) {
			board[row][col] = B;
		}
//		setChanged();
//        notifyObservers(new ReversiBoard(board)); 		
	}
	/**
	 * Flips the current token at location to its opposite color
	 * 
	 * @param row : row token is placed
	 * @param col : col token is placed
	 */
	public void flip(int row, int col) {
		if (!(row < 0 || row > BOARD_DIMENSION-1 || col < 0 || col > BOARD_DIMENSION-1)) {
			if (board[row][col] == B) {
				board[row][col] = W;
			}
			else if (board[row][col] == W) {
				board[row][col] = B;
			}
		}
//		setChanged();
//        notifyObservers(new ReversiBoard(board)); 
	}
	/**
	 * Gets the token color at a given on the board
	 * 
	 * @param row : row token is placed
	 * @param col : col token is placed
	 * @return the color of token or 0 if no token at location
	 * @throws ReversiIllegalLocationException : exception for off board location
	 */
	public int getAtLocation(int row, int col) {
		return board[row][col];
	}
	/**
	 * Getter for the number of legal moves on the board for current player
	 * @return number of legal moves
	 */
	public int getValidMoves() {
		return validMoves;
	}
	/**
	 * Setter for the number of legal moves on the board for current player
	 * @param numValidMoves : number of legal moves
	 */
	public void setValidMoves(int numValidMoves) {
		this.validMoves = numValidMoves;
	}
	/**
	 * Getter for the current player
	 * @return the color of the current player
	 */
	public int getCurrentPlayer() {
		return currentPlayer;
	}
	/**
	 * Setter for the current player
	 * @param currentPlayer : color of the current player
	 */
	public void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	/**
	 * Getter for White player's score
	 * @return the White players score
	 */
	public int getWScore() {
		return wScore;
	}
	/**
	 * Setter for White player's score
	 * @param wScore : the White player's score
	 */
	public void setWScore(int wScore) {
		this.wScore = wScore;
//		setChanged();
//        notifyObservers(new ReversiBoard(board)); 
	}
	/**
	 * Getter for Black player's score
	 * @return the Black players score
	 */
	public int getBScore() {
		return bScore;
	}
	/**
	 * Setter for Black player's score
	 * @param bScore : the Black player's score
	 */
	public void setBScore(int bScore) {
		this.bScore = bScore;
//		setChanged();
//        notifyObservers(new ReversiBoard(board)); 
	}
	
	/**
	 * Setter for Loading a Previously Played Board
	 * @param loadBoard : A Previously Played Board
	 */
	public void setBoard(int[][] loadBoard) {
		this.board = loadBoard;
	}
	
	public ReversiBoard getBoardObj() {
		return new ReversiBoard(board);
	}
	
	public void endTurn() {
		setChanged();
        notifyObservers(new ReversiBoard(board)); 
	}
	

}
