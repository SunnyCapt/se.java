package capt.sunny.labs.l7;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Commands {
    SHOW("\nshow;\n\tвывести в стандартный поток вывода всех " +
            "существ коллекции в строковом представлении\n", new String[]{}),
    //    IMPORT("\nload {\"fileName\":\"path/to/file\"};" +
//            "\n\tSet file with data (from client)\n",  new String[]{"fileName"}),
    EXIT("\nexit;\n\tвыход с сохранением\n", new String[]{}),
    INFO("\ninfo;\n\tвывести в стандартный поток вывода информацию " +
            "о коллекции (тип, дата \n\tинициализации, количество существ)\n",
            new String[]{}),
    //    LOAD("\nload {\"fileName\":\"path/to/file\"};" +
//            "\n\tSet file with data (from server)\n",  new String[]{"fileName"}),
    SAVE("\nsave;\n\tсохранить коллекцию в файл\n", new String[]{}),
    HELP("\nhelp;\n\tпоказать этот текст\n", new String[]{}),
    INSERT("\ninsert {creature};\n\tдобавить новое существо с заданным именем\n\tпример:" +
            "\n\tinsert {\n" +
            "\t            \"creature\":{\n" +
            "\t               \"name\":\"cat\",\n" +
            "\t               \"age\":4,\n" +
            "\t               \"height\":38,\n" +
            "\t               \"species\":\"animal\",\n" +
            "\t               \"isLive\":true,\n" +
            "\t               \"location\":{\n" +
            "\t                   \"x\":59.9572337,\n" +
            "\t                   \"y\":30.3059808,\n" +
            "\t                   \"z\":0\n" +
            "\t                 }\n" +
            "\t              }\n" +
            "\t         };\n", new String[]{"creature"}),
    ADD_IF_MIN("\nadd_if_min {details};\n\tдобавить новое существо в коллекцию, если его значение\n\tменьше, чем у наименьшего существа этой коллекции(сравнение по возрасту)\n\tпример:" +
            "\n\tadd_if_min {\n" +
            "\t            \"creature\":{\n" +
            "\t               \"name\":\"cat\",\n" +
            "\t               \"age\":4,\n" +
            "\t               \"height\":38,\n" +
            "\t               \"species\":\"animal\",\n" +
            "\t               \"isLive\":true,\n" +
            "\t               \"location\":{\n" +
            "\t                   \"x\":59.9572337,\n" +
            "\t                   \"y\":30.3059808,\n" +
            "\t                   \"z\":0\n" +
            "\t                 }\n" +
            "\t              }\n" +
            "\t         };\n", new String[]{"creature"}),
    REMOVE_LOWER("\nremove_lower {String name}; удалить из коллекции всех существ, имена которых меньше, чем заданный\n\tпример:" +
            "\n\tremove_lower{\n" +
            "\t              \"name\":\"name0\"\n" +
            "\t            }\n", new String[]{"name"}),
    REMOVE("\nremove {String name};\n\tудалить существо из коллекции по его имени\n\tпример: " +
            "\n\tremove{\n" +
            "\t       \"name\":\"name0\"\n" +
            "\t      }\n", new String[]{"name"}),
    LOGIN("\nlogin {String nick} {String password};\n\tавторизация\n\tпример: " +
            "\n\tlogin{\n" +
            "\t       \"nick\":\"Ivan_Alexander\"\n" +
            "\t      } {\n" +
            "\t       \"password\":\"STupID STudeNt mUSt Die\"\n" +
            "\t      }\n", new String[]{"nick", "password"});

    private static final String commandNames = Arrays.asList(values()).stream().map(e -> String.format("<%s>,", e.name())).collect(Collectors.joining());
    private static String help = ADD_IF_MIN.manual + LOGIN.manual + EXIT.manual + HELP.manual + REMOVE.manual + REMOVE_LOWER.manual + INSERT.manual + INFO.manual + SHOW.manual + SAVE.manual; // + IMPORT.manual + LOAD.manual;
    private String manual;
    private String[] parameterNames;

    Commands(String _manual, String[] _parameterNames) {
        manual = _manual;
        parameterNames = _parameterNames;
    }

    public static boolean check(String command) {
        return commandNames.contains(String.format("<%s>",command.toUpperCase()));
    }

    public static String help() {
        return help;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }

    public String man() {
        return manual;
    }
}
