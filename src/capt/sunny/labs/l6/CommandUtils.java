package capt.sunny.labs.l6;

public class CommandUtils{
    public final  static String saveHelpInfo = "\nsave;\n\tсохранить коллекцию в файл\n";
    public final  static String exitHelpInfo = "\nexit;\n\tвыход с сохранением\n";
    public final  static String helpHelpInfo = "\nhelp;\n\tпоказать этот текст\n";
    public final  static String insertHelpInfo =
            "\ninsert {String key} {element};\n\tдобавить новый элемент с заданным ключом\n\tпример:" +
                    "\n\tinsert{\n" +
                    "\t       \"key\":\"key0\"\n" +
                    "\t      } {\n" +
                    "\t         \"element\":{\n" +
                    "\t                   \"name\":\"name3\",\n" +
                    "\t                   \"age\":270,\n" +
                    "\t                   \"height\":345.34,\n" +
                    "\t                   \"type\":\"human\",\n" +
                    "\t                   \"isLive\":true,\n" +
                    "\t                   \"creationDate\":\"10 Apr 2019 17:42:26 GMT\",\n" +
                    "\t                   \"location\":{\n" +
                    "\t                           \"x\":23.23,\n" +
                    "\t                           \"y\":23.2,\n" +
                    "\t                           \"z\":25.2\n" +
                    "\t                          }\n" +
                    "\t                   }\n" +
                    "\t          };\n";
    public final  static String showHelpInfo = "\nshow;\n\tвывести в стандартный поток вывода все элементы коллекции в строковом представлении\n";
    public final  static String add_if_minHelpInfo =
            "\nadd_if_min {element};\n\tдобавить новый элемент в коллекцию, если его значение\n\tменьше, чем у наименьшего элемента этой коллекции(сравнение по возрасту)\n\tпример:" +
                    "\n\tadd_if_min {\n" +
                    "\t             \"element\":{\n" +
                    "\t                   \"name\":\"name3\",\n" +
                    "\t                   \"age\":270,\n" +
                    "\t                   \"height\":345.34,\n" +
                    "\t                   \"type\":\"human\",\n" +
                    "\t                   \"isLive\":true,\n" +
                    "\t                   \"creationDate\":\"10 Apr 2019 17:42:26 GMT\",\n" +
                    "\t                   \"location\":{\n" +
                    "\t                           \"x\":23.23,\n" +
                    "\t                           \"y\":23.2,\n" +
                    "\t                           \"z\":25.2\n" +
                    "\t                          }\n" +
                    "\t                   }\n" +
                    "\t             };\n";
    public final  static String removeHelpInfo = "\nremove {String key};\n\tудалить элемент из коллекции по его ключу\n\tпример: " +
            "\n\tremove{\n" +
            "\t       \"key\":\"key0\"\n" +
            "\t      }\n";
    public final  static String infoHelpInfo = "\ninfo;\n\tвывести в стандартный поток вывода информацию о коллекции (тип, дата \n\tинициализации, количество элементов)\n";
    public final  static String remove_lowerHelpInfo =
            "\nremove_lower {String key}; удалить из коллекции все элементы, ключ которых меньше, чем заданный\n\tпример:" +
                    "\n\tremove_lower{\n" +
                    "\t              \"key\":\"key0\"\n" +
                    "\t            }\n";

    //public final  static String multilineHelpInfo = "\nmultiline\n\tвыключить(если включен)/включить(если выключен) многострочный ввод\n";
    public final static String envHelpInfo = "\nenvironment variable:\n\tYou set charset before start programm. \n\tIn the console: set CHARSET5=smth_charset\n\n\tTo set the file path: set FILE_FOR_5LAB=path/to/file.csv\n";
    public final static String loadHelpInfo = "\nload {\"fileName\":\"path/to/file\"};\n\tSet file with data\n";
    public final static String help = infoHelpInfo + loadHelpInfo + showHelpInfo + saveHelpInfo + envHelpInfo + helpHelpInfo + exitHelpInfo +  insertHelpInfo + add_if_minHelpInfo + remove_lowerHelpInfo + removeHelpInfo; //multilineHelpInfo

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
}