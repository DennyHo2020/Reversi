import javafx.application.Application;

/**
 * Reversi
 * 
 * Reversi is a game where one player captures the opponents
 * pieces by surrounding their opponents pieces in a straight line.
 * 
 * This class instantiates the model, view, and controller and runs the game.
 * 
 * @author DennyHo and Ryan Luu
 *
 */
public class Reversi {
	/**
	 * Main starts the game of Reversi
	 * 
	 * @param args : user input
	 */
	public static void main(String[] args){
		Application.launch(ReversiView.class, args);
	}
}