package capt.sunny.labs.l6;


import capt.sunny.labs.l6.serv.CreatureMap;
import capt.sunny.labs.l6.serv.FileSavingException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.InvalidParameterException;

/**
 * @version 1.0
 */

class SerializableJSONObject extends JSONObject implements Serializable {
    public SerializableJSONObject(String str) {
        super(str);
    }
}

public class Command implements Serializable {

    public String name;
    protected String firstParameter;
    protected String secondParameter;
    String stringParameter = null;
    Creature object = null;

    public Command(String _name, String _firstParameters, String _secondParameter) {
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


//    @Override
//    public String toString(){
//        return String.format("Command: %s\nFirstParameter: %s\nSecondParameter: %s\n",this.name,this.firstParameter.toString(), this.secondParameter.toString());
//    }

    protected void parse() {

        if (firstParameter != null) {
            JSONObject tempObj = new JSONObject(firstParameter);
            if (tempObj.has("key")) {
                stringParameter = tempObj.getString("key");
            } else if (tempObj.has("fileName")) {
                stringParameter = tempObj.getString("fileName");
            } else if (tempObj.has("element")) {
                firstParameter = firstParameter.replaceAll("([0-9]+)([0-9]+)\\:([0-9]+)([0-9]+)\\:([0-9]+)([0-9]+)", "$1$2^$3$4^$5$6");
                tempObj = new JSONObject(firstParameter);
                tempObj.getJSONObject("element").put("creationDate", tempObj.getJSONObject("element").get("creationDate").toString().replaceAll("([0-9]+)([0-9]+)\\^([0-9]+)([0-9]+)\\^([0-9]+)([0-9]+)", "$1$2:$3$4:$5$6"));
                object = new Creature(tempObj);
            }

            if (secondParameter != null) {
                secondParameter = secondParameter.replaceAll("([0-9]+)([0-9]+)\\:([0-9]+)([0-9]+)\\:([0-9]+)([0-9]+)", "$1$2^$3$4^$5$6");
                tempObj = new JSONObject(secondParameter);
                tempObj.getJSONObject("element").put("creationDate", tempObj.getJSONObject("element").get("creationDate").toString().replaceAll("([0-9]+)([0-9]+)\\^([0-9]+)([0-9]+)\\^([0-9]+)([0-9]+)", "$1$2:$3$4:$5$6"));
                object = new Creature(tempObj);
            }
        }
    }

    public String executeCommand(CreatureMap creatureMap, String fileName, String charsetName) throws FileSavingException {

        System.out.println(name);
        if (creatureMap == null && !name.equals("load"))
            throw new InvalidParameterException("Collection not loaded. To load, use the load or import:\n" + CommandUtils.loadHelpInfo);

        switch (name) {
            case "load":
                return stringParameter;
            case "insert":
                try {
                    creatureMap.insert(stringParameter, object);
                    return "";
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
                try {
                    creatureMap.save(fileName, charsetName);
                    return "\nFile saved\n";
                } catch (FileSavingException e) {
                    throw e;
                }
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
                    return "";
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
                    return "";
                } catch (JSONException e) {
                    throw new InvalidParameterException(e.getMessage());
                } catch (NullPointerException e) {
                    throw new InvalidParameterException("Specify the key\n");
                }
            case "help":
                return CommandUtils.help;
            case "exit":
                creatureMap.save(fileName, charsetName);
                System.exit(0);
            default:
                throw new InvalidParameterException("not found");
        }

    }
}


