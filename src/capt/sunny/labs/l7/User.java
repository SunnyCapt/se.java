package capt.sunny.labs.l7;


import capt.sunny.labs.l7.serv.DataManager;
import capt.sunny.labs.l7.serv.db.DBException;

import javax.security.auth.login.LoginException;

public class User {
    private String nick = null;
    private String token = null;

    public User(String _nick, String _token) {
        nick = _nick;
        token = _token;
    }

    public User(){
    }

    public void updateToken(String _token){
        token = _token;
    }

    public void setNick(String _nick){
        if (nick == null)
            nick = _nick;
    }

    public String getToken(){
        return token;
    }

    public String getNick() {
        return nick;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        User other = (User) obj;
        if (!nick.equals(other.nick)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (int) nick.chars().mapToDouble(c -> c * 2.27).sum();
    }
}



