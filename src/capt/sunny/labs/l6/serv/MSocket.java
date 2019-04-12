package capt.sunny.labs.l6.serv;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidParameterException;

import static capt.sunny.labs.l6.serv.Main.charsetName;
import static capt.sunny.labs.l6.serv.Main.fileName;

public class MSocket implements Runnable {

    Socket client;

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
}

