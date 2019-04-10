package capt.sunny.labs.l4;

interface ClothesInt{
    boolean canRazvevatsya();
    String getColor();
    int getSize();
    void makeLoose();
    boolean isStagger();
}

public class Clothes extends Thing implements ClothesInt{
    protected int size; //м^2
    protected boolean staggers;

    public Clothes(String _name, String _color, int _size) {
        name = _name;
        color = _color;
        staggers = false;
        size = _size;
    }

    public Clothes() {
        name = null;
        size = 0;
        color = null;
        staggers = false;
    }

    public boolean canRazvevatsya() {
        return (size > 47);
    }

    public String getColor() {
        return color;
    }

    public int getSize() {
        return size;
    }

    public void makeLoose() {
        staggers = true;
    }

    public boolean isStagger() {
        return staggers;
    }

    public int hashCode() {
        char[] _name = name.toCharArray();
        char[] _color = color.toCharArray();
        int firstNumber = 1;
        int secondNumber = -1;
        for (int i : _name) {
            if (i == 0) {
                i = -1;
            }
            firstNumber *= i;
        }
        for (int i : _color) {
            if (i == 0) {
                i = -1;
            }
            firstNumber *= i;
        }
        return firstNumber ^ secondNumber ^ size;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        Clothes other = (Clothes) obj;
        if ((name != other.name) || (size != other.size) || (color != other.color)) {
            return false;
        }
        return true;
    }

}
