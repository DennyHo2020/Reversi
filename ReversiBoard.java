import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 
 * @author Denny Ho and Ryan Luu 
 * 
 * ReversiBoard's Purpose is to hold an representation of the Board that is 
 * written out to bytes using Serialization for loading and saving purposes.
 *
 */

public class ReversiBoard implements Serializable{

	private int [][] board; 
	
	/**
	 * constructor using a passed in representation of the current board
	 * 
	 * @param board: the current state of the Reversi Board
	 */
	public ReversiBoard(int[][] board) {
		this.board = board;
	}
	
	/**
	 * Save() writes out the current object to a save_game.dat file
	 * 
	 * @throws Exception: When File could not be saved too.
	 */
	public void save() throws Exception {
		ObjectOutputStream oos = 
			    new ObjectOutputStream(new FileOutputStream("save_game.dat"));
	oos.writeObject(this);
	}
	
	/**
	 * getBorad is a getter for current representation of the board
	
	 * @return : int[][] of the current board
	 */
	public int[][] getBoard() {
		return board;
	}
	
	
	
	
}