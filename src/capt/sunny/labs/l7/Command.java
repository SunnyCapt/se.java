package capt.sunny.labs.l7;


import capt.sunny.labs.l7.serv.FileSavingException;
import capt.sunny.labs.l7.serv.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.InvalidParameterException;

/**
 * @version 1.0
 */


public class Command implements Serializable {

    protected String name;
    protected String rawFirstParameter;
    protected String rawSecondParameter;
    protected String firstParameter = null;


    protected String secondParameter = null;
    protected Creature object = null;
    protected User user = null;

    public Command(String _name, String _firstParameters, String _secondParameter) throws InvalidParameterException {
        if (!Commands.check(_name))
            throw new InvalidParameterException("\nUnknown command\n");
        name = _name;
        rawFirstParameter = _firstParameters;
        rawSecondParameter = _secondParameter;
        this.parse();
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
        return String.format("\nCommand: %s\nFirst Parameter: %s\nObject: %s\n\n", this.name, this.firstParameter, this.object);
    }

    protected void parse() {
        String[] commandParameters = Commands.valueOf(this.name.toUpperCase()).getParameterNames();
        if (commandParameters.length != (rawFirstParameter == null ? 0 : 1) + (rawSecondParameter == null ? 0 : 1)) {
            throw new InvalidParameterException("\nWrong parameters for: " + name + "\n");
        }
        if (commandParameters.length >= 1) {
            try {
                JSONObject tempObj = new JSONObject(rawFirstParameter);
                if (tempObj.has("key") && commandParameters[0].equals("key")) {
                    firstParameter = tempObj.getString("key");
                } else if (tempObj.has("element") && commandParameters[0].equals("element")) {
                    tempObj = new JSONObject(rawFirstParameter);
                    object = new Creature(tempObj.getJSONObject("element"), user);
                } else if (tempObj.has("nick") && commandParameters[0].equals("nick")) {
                    firstParameter = tempObj.getString("nick");
                } else {
                    throw new InvalidParameterException("\nWrong parameters for: " + name + "\n");
                }
                if (commandParameters.length == 2) {
                    if (commandParameters[1].equals("element")) {
                        tempObj = new JSONObject(rawSecondParameter);
                        object = new Creature(tempObj.getJSONObject("element"), user);
                    } else if (commandParameters[1].equals("password")) {
                        tempObj = new JSONObject(rawSecondParameter);
                        secondParameter = tempObj.getString("password");
                    } else {
                        throw new InvalidParameterException("\nWrong parameters for: " + name + "\n");
                    }
                }
            } catch (JSONException e) {
                throw new InvalidParameterException(e.getMessage());
            }
        }
    }

    public String executeCommand(CreatureMap creatureMap, String fileName, User[] user, String charsetName) throws FileSavingException, InvalidParameterException {

        if (user[0] == null && (!name.equals("login")) && (!name.equals("help")))
            return "\nYou are not logged in, please use the login \ncommand for it. (see manual with help command)";
        switch (name) {
            case "login":
                user[0] = User.login(firstParameter, secondParameter); //try-catch
                return String.format("\nHello %s. You haven't been in street racing for a long time!", firstParameter);
            case "insert":
                try {
                    creatureMap.insert(firstParameter, object);
                    return "Item added\n";
                } catch (JSONException e) {
                    throw new InvalidParameterException(e.getMessage());
                } catch (NullPointerException e) {
                    throw new InvalidParameterException("Specify the key and the object to add\n");
                } catch (InvalidParameterException e) {
                    throw new InvalidParameterException("Failed to initialize object: " + e.getMessage());
                }
            case "show":
                return creatureMap.show();
            case "save":
                creatureMap.save(fileName, charsetName);
                return "\nFile saved\n";
            case "add_if_min":
                try {
                    creatureMap.add_if_min(object);
                    return "";
                } catch (JSONException e) {
                    throw new InvalidParameterException(e.getMessage());
                } catch (NullPointerException e) {
                    throw new InvalidParameterException("Specify the object to add\n");
                } catch (InvalidParameterException e) {
                    throw new InvalidParameterException("Failed to initialize objects: \n" + e.getMessage());
                }
            case "remove":
                try {
                    creatureMap.remove(firstParameter);
                    return "item removed";
                } catch (JSONException e) {
                    throw new InvalidParameterException(e.getMessage());
                } catch (NullPointerException e) {
                    throw new InvalidParameterException("Specify the key of the object to be deleted.\n");
                }
            case "info":
                return creatureMap.info();
            case "remove_lower":
                try {
                    creatureMap.remove_lower(firstParameter);
                    return "all lower items removed";
                } catch (JSONException e) {
                    throw new InvalidParameterException(e.getMessage());
                } catch (NullPointerException e) {
                    throw new InvalidParameterException("Specify the key\n");
                }
            case "help":
                return Commands.help();
            case "exit":
                try {
                    creatureMap.save(fileName, charsetName);
                    return "File saved, bye...";
                } catch (FileSavingException ignored) {
                    return "File didnt save, sorry, bye...";
                }
            default:
                throw new InvalidParameterException("not found");
        }

    }
}


