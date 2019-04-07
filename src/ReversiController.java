import java.util.ArrayList;
import java.util.Collections;

/**
 * ReversiController is the controller for Reversi
 * 
 * @author DennyHo and Ryan Luu
 *
 */
public class ReversiController {
	
	private ReversiModel model;
	
	/**
	 * Constructor for ReversiController sets the model and view
	 * 
	 * @param model : model for Reversi
	 * @param view : view for Reversi
	 */
	public ReversiController(ReversiModel model) {
		this.model = model;
	}
	/**
	 * Human turn places token and captures any opponent pieces.
	 * 
	 * @param row : intended row for placement
	 * @param col : intended column for placement
	 * @throws ReversiIllegalLocationException 
	 */
	public void humanTurn(int row, int col) throws ReversiIllegalLocationException {
		if (row < 0 || row > 7 || col < 0 || col > 7) {
			throw new ReversiIllegalLocationException("Invalid Row or Column Entered");
		}
		getCount(row, col, ReversiModel.W, true);
		model.placeW(row, col);
		model.setCurrentPlayer(ReversiModel.B);
	}
	/**
	 * Computer turn finds a legal space that will result in the most captures and places
	 * its token at that location. If best location ties in value, the first location is chosen.
	 * 
	 * @param color: parameter used to determine which color is played by the Bot
	 */
	public void computerTurn(int color) {
		int currColor;
		int oppColor;
		ArrayList<int []> maxScoreList = new ArrayList<int []>();
		
		if (color == 2) {
			currColor = ReversiModel.B;
			oppColor = ReversiModel.W;
		} else {
			currColor = ReversiModel.W;
			oppColor = ReversiModel.B;
		}
		int maxCount = 0;
		int row = 0;
		int col = 0;
	
		// Search for best legal move
		for (int i = 0; i < ReversiModel.BOARD_DIMENSION; i++) {
			for (int j = 0; j < ReversiModel.BOARD_DIMENSION; j++) {
				int count = getCount(i, j, currColor, false);
				if (count > maxCount) {
					row = i;
					col = j;
					maxCount = count;
				}
			}
		}
		// Looks for Multiple Maxes and adds to List 
		for (int i = 0; i < ReversiModel.BOARD_DIMENSION; i++) {
			for (int j = 0; j < ReversiModel.BOARD_DIMENSION; j++) {
				int count = getCount(i, j, currColor, false);
				if (count == maxCount) {
					maxScoreList.add(new int[]{i,j});
				}
			}
		}
		
		int playRow = 0;
		int playCol = 0;
		
		
		if (maxScoreList.size() == 1) { // If there is only one MAX
			playRow = maxScoreList.get(0)[0];
			playCol = maxScoreList.get(0)[1];
		} else {
			Collections.shuffle(maxScoreList); // Randomize if there are many MAXES 
			playRow = maxScoreList.get(0)[0];
			playCol = maxScoreList.get(0)[1];
		}
			
		// Place piece at best location and capture opponents pieces 
		if (color == 2) {
			getCount(playRow, playCol,currColor, true);
			model.placeB(playRow, playCol);
							model.setCurrentPlayer(oppColor);
		} else {
			getCount(playRow, playCol, currColor, true);
			model.placeW(playRow, playCol);			
			model.setCurrentPlayer(oppColor);
		}
		}

	
	/**
	 * Gets the grid in a string representation
	 * @return the grid
	 */
	public int[][] getGrid() {
		int [][] grid = new int[ReversiModel.BOARD_DIMENSION][ReversiModel.BOARD_DIMENSION];
		
		for (int i = 0; i < ReversiModel.BOARD_DIMENSION; i++) {
			for (int j = 0; j < ReversiModel.BOARD_DIMENSION; j++) {
				grid[i][j] = model.getAtLocation(i, j);
			}
		}
		return grid;
	}
	/**
	 * Updates the model's number of valid moves for current player
	 * @param color : color of the player whose number of valid moves will be updated
	 */
	public void updateValidMoves(int color) {
		int numValidMoves = 0;
		for (int i = 0; i < ReversiModel.BOARD_DIMENSION; i++) {
			for (int j = 0; j < ReversiModel.BOARD_DIMENSION; j++) {
				if (isValidMove(i, j, color)) {
					numValidMoves += 1;
				}
			}
		}
		model.setValidMoves(numValidMoves);
	}
	/**
	 * Gets the number of captures if piece is placed at location
	 * 
	 * @param row : intended row placement
	 * @param col : intended column placement
	 * @param color : color of piece placed
	 * @param flip : whether to capture now
	 * @return the number of captures
	 */
	private int getCount(int row, int col, int color, boolean flip) {
		if(model.getAtLocation(row, col) != ReversiModel.BLANK || !isValidMove(row,col, color)) {
			return 0;
		}
		
		int oppositeColor;
		if (color == 1) {
			oppositeColor = 2;
		}else {
			oppositeColor = 1;
		}
		
		// Search in all directions of location to count captures
		int left = flipDirection(row, col, 0, 1, color, oppositeColor, false); 
		if (left > 0 && flip)
			flipDirection(row, col, 0, 1, color, oppositeColor, true);
		int right = flipDirection(row, col, 0, -1, color, oppositeColor, false); 
		if (right > 0 && flip)
			flipDirection(row, col, 0, -1, color, oppositeColor, true);
		int down = flipDirection(row, col, 1, 0, color, oppositeColor, false);
		if (down > 0 && flip)
			flipDirection(row, col, 1, 0, color, oppositeColor, true);
		int up = flipDirection(row, col, -1, 0, color, oppositeColor, false);
		if (up > 0 && flip) 
			flipDirection(row, col, -1, 0, color, oppositeColor, true);
		int rightDown = flipDirection(row, col, 1, 1, color, oppositeColor, false);
		if (rightDown > 0 && flip)
			flipDirection(row, col, 1, 1, color, oppositeColor, true);
		int rightUp = flipDirection(row, col, -1, 1, color, oppositeColor, false);
		if (rightUp > 0 && flip)
			flipDirection(row, col, -1, 1, color, oppositeColor, true);
		int leftUp = flipDirection(row, col, -1, -1, color, oppositeColor, false);
		if (leftUp > 0 && flip)
			flipDirection(row, col, -1, -1, color, oppositeColor, true);
		int leftDown = flipDirection(row, col, 1, -1, color, oppositeColor, false);
		if (leftDown > 0 && flip)
			flipDirection(row, col, 1, -1, color, oppositeColor, true);
		
		return (left + right + down + up + rightDown + rightUp + leftUp + leftDown);
	}
	/**
	 * Checks if location is a legal move
	 * 
	 * @param row : row of move
	 * @param col : column of move
	 * @param color : color of token to be placed
	 * @return whether location is legal
	 */
	public boolean isValidMove(int row, int col, int color) {
		if (row < 0 || row > 7 || col < 0 || col > 7) {
			return false;
		}
		if (model.getAtLocation(row, col) != ReversiModel.BLANK) {
			return false;
		}
		
		int oppositeColor;
		if (color == 1) {
			oppositeColor = 2;
		}else {
			oppositeColor = 1;
		}
	
		// Search in all directions to see if captures are possible
		boolean left = checkDirection(row, col, 0, 1, color, oppositeColor); 
		boolean right = checkDirection(row, col, 0, -1, color, oppositeColor); 
		boolean down = checkDirection(row, col, 1, 0, color, oppositeColor);
		boolean up = checkDirection(row, col, -1, 0, color, oppositeColor);
		boolean rightDown = checkDirection(row, col, 1, 1, color, oppositeColor);
		boolean rightUp = checkDirection(row, col, -1, 1, color, oppositeColor);
		boolean leftUp = checkDirection(row, col, -1, -1, color, oppositeColor);
		boolean leftDown = checkDirection(row, col, 1, -1, color, oppositeColor);

		return (left || right || down || up || rightDown || rightUp || leftUp || leftDown);
		
	}
	/**
	 * Checks if a direction has any possible captures. 
	 * 
	 * @param row : row of location
	 * @param col : column of location
	 * @param rChange : change in row value
	 * @param cChange : change in column value
	 * @param color : color of token to be placed
	 * @param oppositeColor : opposite color of token
	 * @return whether the location can capture in direction
	 */
	private boolean checkDirection(int row, int col, int rChange, int cChange, int color, int oppositeColor) {
		boolean oppositeFound;
		oppositeFound = false;
		int x = row + rChange;
		int y = col + cChange;
		while (x < ReversiModel.BOARD_DIMENSION && y < ReversiModel.BOARD_DIMENSION && x >= 0 && y >= 0) {
			if (model.getAtLocation(x, y) == oppositeColor) {
				oppositeFound = true;
			}else if (model.getAtLocation(x, y) == ReversiModel.BLANK) {
				break;
			}else if (model.getAtLocation(x, y) == color && oppositeFound == true) {
				return true;
			}else if (model.getAtLocation(x, y) == color && oppositeFound == false){
				break;
			}
			x += rChange;
			y += cChange;
		}
		return false;
	}
	/**
	 * Counts the number of captures possible in one direction 
	 * from a location on the board. Method can also capture pieces.
	 * 
	 * @param row : row of location
	 * @param col : column of location
	 * @param rChange : row change direction
	 * @param cChange : column change direction 
	 * @param color : color of token intended to be placed
	 * @param oppositeColor : opposite color of token
	 * @param flip : whether the program wants to flip now 
	 * @return the number of possible captures 
	 */
	private int flipDirection(int row, int col, int rChange, int cChange, int color, int oppositeColor, boolean flip) {
		boolean oppositeFound;
		oppositeFound = false;
		int count = 0;
		int x = row + rChange;
		int y = col + cChange;
		while (x < ReversiModel.BOARD_DIMENSION && y < ReversiModel.BOARD_DIMENSION && x >= 0 && y >= 0) {
			if (model.getAtLocation(x, y) == oppositeColor) {
				oppositeFound = true;
				if (flip) {
					model.flip(x, y);
				}
				count++;
			}else if (model.getAtLocation(x, y) == ReversiModel.BLANK) {
				count = 0;
				break;
			}else if (model.getAtLocation(x, y) == color && oppositeFound == true) {
				return count;
			}else if (model.getAtLocation(x, y) == color && oppositeFound == false){
				count = 0;
				break;
			}
			x += rChange;
			y += cChange;
		}
		count = 0;
		return count;
	}
	/**
	 * Checks if game has ended. Game ends when board is full or 
	 * no more legal moves are left for both players.
	 * 
	 * @return whether game has ended
	 */
	public boolean isGameOver() {
		boolean emptySpace = false;
		
		// Board is full
		for(int i = 0; i < ReversiModel.BOARD_DIMENSION; i++) {
			for(int j = 0; j < ReversiModel.BOARD_DIMENSION; j++) {
				if(model.getAtLocation(i, j) == ReversiModel.BLANK) {
					emptySpace = true;
				}
			}
		}
		if(!emptySpace) {
			return true;
		}
		
		// No more legal moves
		updateValidMoves(model.getCurrentPlayer());
		if (model.getValidMoves() == 0) {
			if (model.getCurrentPlayer() == 1) {
				updateValidMoves(2);
			}else {
				updateValidMoves(1);
			}
			if (model.getValidMoves() == 0) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Update the score inside the model. Counts number of Black token
	 * and White tokens on the board to get score.
	 */
	public void updateScore() {
		int bCount = 0;
		int wCount = 0;
		// Count black tokens
		for(int i = 0; i < ReversiModel.BOARD_DIMENSION; i++) {
			for(int j = 0; j < ReversiModel.BOARD_DIMENSION; j++) {
				if(model.getAtLocation(i, j) == ReversiModel.B) {
					bCount++;
				}
			}
		}
		// Count white tokens
		for(int i = 0; i < ReversiModel.BOARD_DIMENSION; i++) {
			for(int j = 0; j < ReversiModel.BOARD_DIMENSION; j++) {
				if(model.getAtLocation(i, j) == ReversiModel.W) {
					wCount++;
				}
			}
		}
		model.setBScore(bCount);
		model.setWScore(wCount);
	}
}