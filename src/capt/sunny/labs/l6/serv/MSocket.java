package capt.sunny.labs.l6.serv;

import capt.sunny.labs.l6.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidParameterException;
import java.util.Date;

//import capt.sunny.labs.l6.StringWrapper;


/**
 * An instance of the class is created for each new customer.
 */
public class MSocket implements Runnable {

    private String fileName;
    private CreatureMap creatureMap = new CreatureMap();
    private String message;
    private Socket client;

    /**
     * @param _client - Socket obj which you can get by <code>serverSocket.accept();</code>
     */
    MSocket(Socket _client) {
        client = _client;
    }

    @Override
    public void run() {

        System.out.printf("New connection: %s:%d\n", client.getInetAddress().getHostAddress(), client.getPort());

        try (ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
             InputStream inputStream = client.getInputStream()) {
            Command command = null;
            while (!client.isClosed()) {

                message = "";
                try {

                    //disconnect after 600 seconds of waiting
                    try {
                        command = CommandUtils.readCommand(inputStream);
                    } catch (NoClassDefFoundError e1) {
                        throw new RequestException("");
                    }
                    printRequest(command);

                    check(command);
                    message = command.executeCommand(creatureMap, fileName, "UTF-8");

                    check(command);

                } catch (RequestException | StreamCorruptedException e) {
                    System.out.printf("Incorrect request from [%s:%d]\n", client.getInetAddress().getHostAddress(), client.getPort());
                    message = "Incorrect request: " + e.getMessage();
                } catch (InvalidParameterException e) {
                    message = "Invalid: " + e.getMessage();
                } catch (FileSavingException e) {
                    message = "FileSavingException " + e.getMessage();
                } catch (SocketException e) {
                    break;
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    break;
                } catch (Exception e) {
                    System.out.println(String.format("Unknow exception[%s:%d]: %s", client.getInetAddress().getHostAddress(), client.getPort(), e.getMessage()));
                    break;
                }

                if (!message.isEmpty()) {
                    IOTools.sendObject(oos, message, String.class.getName());
                }


                if (command != null && command.getName().equals("exit"))
                    break;

            }


            throw new IOException();

        } catch (IOException e) {//InterruptedException
            System.out.printf("Connection with %s:%d broke\n", client.getInetAddress().getHostAddress(), client.getPort());
            try {
                client.close();
            } catch (IOException ignored) {
            }
        } catch (Exception e) {
            System.out.println("Unknow exception: " + e.getMessage());
        }

    }

    private void check(Command command) {
        if (command.getName().equals("import")) {
            fileName = String.format("%s/file_created_by%s_[time:%s].csv", Main.DATA_DIR, client.toString().replaceAll("/", "l"), new Date().getTime());
            creatureMap = new CreatureMap(command.getObjectMap());
        } else if (command.getName().equals("load")) {
            if (message.startsWith(Main.DATA_DIR) || message.startsWith("~") || message.equals("data/data.csv")) {
                fileName = message;
                try {
                    creatureMap = IOTools.getCreatureMapFromFile(fileName, "UTF-8");
                    message = "File loaded";
                } catch (Exception e) {
                    message = "File didnt load: " + e.getMessage();
                }
            } else {
                message = "File didnt load: FORBIDDEN, path to file on server must starts with " + Main.DATA_DIR + "\n";
            }
        }
    }

    private void printRequest(Command command) {
        System.out.printf("\n[New request from: %s:%d]{%s}\n", client.getInetAddress().getHostAddress(), client.getPort(), command.toString());
    }
}

