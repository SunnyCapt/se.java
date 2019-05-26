package capt.sunny.labs.l7;

public class Status {
    private AuthStatus status = AuthStatus.NOT_LOGGED_IN;

    public AuthStatus getStatus() {
        return status;
    }

    public void setStatus(AuthStatus _status) {
        status = _status;
    }

    public boolean is_OK() {
        return status == AuthStatus.OK;
    }

    public boolean is_NOT_LOGGED_IN() {
        return status == AuthStatus.NOT_LOGGED_IN;
    }

    public boolean is_LOGGING() {
        return status == AuthStatus.LOGGING;
    }

    public void do_OK() {
        status = AuthStatus.OK;
    }

    public void do_NOT_LOGGED_IN() {
        status = AuthStatus.NOT_LOGGED_IN;
    }

    public void do_LOGGING() {
        status = AuthStatus.LOGGING;
    }

    public void do_SIGIN_FINISH(){status = AuthStatus.SIGIN_FINISH;}

    public boolean is_SIGIN_FINISH(){return  (status == AuthStatus.SIGIN_FINISH);}
}

enum AuthStatus {
    OK,
    NOT_LOGGED_IN,
    LOGGING,
    SIGIN_FINISH;
}
