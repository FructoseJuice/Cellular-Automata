package CAClassPackage;

/**
 * Exception for an Illegal Initial State in Cellular Automatas
 */
public class IllegalInitialStateException extends Exception{

    public IllegalInitialStateException() {
        super("Initial state must only contain 1's, and 0's.");
    }

    public IllegalInitialStateException(String message) {
        super(message);
    }
}
