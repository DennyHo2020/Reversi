


/**
 * Exception for when the user enters too many colors for Mastermind
 * 
 * Correct amount of colors is 4
 * 
 * @author Denny Ho
 */
public class ReversiIllegalLocationException extends Exception{
	public ReversiIllegalLocationException(String message) {
		super(message);
	}
	
	public String toString() {
		return getMessage();
	}
}