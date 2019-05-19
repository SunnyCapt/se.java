package capt.sunny.labs.l7.serv;


public class User {
    private final String nick;
    private final String password;

    public User(String _nick, String _password) {
        nick = _nick;
        password = _password;
    }

    public static User login(String _login, String _password) {
        return new User(_login, _password);
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


