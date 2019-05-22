package capt.sunny.labs.l7;


public class User {
    private String nick = null;
    private String token = null;
    private long lastReqTime = 0;

    public User(String _nick, String _token) {
        nick = _nick;
        token = _token;
    }

    public User() {
    }

    public void updateLastReqTime() {
        lastReqTime = System.currentTimeMillis() / 1000L;
    }

    public void updateToken(String _token) {
        token = _token;
    }

    public String getToken() {
        return token;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String _nick) {
        if (nick == null)
            nick = _nick;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if ((obj == null) || (getClass() != obj.getClass()))
            return false;
        User other = (User) obj;
        if (!nick.equals(other.nick))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return (int) nick.chars().mapToDouble(c -> c * 2.27).sum();
    }

    public boolean isTokenValid() {
        return (((System.currentTimeMillis() / 1000L) - lastReqTime) < 150) && token!=null && !token.isEmpty();
    }
}



