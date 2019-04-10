package capt.sunny.labs.l4;

interface ThingInt {
    String getName();
}

abstract public class Thing implements ThingInt {
    protected String name;
    protected String color;

    @Override
    public String toString() {
        return color + " " + name;
    }

    @Override
    public String getName() {
        return name;
    }
}