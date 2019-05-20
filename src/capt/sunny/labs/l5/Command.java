package capt.sunny.labs.l5;


import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;

public class Command {
    public static String saveHelpInfo = "\nsave;\n\tсохранить коллекцию в файл\n";
    public static String exitHelpInfo = "\nexit;\n\tвыход с сохранением\n";
    public static String helpHelpInfo = "\nhelp;\n\tпоказать этот текст\n";
    public static String insertHelpInfo =
            "\ninsert {String key} {element};\n\tдобавить новый элемент с заданным ключом\n\tпример:" +
                    "\n\tinsert{\n" +
                    "\t       \"key\":\"key0\"\n" +
                    "\t      } {\n" +
                    "\t         \"element\":{\n" +
                    "\t                   \"commandName\":\"name3\",\n" +
                    "\t                   \"age\":270,\n" +
                    "\t                   \"height\":345.34,\n" +
                    "\t                   \"species\":\"human\",\n" +
                    "\t                   \"isLive\":true,\n" +
                    "\t                   \"location\":{\n" +
                    "\t                           \"x\":23.23,\n" +
                    "\t                           \"y\":23.2,\n" +
                    "\t                           \"z\":25.2\n" +
                    "\t                          }\n" +
                    "\t                   }\n" +
                    "\t          };\n";
    public static String showHelpInfo = "\nshow;\n\tвывести в стандартный поток вывода все элементы коллекции в строковом представлении\n";
    public static String add_if_minHelpInfo =
            "\nadd_if_min {element};\n\tдобавить новый элемент в коллекцию, если его значение\n\tменьше, чем у наименьшего элемента этой коллекции(сравнение по возрасту)\n\tпример:" +
                    "\n\tadd_if_min {\n" +
                    "\t             \"element\":{\n" +
                    "\t                   \"commandName\":\"name3\",\n" +
                    "\t                   \"age\":270,\n" +
                    "\t                   \"height\":345.34,\n" +
                    "\t                   \"species\":\"human\",\n" +
                    "\t                   \"isLive\":true,\n" +
                    "\t                   \"location\":{\n" +
                    "\t                           \"x\":23.23,\n" +
                    "\t                           \"y\":23.2,\n" +
                    "\t                           \"z\":25.2\n" +
                    "\t                          }\n" +
                    "\t                   }\n" +
                    "\t             };\n";
    public static String removeHelpInfo = "\nremove {String key};\n\tудалить элемент из коллекции по его ключу\n\tпример: " +
            "\n\tremove{\n" +
            "\t       \"key\":\"key0\"\n" +
            "\t      }\n";
    public static String infoHelpInfo = "\ninfo;\n\tвывести в стандартный поток вывода информацию о коллекции (тип, дата \n\tинициализации, количество элементов)\n";
    public static String remove_lowerHelpInfo =
            "\nremove_lower {String key}; удалить из коллекции все элементы, ключ которых меньше, чем заданный\n\tпример:" +
                    "\n\tremove_lower{\n" +
                    "\t              \"key\":\"key0\"\n" +
                    "\t            }\n";

    public static String multilineHelpInfo = "\nmultiline\n\tвыключить(если включен)/включить(если выключен) многострочный ввод\n";
    public static String envHelpInfo = "\nenvironment variable:\n\tYou set charset before start programm. \n\tIn the console: set CHARSET5=smth_charset\n\n\tTo set the file path: set FILE_FOR_5LAB=path/to/file.csv\n";
    public static String help = infoHelpInfo + showHelpInfo + saveHelpInfo + envHelpInfo + helpHelpInfo + exitHelpInfo + multilineHelpInfo + insertHelpInfo + add_if_minHelpInfo + remove_lowerHelpInfo + removeHelpInfo;

    String name;
    String firstParameter;
    String secondParameter;


    Command(String _name, String _firstParameters, String _secondParameter) {
        name = _name;
        firstParameter = _firstParameters;
        secondParameter = _secondParameter;
    }

    public static Command getCommand(String commandLine) {
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
        return new Command(
                indexOfFirstParameter == -1 ? commandLine.replace(" ", "") : commandLine.substring(0, indexOfFirstParameter).replace(" ", ""),
                indexOfFirstParameter == -1 ? null : indexOfSecondParameter == -1 ? commandLine.substring(indexOfFirstParameter) : commandLine.substring(indexOfFirstParameter, indexOfSecondParameter),
                indexOfSecondParameter == -1 ? null : commandLine.substring(indexOfSecondParameter)
        );
    }

    public String executeСommand(CreatureMap creatureMap, String fileName, String charsetName) throws FileSavingException {
        JSONObject parsedFirstParameter;
        JSONObject parsedSecondParameter;
        try {
            parsedFirstParameter = firstParameter == null ? null : new JSONObject(firstParameter);
            parsedSecondParameter = secondParameter == null ? null : new JSONObject(secondParameter);
        } catch (JSONException e) {
            throw new InvalidParameterException("parameters incorrectly specified\n");
        }

        switch (name) {
            case "insert":
                try {
                    creatureMap.insert(parsedFirstParameter.getString("key"), new Creature(parsedSecondParameter.getJSONObject("element")));
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
                    return "";
                } catch (FileSavingException e) {
                    e.printStackTrace();
                }
            case "add_if_min":
                try {
                    creatureMap.add_if_min(new Creature(parsedFirstParameter.getJSONObject("element")));
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
                    creatureMap.remove(parsedFirstParameter.getString("key"));
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
                    creatureMap.remove_lower(parsedFirstParameter.getString("key"));
                    return "";
                } catch (JSONException e) {
                    throw new InvalidParameterException(e.getMessage());
                } catch (NullPointerException e) {
                    throw new InvalidParameterException("Specify the key\n");
                }
            case "help":
                return Command.help;
            case "exit":
                creatureMap.save(fileName, charsetName);
                System.exit(0);
            default:
                throw new InvalidParameterException("not found");
        }

    }
}
