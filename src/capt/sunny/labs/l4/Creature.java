package capt.sunny.labs.l4;

interface CreatureSoundsInt{
    default void say(String _words) {
        System.out.println(_words);
    }
    default void cry() {
        System.out.println("[PERFORMANCE_INFO]: плачет");
    }
}

interface CreatureInt extends CreatureSoundsInt{
    void die();
}

abstract public class Creature implements CreatureInt, LocationInt{
    protected String type;
    protected boolean isLive;
    protected int age;
    protected String name;
    protected double height;

    protected PointInt location = new Point();

    public Creature(String _type) {
        type = _type;
        isLive = true;
    }

    public void die() {
        isLive = false;
    }

    public void unrealAction() {
        //анонимный класс
        CreatureSoundsInt finishActions = new CreatureSoundsInt() {
            public void cry() {
                System.out.println("[PERFORMANCE_INFO]: он кричал \"мама я тебя люблю я не хочу умирать\"");
            }
        };
        finishActions.say("я не способен на это...");
        finishActions.cry();
        die();
    }

    @Override
    public double[] getLocation() {
        return location.get();
    }

    @Override
    public void updateLocation(double[] _parameters)  throws ParametersException{
        location.update(_parameters);
    }

    @Override
    public String toString() {
        return String.format("commandName: %s; age: %d;", name, age);
    }

    @Override
    public int hashCode() {
        char[] _name = name.toCharArray();
        int n = 1;
        for (int i : _name) {
            if (i == 0) {
                i = -1;
            }
            n *= i;
        }
        return (int) (n * age * height);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        Creature other = (Creature) obj;
        if ((name != other.name) | (height != other.height) | (age != other.age)) {
            return false;
        }
        return true;
    }

}
