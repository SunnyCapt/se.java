package capt.sunny.labs.l4;

public class PossessionException extends RuntimeException{
    protected static String _message = "Ошибка владения чем-либо";

    public PossessionException(String message){
        super(message);
    }
    public PossessionException(){
        super(_message);
    }
    public PossessionException(String message, Throwable cause){
        super(message, cause);
    }

}
