package capt.sunny.labs.l7;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.InvalidParameterException;

/**
 * @version 1.0
 */


public class Command implements Serializable {

    protected String name;
    protected String firstParameter;
    protected String secondParameter;
    protected Creature object;
    private String token;

    public String getToken() {
        return token;
    }

    public String getUserName() {
        return userName;
    }

    private String userName;

    public Command(String _name, String _firstParameters, String _secondParameter, Creature _object, String _userName, String _token) throws InvalidParameterException {
        if (!Commands.check(_name))
            throw new InvalidParameterException("\nUnknown command\n");
        name = _name;
        firstParameter = _firstParameters;
        secondParameter = _secondParameter;
        object = _object;
        token = _token;
        userName = _userName;
    }

    public String getName() {
        return name;
    }

    public String getFirstParameter() {
        return firstParameter;
    }

    public String getSecondParameter() {
        return secondParameter;
    }

    public Creature getObject() {
        return object;
    }


//    public void deleteEmpty() {
//        srcFile.forEach(e -> {
//            if (Arrays.asList(e).size() == 1 && Arrays.asList(e).get(0) == "") srcFile.remove(e);
//        });
//    }

    @Override
    public String toString() {
        return String.format("\nCommand: %s\nFirst Parameter: %s\nSecond Parameter: %s\nObject: %s\n\n", this.name, this.firstParameter, this.secondParameter, this.object);
    }

}

class CommandParser {
    protected static Command parse(String commandName, String rawFirstParameter, String rawSecondParameter, User user) {
        if (!Commands.check(commandName))
            throw new InvalidParameterException("\nUnknown command\n");
        String[] commandParameters = Commands.valueOf(commandName.toUpperCase()).getParameterNames();
        if (commandParameters.length != (rawFirstParameter == null ? 0 : 1) + (rawSecondParameter == null ? 0 : 1)) {
            throw new InvalidParameterException("\nWrong parameters for: " + commandName + "\n");
        }
        String firstParameter = null;
        String secondParameter = null;
        Creature object = null;
        if (commandParameters.length >= 1) {
            try {
                JSONObject tempObj = new JSONObject(rawFirstParameter);
                if (tempObj.has("name") && commandParameters[0].equals("name")) {
                    firstParameter = tempObj.getString("name");
                } else if (tempObj.has("details") && commandParameters[0].equals("details")) {
                    tempObj = new JSONObject(rawFirstParameter).getJSONObject("details");
                    object = new Creature(tempObj, user.getNick());
                } else if (tempObj.has("nick") && commandParameters[0].equals("nick")) {
                    firstParameter = tempObj.getString("nick");
                } else {
                    throw new InvalidParameterException("\nWrong parameters for: " + commandName + "\n");
                }
                if (commandParameters.length == 2) {
                    if (commandParameters[1].equals("details")) {
                        tempObj = new JSONObject(rawSecondParameter);
                        tempObj.put("name", firstParameter);
                        object = new Creature(tempObj.getJSONObject("details"), user.getNick());
                    } else if (commandParameters[1].equals("password")) {
                        tempObj = new JSONObject(rawSecondParameter);
                        secondParameter = tempObj.getString("password");
                    } else {
                        throw new InvalidParameterException("\nWrong parameters for: " + commandName + "\n");
                    }
                }
            } catch (JSONException e) {
                throw new InvalidParameterException(e.getMessage());
            }
        }

        if (user.getNick() != null && user.getToken() != null)
            return new Command(commandName, firstParameter, secondParameter, object, user.getToken(), user.getNick());
        else
            return new Command(commandName, firstParameter, secondParameter, object, null, null);
    }
}





