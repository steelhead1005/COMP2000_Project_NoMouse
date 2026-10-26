public class CapacityExceededException extends Exception {
    public CapacityExceededException(String message) {
        System.out.println("An exception occured on this program:");
        super(message);
    }
}