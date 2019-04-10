package capt.sunny.labs.l4;

public enum State {
    ON("Всё работает прекрасно"),
    OFF("Выключено, а так вроде бы работает...");

    String info;

    State(String _info) {
        info = _info;
    }

}
