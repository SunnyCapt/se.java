package capt.sunny.labs.l6.serv;

import java.security.InvalidParameterException;

public class ClientRequestException extends InvalidParameterException {
    public ClientRequestException(String message) {
        super(message);
    }
}
