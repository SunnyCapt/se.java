package capt.sunny.labs.l6.serv;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidParameterException;
import java.util.Map;


/**
 * An instance of the class is created for each new customer.
 */
public class MSocket implements Runnable {

    Socket client;
    String fileName;
    String charsetName = "UTF-8";

    /**
     * @param _client - Socket obj which you can get by <code>serverSocket.accept();</code>
     */
    public MSocket(Socket _client) {
        client = _client;
    }

    @Override
    public void run() {
        System.out.printf("New connection: %s:%d\n", client.getInetAddress().getHostAddress(), client.getPort());
        try (DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
             DataInputStream inputStream = new DataInputStream(client.getInputStream())) {

            String message = "";
            CreatureMap creatureMap = null;

            while (!client.isClosed()) {
                try {
                    String multilineCommand = inputStream.readUTF();
                    JSONObject rawCommand = new JSONObject(multilineCommand);
                    System.out.printf("Command from %s:%d : %s\n", client.getInetAddress().getHostAddress(), client.getPort(), rawCommand.get("fullCommandInput"));
                    String commandLine = rawCommand.getString("fullCommandInput");
                    Command command = Command.getCommand(commandLine);
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
                } catch (SocketException e) {
                    System.out.printf("Connection with %s:%d broke\n", client.getInetAddress().getHostAddress(), client.getPort());
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                outputStream.writeUTF(message/*new String(message.getBytes(), Charset.forName(charsetName))*/);
            }

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setClientConfig() {
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

