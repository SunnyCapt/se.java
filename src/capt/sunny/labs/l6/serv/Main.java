package capt.sunny.labs.l6.serv;

import capt.sunny.labs.l6.IOTools;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.Map;

public class Main {
    static String fileName;
    static String charsetName = "UTF-8";


    public static void main(String[] args) {
        String message;
        String multilineCommand;
        String commandLine;
        Command command;
        setClientConfig();
        CreatureMap creatureMap = IOTools.getCreatureMapFromFile(fileName, charsetName);
        try (ServerSocket servSocket = new ServerSocket(1337)) {
            Socket client = servSocket.accept();
            DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
            DataInputStream inputStream = new DataInputStream(client.getInputStream());
            while (!client.isClosed()) {
                try {
                    multilineCommand = inputStream.readUTF();
                    JSONObject rawCommand = new JSONObject(multilineCommand);
                    System.out.printf("Command from %s:%d : %s\n",client.getInetAddress().getHostAddress(),client.getPort(), rawCommand.get("fullCommandInput"));
                    commandLine = rawCommand.getString("fullCommandInput");
                    command = Command.getCommand(commandLine);
                    if (!(rawCommand.getString("withoutParametersInput").contains(command.getName()))) {
                        throw new InvalidParameterException("command not found");
                    }

                    if (commandLine.equals("")) {
                        message = "Enter command\n";
                    } else {
                        message = command.executeCommand(creatureMap, fileName, charsetName);
                    }
                } catch (InvalidParameterException e) {
                    message = "Invalid command: " + e.getMessage();
                } catch (FileSavingException e) {
                    message = e.getMessage();
                }
                outputStream.writeUTF(message/*new String(message.getBytes(), Charset.forName(charsetName))*/);
            }
        } catch (Exception e) {

        }

    }

    public static void setClientConfig() {
        Map<String, String> env = System.getenv();
        fileName = env.get("FILE_FOR_5LAB");
//        charsetName = env.get("CHARSET5");
//        if (charsetName == null) {
//            charsetName = "UTF-8";
//        }
        if (fileName == null) {
            System.out.println("Set an environment variable named \"FILE_FOR_5LAB\"");
            System.exit(-1);
        }
//        try {
//            System.setOut(new PrintStream(System.out, true, charsetName));
//        } catch (UnsupportedEncodingException e) {
//            System.out.printf("Change CHARSET5 environment variable: UnsupportedEncodingException %s", charsetName);
//        }
    }
}
