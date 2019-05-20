package capt.sunny.labs.l5;

import org.json.JSONException;
import org.json.JSONObject;


import java.security.InvalidParameterException;

interface LocationInt {
    double[] getLocation();
    void updateLocation(double[] _parameters);
}

interface CreatureInt extends LocationInt {
    void die();
    void say(String _str);
}

public class Creature implements CreatureInt{
    protected String type;
    protected boolean isLive;
    protected int age;
    protected String name;
    protected double height;
    protected PointInt location;

    public Creature(String _type, int _age, String _name, double _height, Point _point) {
        name = _name;
        age = _age;
        height = _height;
        type = _type;
        isLive = true;
        location = _point;
    }

    public Creature(JSONObject jsonObject) {
        try {
            name = jsonObject.getString("commandName");
            age = jsonObject.getInt("age");
            height = jsonObject.getDouble("height");
            type = jsonObject.getString("species");
            isLive = jsonObject.getBoolean("isLive");
            location = new Point(jsonObject.getJSONObject("location"));
            if (age<=0)
                throw new InvalidParameterException("age must be greater than zero");
            if(height<=0)
                throw new InvalidParameterException("height must be greater than zero");
            if (name.equals(""))
                throw new InvalidParameterException("commandName must not be empty");
            if (type.equals(""))
                throw new InvalidParameterException("species must not be empty");
        } catch (JSONException e) {
            throw new InvalidParameterException("Creature object cannot be created: invalid json structure\n");
        }
    }

    public Creature(String[] line) {
        if (line.length!=9){
            throw new InvalidParameterException("Number of objects must be 9");
        }/*
        "key","commandName","age","height","species","isLive","x","y","z"
        */
        try {
            name = line[1];
            age = Integer.valueOf(line[2]);
            height = Double.valueOf(line[3]);
            type = line[4];
            isLive = Boolean.valueOf(line[5]);
            location = new Point(Double.valueOf(line[6]), Double.valueOf(line[7]), Double.valueOf(line[8]));
            if (age<=0)
                throw new InvalidParameterException("age must be greater than zero");
            if(height<=0)
                throw new InvalidParameterException("height must be greater than zero");
            if (name.equals(""))
                throw new InvalidParameterException("commandName must not be empty");
            if (type.equals(""))
                throw new InvalidParameterException("species must not be empty");
        }catch (Exception e){
            throw new InvalidParameterException("Object parameters are wrong: " + e.getMessage());
        }

    }


    public int compareTo(Creature otherCreature) {
        if (this.hashCode() == otherCreature.hashCode()) {
            return 0;
        } else {
            return (this.age < otherCreature.age ? -1 : 1);
        }
    }

    @Override
    public void say(String _str) {
        System.out.println(String.format("%s said: %s ", this.name, _str));
    }


    @Override
    public void die() {
        isLive = false;
    }

    @Override
    public double[] getLocation() {
        return location.get();
    }

    @Override
    public void updateLocation(double[] _parameters) {
        location.update(_parameters);
    }

    @Override
    public String toString() {
        return String.format("commandName: %s; age: %d;", name, age);
    }

    @Override
    public int hashCode() {
        char[] _data = (name + type).toCharArray();
        int n = 1;
        for (int i : _data) {
            if (i == 0) {
                i = -1;
            }
            n += i;
        }
        return (int) (n * (age == 0 ? -2.446 : age) * (height == 0 ? -373.67 : age));
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
        if ((name != other.name) | (height != other.height) | (age != other.age) | (type != other.type)) {
            return false;
        }
        return true;
    }

}

