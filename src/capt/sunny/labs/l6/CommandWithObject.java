package capt.sunny.labs.l6;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


public class CommandWithObject<T> implements Serializable {

    protected String command;

    public String getCommand() {
        return command;
    }

    public T getObj() {
        return obj;
    }

    protected double objClassVersion;
    T obj = null;

    public double getObjClassVersion() {
        return objClassVersion;
    }

    private CommandWithObject(String _command, T _obj) {
        command = _command;
        obj = _obj;
    }

    public CommandWithObject(String _command, Class<T> clazz) {
        command = _command;
        try {
            Field field  = clazz.getDeclaredField("version");
            field.setAccessible(true);
            objClassVersion = (double) field.get(clazz);
            String prCommand = command.replace('\n', ' ').replace('\r', ' ').trim();
            if (prCommand.startsWith("insert")) {
                obj = clazz.getConstructor(new Class[]{JSONObject.class})
                        .newInstance(new JSONObject(getCommand(_command)[2]).getJSONObject("element"));
            } else if (prCommand.startsWith("add_if_min"))
                obj = clazz.getConstructor(new Class[]{JSONObject.class})
                        .newInstance(new JSONObject(getCommand(_command)[1]).getJSONObject("element"));
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException
                |InvocationTargetException | InstantiationException e) {
            System.out.println(e.getMessage());
        }


    }

    public static String[] getCommand(String commandLine) {
        int indexOfSecondParameter = -1;
        int indexOfFirstParameter = -1;
        boolean i = true;
        char preChar = ' ';
        char c;
        for (int n = 0; n < commandLine.length(); n++) {
            c = commandLine.charAt(n);
            if ((indexOfSecondParameter == -1) && preChar == '}' && c == '{' && i)
                indexOfSecondParameter = n;
            if (((indexOfFirstParameter == -1) && (c == '{')) && i)
                indexOfFirstParameter = n;
            if ((indexOfFirstParameter != -1) && (indexOfSecondParameter != -1))
                break;
            if (c == '"')
                i = !i;
            preChar = c;
        }
        return new String[]{
                indexOfFirstParameter == -1 ? commandLine.replace(" ", "") : commandLine.substring(0, indexOfFirstParameter).replace(" ", ""),
                indexOfFirstParameter == -1 ? null : indexOfSecondParameter == -1 ? commandLine.substring(indexOfFirstParameter) : commandLine.substring(indexOfFirstParameter, indexOfSecondParameter),
                indexOfSecondParameter == -1 ? null : commandLine.substring(indexOfSecondParameter)
        };
    }

}

















