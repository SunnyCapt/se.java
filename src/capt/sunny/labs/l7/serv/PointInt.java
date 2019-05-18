package capt.sunny.labs.l7.serv;


import capt.sunny.labs.l7.serv.Updateable;

import java.io.Serializable;

public interface PointInt extends Updateable, Serializable {
    double[] get();

    String toString();
}
