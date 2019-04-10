package capt.sunny.labs.l6.serv;

import org.json.JSONObject;

public class Point implements PointInt {
    protected double x;
    protected double y;
    protected double z;

    public Point(double _x, double _y, double _z) {
        x = _x;
        y = _y;
        z = _z;
    }

    public Point(double[] _parameters) {
        x = _parameters[0];
        y = _parameters[1];
        z = _parameters[2];
    }

    public Point(JSONObject jsonObject) {
        x = jsonObject.getDouble("x");
        y = jsonObject.getDouble("y");
        z = jsonObject.getDouble("z");
    }

    public Point() {
        x = 0;
        y = 0;
        z = 0;
    }

    @Override
    public double[] get() {
        return new double[]{x, y, z};
    }

    @Override
    public void update(double[] _parameters) {
        if (_parameters.length != 3) {
        }
        x = _parameters[0];
        y = _parameters[1];
        z = _parameters[2];
    }

    @Override
    public String toString() {
        return String.format("(%s,%s,%s)", String.valueOf(x), String.valueOf(y), String.valueOf(z));
    }

    @Override
    public int hashCode() {
        if (x == 0) {
            x = -1;
        }
        if (y == 0) {
            y = -1;
        }
        if (z == 0) {
            z = -1;
        }
        return (int) (x * y * z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        Point other = (Point) obj;
        if ((x != other.x) || (y != other.y) || (z != other.z)) {
            return false;
        }
        return true;
    }


    public static class Func {
        public static double[] circle(double A, double t) {
            double x = A * Math.sin(t);
            double z = A * Math.cos(t);
            return new double[]{x, z};
        }

        public static double[] spiral(double A, double t) {
            double x = A * Math.exp(-0.05 * t) * Math.sin(t);
            double z = A * Math.exp(-0.05 * t) * Math.cos(t);
            return new double[]{x, z};
        }
    }
}

