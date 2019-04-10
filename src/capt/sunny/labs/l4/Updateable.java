package capt.sunny.labs.l4;

@FunctionalInterface
public interface Updateable {

    void update(double[] _parameters) throws ParametersException;

}
