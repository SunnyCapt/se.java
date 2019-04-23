package capt.sunny.labs.l6;

import capt.sunny.labs.l6.serv.Point;
import capt.sunny.labs.l6.serv.PointInt;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Date;

import static capt.sunny.labs.l6.IOTools.getCSVQuotes;

interface LocationInt {
    double[] getLocation();

    void updateLocation(double[] _parameters);
}

interface CreatureInt extends LocationInt {
    void die();
    void say(String _str);
}

public class Creature implements CreatureInt, Comparable , Serializable {
    private static double version = 1.0;
    protected String type;
    protected boolean isLive;
    protected int age;
    protected String name;
    protected double height;
    protected PointInt location;
    private double size;
    private Date creationDate;

    public Creature(String _type, int _age, String _name, double _height, Point _point) {
        name = _name;
        age = _age;
        height = _height;
        type = _type;
        isLive = true;
        location = _point;
        creationDate = new Date();
        setSize();
    }


    public Creature(String _type, int _age, String _name, double _height, Point _point, String _GMTString) {
        try {
            creationDate = new Date(_GMTString);
        } catch (IllegalArgumentException e) {
            throw new InvalidParameterException("Creature object cannot be created: wrong time parameter, use pattern like this: " + new Date().toGMTString() + "\n");
        }
        name = _name;
        age = _age;
        height = _height;
        type = _type;
        isLive = true;
        location = _point;
        setSize();
    }

    public Creature(JSONObject jsonObject) {
        try {
            jsonObject = jsonObject.getJSONObject("element");
            try {
                creationDate = new Date(jsonObject.get("creationDate").toString());
            } catch (JSONException e) {
                creationDate = new Date();
            } catch (IllegalArgumentException e) {
                throw new InvalidParameterException("Creature object cannot be created: wrong time parameter, use pattern like this: \n" + new Date().toGMTString());
            }
            name = jsonObject.getString("name");
            age = jsonObject.getInt("age");
            height = jsonObject.getDouble("height");
            type = jsonObject.getString("type");
            isLive = jsonObject.getBoolean("isLive");
            location = new Point(jsonObject.getJSONObject("location"));
            setSize();
            checkParameters();
        } catch (JSONException e) {
            throw new InvalidParameterException("Creature object cannot be created: invalid json structure\n");
        }
    }


    public Creature(String[] line) {
        if (line.length != 10 && line.length != 9) {
            throw new InvalidParameterException("Number of objects must be 9");
        }/*
        "key","name","age","height","type","isLive","x","y","z"
        */
        try {
            name = line[1];
            age = Integer.valueOf(line[2]);
            height = Double.valueOf(line[3]);
            type = line[4];
            isLive = Boolean.valueOf(line[5]);
            location = new Point(Double.valueOf(line[6]), Double.valueOf(line[7]), Double.valueOf(line[8]));
            setSize();
            checkParameters();
        } catch (Exception e) {
            throw new InvalidParameterException("Object parameters are wrong: " + e.getMessage());
        }
        if (line.length == 10 && line[9] != null) {
            try {
                creationDate = new Date(line[9]);
            } catch (IllegalArgumentException e) {
                throw new InvalidParameterException("Creature object cannot be created: wrong time parameter, use pattern like this: \n" + new Date().toGMTString());
            } catch (JSONException e) {
                creationDate = new Date();
            }
        } else {
            creationDate = new Date();
        }

    }

    public static double getVersion() {
        return version;
    }

    private void checkParameters() {
        if (age <= 0)
            throw new InvalidParameterException("age must be greater than zero");
        if (height <= 0)
            throw new InvalidParameterException("height must be greater than zero");
        if (name.equals(""))
            throw new InvalidParameterException("name must not be empty");
        if (type.equals(""))
            throw new InvalidParameterException("type must not be empty");
    }

    public String getType() {
        return type;
    }

    public boolean isLive() {
        return isLive;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public double getHeight() {
        return height;
    }

    public double getSize() {
        return size;
    }

    private void setSize() {
        size = isLive ? 1 : 0;
        size = String.valueOf(height).length() +
                String.valueOf(age).length() +
                name.length() +
                type.length() +
                String.valueOf(location.get()[0]).length() +
                String.valueOf(location.get()[1]).length() +
                String.valueOf(location.get()[2]).length();
        size *= 8;
    }

//    public int compareTo(Creature otherCreature) {
//        if (this.hashCode() == otherCreature.hashCode()) {
//            return 0;
//        } else {
//            return (this.size < otherCreature.size ? -1 : 1);
//        }
//    }

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


    public String toCSVLine() {
        return String.format("\"%s\",%d,%s,\"%s\",%b,%s,%s,%s,\"%s\"\n", getCSVQuotes(name), age, String.valueOf(height), getCSVQuotes(type), isLive, String.valueOf(location.get()[0]), String.valueOf(location.get()[1]), String.valueOf(location.get()[2]), creationDate.toGMTString());
    }

    public String toString() {
        return String.format("\n\ttype: %s\n\tname: %s\n\tage: %s\n\thieght: %s\n\tisLive: %s\n\tlocation: %s\n\tsize: %s\n\tcreation date: %s\n", type, name, String.valueOf(age), String.valueOf(height), String.valueOf(isLive), location.toString(), String.valueOf(size), creationDate.toInstant());
    }

    @Override
    public int hashCode() {
        char[] _data = (name + type + creationDate.toGMTString()).toCharArray();
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

    @Override
    public int compareTo(Object o) {
        if (! (o instanceof Creature) || o == null)
            return -1;
        else
            return this.getAge() - ((Creature)o).getAge();
    }
}

