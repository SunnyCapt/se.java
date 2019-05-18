package capt.sunny.labs.l7;

import java.security.InvalidParameterException;

public class RequestException extends InvalidParameterException {
    public RequestException(String message) {
        super(message);
    }
}
