package capt.sunny.labs.l4;

interface DeviceInt{
    State checkWind();
    void button();
}

public class Device extends Thing implements DeviceInt{
    protected State state;
    protected DeviceType type;

    public Device(String _name, DeviceType _type) {
        name = _name;
        type = _type;
        state = State.OFF;
    }

    public State checkWind() {
        return state;
    }

    public void button(){
        if (state == State.OFF) {
            state = State.ON;
        } else {
            state = State.OFF;
        }

    }

    @Override
    public int hashCode() {
        char[] _name = name.toCharArray();
        char[] _type = type.name().toCharArray();
        int firstNumber = 1;
        int secondNumber = -1;
        for (int i : _name) {
            if (i == 0) {
                i = -1;
            }
            firstNumber *= i;
        }
        for (int i : _type) {
            if (i == 0) {
                i = -1;
            }
            firstNumber *= i;
        }
        return firstNumber ^ secondNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        Device other = (Device) obj;
        if ((name != other.name) || (type != other.type)) {
            return false;
        }
        return true;
    }

}
