package capt.sunny.labs.l7;

import capt.sunny.labs.l7.serv.Point;
import capt.sunny.labs.l7.serv.PointInt;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;


interface LocationInt {
    double[] getLocation();

    void updateLocation(double[] _parameters);
}


interface CreatureInt extends LocationInt {
    void die();

    void say(String _str);
}


public class Creature implements CreatureInt, Comparable, Serializable {
    protected String species; //+
    protected boolean isLive; //+
    protected int age; //+
    protected String name; //+
    protected PointInt location; //+
    private double size; //+
    private LocalDateTime creationDate; //+
    private String ownerNick; //+


    public Creature(String _species, int _age, String _name, Point _point, String _ownerNick) {
        creationDate = LocalDateTime.now();
        name = _name;
        age = _age;
        species = _species;
        isLive = true;
        location = _point;
        ownerNick = _ownerNick;
        setSize();
    }

    public Creature(String _species, int _age, String _name, Point _point, String _ownerNick, String _dateTimeString) {
        try {
            creationDate = LocalDateTime.parse(_dateTimeString);
        } catch (IllegalArgumentException e) {
            throw new InvalidParameterException("Creature object cannot be created: wrong time parameter, use pattern like this: " + LocalDateTime.now().toString() + "\n");
        }
        name = _name;
        age = _age;
        species = _species;
        isLive = true;
        location = _point;
        ownerNick = _ownerNick;
        setSize();
    }


    public Creature(JSONObject jsonObject, String _ownerNick) {
        if (_ownerNick == null)
            throw new InvalidParameterException("Creature object cannot be created: owner not found\n");
        try {
            try {
                creationDate = LocalDateTime.parse(jsonObject.get("creationDate").toString());
            } catch (JSONException e) {
                creationDate = LocalDateTime.now();
            } catch (IllegalArgumentException e) {
                throw new InvalidParameterException("Creature object cannot be created: wrong time parameter, use pattern like this: \n" + LocalDateTime.now().toString());
            }
            name = jsonObject.getString("name");
            age = jsonObject.getInt("age");
            species = jsonObject.getString("species");
            isLive = jsonObject.getBoolean("isLive");
            location = new Point(jsonObject.getJSONObject("location"));
            ownerNick = _ownerNick;
            setSize();
            checkParameters();
        } catch (JSONException e) {
            throw new InvalidParameterException("Creature object cannot be created: invalid json structure\n");
        }
    }



    public String getOwnerNick() {
        return ownerNick;
    }

    private void checkParameters() {
        if (age <= 0)
            throw new InvalidParameterException("age must be greater than zero");
        if (name.equals(""))
            throw new InvalidParameterException("name must not be empty");
        if (species.equals(""))
            throw new InvalidParameterException("species must not be empty");
    }

    public String getSpecies() {
        return species;
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


    public double getSize() {
        return size;
    }

    private void setSize() {
        size = isLive ? 1 : 0;
        size = String.valueOf(age).length() +
                name.length() +
                species.length() +
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


//    public String toCSVLine() {
//        return String.format("\"%s\",%s,\"%s\",%b,%s,%s,%s,\"%s\"\n", getCSVQuotes(name), getCSVQuotes(species), isLive, String.valueOf(location.get()[0]), String.valueOf(location.get()[1]), String.valueOf(location.get()[2]), creationDate.toString(), owner);
//    }

    public String toString(boolean needName) {
        String res = String.format("\n\towner: %s\n\tspecies: %s\n\tage: %s\n\tisLive: %s\n\tlocation: %s\n\tsize: %s\n\tcreation date: %s\n", ownerNick, species, String.valueOf(age), String.valueOf(isLive), location.toString(), String.valueOf(size), creationDate.toString());
        res += needName?"\n\tname: " + name:"";
        return res;
    }

    public String getStringForDB() {
        return String.format(" name='%s', owner='%s', size=%f, creation_date='%s', location=array[%f,%f,%f], age=%d, is_live%b, species='%s' ", name, ownerNick, size, creationDate.toString(), location.get()[0], location.get()[1], location.get()[2], age, isLive, species);
    }

    //FIX IT !!!!!!!!!!
    @Override
    public int hashCode() {
        char[] _data = (name + species + creationDate.toString()).toCharArray();
        int n = 1;
        for (int i : _data) {
            if (i == 0) {
                i = -1;
            }
            n += i;
        }
        return (int) (n * (age == 0 ? -2.446 : age));
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
        if ((!name.equals(other.name)) || (age != other.age) || (!species.equals(other.species))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Creature) || o == null)
            return -1;
        else
            return this.getAge() - ((Creature) o).getAge();
    }
}

