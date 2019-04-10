package capt.sunny.labs.l4;

import java.util.ArrayList;
import java.util.List;

interface LusterInt extends LocationInt{
    double getHeight();
    double getRadius();
    void addToHooks(Thing _thing);
    List<ThingInt> checkHooks();
}

public class Luster extends Thing implements LusterInt {
    protected double radius;
    protected double height;
    protected PointInt location;
    protected List<ThingInt> hooks = new ArrayList<ThingInt>();

    public Luster(String _name, double _radius, double _height, Point _location) {
        radius = _radius;
        height = _height;
        location = _location;
        name = "Люстра: " + _name;
        color = "Черный";
    }

    public double getHeight() {
        return height;
    }

    public double getRadius() {
        return radius;
    }

    public double[] getLocation() {
        return location.get();
    }

    public void updateLocation(double[] _parameters) throws ParametersException{
        location.update(_parameters);
    }

    public void addToHooks(Thing _thing) {
        hooks.add(_thing);
    }

    public List<ThingInt> checkHooks() {
        return hooks;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        double p = radius * height;
        return (int) ((p - (int) p) * p);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        Luster other = (Luster) obj;
        if ((radius != other.radius) || (height != other.height)) {
            return false;
        }
        return true;
    }
}
