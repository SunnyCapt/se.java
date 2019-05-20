package capt.sunny.labs.l5;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;


public class Main {
    static String charsetName;
    static String fileName;

    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        fileName = env.get("FILE_FOR_5LAB");
        charsetName = env.get("CHARSET5");
        if (charsetName == null) {
            charsetName = "UTF-8";
        }
        if (fileName == null) {
            System.out.println("Set an environment variable named \"FILE_FOR_5LAB\"");
            System.exit(-1);
        }

        try {
            System.setOut(new PrintStream(System.out, true, charsetName));
        } catch (java.io.UnsupportedEncodingException unsupportedEncodingException) {
        }

        final CreatureMap[] creatureMap = new CreatureMap[1];
        final boolean[] closeApp = {false};

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName), charsetName)) {
            List<String[]> fileLines = IOTools.readFile(reader, fileName);
            if (fileLines.isEmpty()) {
                creatureMap[0] = new CreatureMap();
            } else {
                boolean isLastLineEmpty = (fileLines.get(fileLines.size() - 1)[0].length() == 0) && (fileLines.get(fileLines.size() - 1)[1] == null);
                if (isLastLineEmpty)
                    fileLines.remove(fileLines.size() - 1);
                creatureMap[0] = new CreatureMap(fileLines);
            }
        } catch (FileNotFoundException e) {
            System.out.println(String.format("Can't read \"%s\"", fileName));
            System.exit(-1);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        } catch (InvalidParameterException e) {
            System.out.println("Failed to load objects: " + e.getMessage());
            System.exit(-1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
//                try {
//                    creatureMap[0].save(fileName, charsetName);
//                } catch (FileSavingException e) {
//                    e.printStackTrace();
//                } finally {
//                    closeApp[0] = true;
//                }
                closeApp[0] = true;
            }
        });


        String message;
        String commandLine;
        Map<String, String> multilineCommand;
        Command command;

        try (BufferedReader bufferedReader = new BufferedReader(new java.io.InputStreamReader(System.in, charsetName))) {
            System.out.print("Sign of the end of the command - ';'\n\n>>> ");
            for (; ; ) {
                try {
                    multilineCommand = IOTools.getMultiline(bufferedReader);
                    commandLine = multilineCommand.get("fullCommandInput");
                    command = Command.getCommand(commandLine);
                    if (!(multilineCommand.get("withoutParametersInput").contains(command.name))) {
                        throw new InvalidParameterException("command not found");
                    }

                    if (commandLine.equals("")) {
                        message = "Enter command\n";
                    } else {

                        message = command.executeСommand(creatureMap[0], fileName, charsetName);
                    }
                } catch (InvalidParameterException e) {
                    message = "Invalid command: " + e.getMessage();
                } catch (FileSavingException e) {
                    message = e.getMessage();
                }
                System.out.println(message/*new String(message.getBytes(), Charset.forName(charsetName))*/);
                System.out.print(">>> ");

                Thread.sleep(100);
                if (closeApp[0]) System.exit(-1);
            }
        } catch (IOException e) {
            System.out.print("Failed to read(write) standard input(output) stream: " + e.getMessage());
            System.exit(-1);
        } catch (InterruptedException e) {
            System.out.print(e.getMessage());
            System.exit(-1);
        }
    }


}

/*
Creature's structure
{
    "commandName": "_name",
    "age": 2019,
    "height": 100.0,
    "species": "_type",
    "isLive": true,
    "location": {
        "x": 1.0,
        "y": 2.0,
        "z": 3.0
     }
}
 */
/*
"key","commandName","age","height","species","isLive","x","y","z"
*/

/*
              {
                "key":"key0"
              }{
                "element":{
                             "commandName":"name3",
                             "age":-1,
                             "height":345.34,
                             "species":"human",
                             "isLive":true,
                             "location":{
                                          "x":23.23,
                                          "y":23.2,

                                          "z":25.2
                                        }
                          }
               };
 */
/*
"key1","name1",323,345.34,"human",true,23.23,23.2,25.2
"key2","name2",300,345.34,"human",true,23.23,23.2,25.2
"key3","name3",290,345.34,"human",true,23.23,23.2,25.2
 */
