package capt.sunny.labs.l7;


import capt.sunny.labs.l7.Commands;
import capt.sunny.labs.l7.Creature;
import capt.sunny.labs.l7.CreatureMap;
import capt.sunny.labs.l7.IOTools;
import capt.sunny.labs.l7.serv.FileSavingException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * @version 1.0
 */


public class Command implements Serializable {

    protected String name;
    protected String firstParameter;
    protected String secondParameter;
    protected String stringParameter = null;
    protected Creature object = null;
    protected List<String[]> srcFile = null;

    public Command(String _name, String _firstParameters, String _secondParameter) throws InvalidParameterException {
        if (!Commands.check(_name))
            throw new InvalidParameterException("\nUnknown command\n");
        name = _name;
        firstParameter = _firstParameters;
        secondParameter = _secondParameter;
        this.parse();
    }

    public String getName() {
        return name;
    }

    public String getStringParameter() {
        return stringParameter;
    }

    public Creature getObject() {
        return object;
    }

    public List<String[]> getObjectMap() {
        return srcFile;
    }

//    public void deleteEmpty() {
//        srcFile.forEach(e -> {
//            if (Arrays.asList(e).size() == 1 && Arrays.asList(e).get(0) == "") srcFile.remove(e);
//        });
//    }

    @Override
    public String toString() {
        return String.format("\nCommand: %s\nFirst Parameter: %s\nObject: %s\nCollection from file: %s\n", this.name, this.stringParameter, this.object, this.srcFile != null ? "some bytes" : null);
    }

    @SuppressWarnings("ConstantConditions")
    protected void parse() {
        String[] commandParameters = Commands.valueOf(this.name.toUpperCase()).getParameterNames();
        if (commandParameters.length != (firstParameter == null ? 0 : 1) + (secondParameter == null ? 0 : 1)) {
            throw new InvalidParameterException("\nWrong parameters for: " + name + "\n");
        }
        if (commandParameters.length >= 1) {
            try {
                JSONObject tempObj = new JSONObject(firstParameter);
                if (tempObj.has("key") && commandParameters[0].equals("key")) {
                    stringParameter = tempObj.getString("key");
                } else if (tempObj.has("fileName") && commandParameters[0].equals("fileName")) {
                    if (name.equals("load"))
                        stringParameter = tempObj.getString("fileName");
                    else if (name.equals("import")) {
                        try {
                            srcFile = IOTools.readFile(tempObj.getString("fileName"));
                        } catch (Exception e) {
                            throw new InvalidParameterException("file could not be read\n");
                        }
                    }
                } else if (tempObj.has("element") && commandParameters[0].equals("element")) {
                    tempObj = new JSONObject(firstParameter);
                    object = new Creature(tempObj.getJSONObject("element"));
                } else {
                    throw new InvalidParameterException("\nWrong parameters for: " + name + "\n");
                }
                if (commandParameters.length == 2) {
                    if (commandParameters[1].equals("element")) {
                        tempObj = new JSONObject(secondParameter);
                        object = new Creature(tempObj.getJSONObject("element"));
                    } else {
                        throw new InvalidParameterException("\nWrong parameters for: " + name + "\n");
                    }
                }
            } catch (JSONException e) {
                throw new InvalidParameterException(e.getMessage());
            }
        }
    }

    public String executeCommand(CreatureMap creatureMap, String fileName, String charsetName) throws FileSavingException, InvalidParameterException{


//        if ((creatureMap == null) && (!name.equals("help")) && (!name.equals("load")) && (!name.equals("import")))
//            throw new InvalidParameterException("Collection not loaded. To load, use the load or import:\n" + Commands.LOAD.man());

        switch (name) {
            case "load":
                return stringParameter;
            case "import":
                if (creatureMap != null)
                    return "collection imported\n";
                else
                    return "collection didnt import\n";
            case "insert":
                try {
                    creatureMap.insert(stringParameter, object);
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
                    creatureMap.remove(stringParameter);
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
                    creatureMap.remove_lower(stringParameter);
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


