package capt.sunny.labs.l4;

import java.util.Arrays;

interface HumanInt extends LocationInt {
    void scream();

    void changeDeviceState(Device _device);

    Clothes showWear(String _wearName);

    boolean checkWear(Clothes _clothes);

    void addWear(Clothes _clothes);

    void removeWear(Clothes _clothes);
}

public class Human extends Creature implements HumanInt {
    protected Device[] devices;
    protected Clothes[] clothes;

    protected boolean shout;


    public Human(int _age, String _name, double _height,
                 Device[] _device, Clothes[] _clothes,
                 double[] point) {
        super("human");
        age = _age;
        name = _name;
        height = _height;
        devices = _device;
        clothes = _clothes;
        shout = false;
        try {
            updateLocation(point);
        } catch (ParametersException e) {
            System.out.println(e.getMessage() + "\nОбъект помещен в другую точку");
            double[] newPoint = new double[3];
            for (int i = 0; (i < point.length) & (i < 3); i++) {
                if (i < point.length) {
                    newPoint[i] = point[i];
                } else {
                    newPoint[i] = 0;
                }
            }
            try {
                location.update(newPoint);
            } catch (ParametersException e2) {
                e2.printStackTrace();
            }
        }
    }


    public boolean checkWear(Clothes _clothes) {
        return Arrays.asList(clothes).contains(_clothes);
    }

    public void removeWear(Clothes _clothes) {
        if (!Arrays.asList(clothes).contains(_clothes)) {
            throw new PossessionException(name + " не обладает вещью c названием " + _clothes.toString());
        }
        Clothes[] updatedClothes = new Clothes[clothes.length - 1];
        int newMassIndex = 0;
        for (int oldMassIndex = 0; oldMassIndex < clothes.length; oldMassIndex++) {
            if (clothes[oldMassIndex] != _clothes) {
                updatedClothes[newMassIndex] = clothes[oldMassIndex];
                newMassIndex++;
            }
        }
        clothes = updatedClothes;
    }


    public void scream() {
        //локальный класс
        class Scream {
            public void startShouting() {
                shout = true;
            }

            public void stopShouting() {
                shout = false;
            }
        }

        Scream _scream = new Scream();

        if (shout) {
            _scream.stopShouting();
        } else {
            _scream.startShouting();
        }
    }

    public boolean isShout() {
        return shout;
    }


    public void changeDeviceState(Device _device) {
        boolean flage = true;
        if (Arrays.asList(devices).contains(_device)){
            _device.button();
        }else{
            throw new PossessionException(name + " не оббладает девайсом " + _device.toString() );
        }
    }

    public Clothes showWear(String _wearName) {
        for (int i = 0; i < clothes.length; i++) {
            if (clothes[i].getName() == _wearName) {
                return clothes[i];
            }
        }
        throw new PossessionException(name + " не обладает вещью c названием " + _wearName);
    }

    public void addWear(Clothes _clothes) {
        Clothes[] newClothesPack = new Clothes[clothes.length + 1];
        for (int i = 0; i < clothes.length; i++) {
            newClothesPack[i] = clothes[i];
        }

        newClothesPack[clothes.length] = _clothes;
        this.clothes = newClothesPack;
    }


}
