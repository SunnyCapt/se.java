package capt.sunny.labs.l6.serv;


import java.io.Serializable;

public interface PointInt extends Updateable, Serializable {
    double[] get();

    String toString();
}
