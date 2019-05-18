package capt.sunny.labs.l7.serv;

public class FileSavingException extends Exception {
    public FileSavingException(String message) {
        super(message);
    }

    public FileSavingException(String message, Throwable cause) {
        super(message, cause);
    }
}
