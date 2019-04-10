package capt.sunny.labs.l4;

public class ParametersException extends Exception {
    private static String _message = "Ошибка параметров";

    public ParametersException(String message) {
        super(message);
    }

    public ParametersException() {
        super(_message);
    }

    public ParametersException(String message, Throwable cause) {
        super(message, cause);
    }

}
