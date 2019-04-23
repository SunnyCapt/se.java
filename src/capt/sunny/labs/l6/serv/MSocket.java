package capt.sunny.labs.l6.serv;

import capt.sunny.labs.l6.Command;
import capt.sunny.labs.l6.CreatureMap;
import capt.sunny.labs.l6.IOTools;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidParameterException;
import java.util.Date;

//import capt.sunny.labs.l6.StringWrapper;


/**
 * An instance of the class is created for each new customer.
 */
public class MSocket implements Runnable {

    private Socket client;

    /**
     * @param _client - Socket obj which you can get by <code>serverSocket.accept();</code>
     */
    public MSocket(Socket _client) {
        client = _client;
    }

    @Override
    public void run() {
        System.out.printf("New connection: %s:%d\n", client.getInetAddress().getHostAddress(), client.getPort());
        //setClientConfig();
        String fileName = String.format("%s/file_created_by%s_[time:%s].csv", Main.DATA_DIR, client.toString().replaceAll("/","l"), new Date().getTime());
        String message = "";
        Command command = null;
        CreatureMap creatureMap = null;
        try (ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
             InputStream inputStream = client.getInputStream();) {

            while (!client.isClosed()) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
                    //disconnect after 600 seconds of waiting
                    command = IOTools.readCommand(inputStream);
                    System.out.printf("\n[New request from: %s:%d]{%s}\n", client.getInetAddress().getHostAddress(), client.getPort(), command.toString());

                    if (command.getName().equals("import")) {
                        command.deleteEmpty();
                        creatureMap = new CreatureMap(command.getObjectMap());
                    }
                    message = command.executeCommand(creatureMap, fileName, "UTF-8");

                    //message = command.executeCommand(creatureMap, fileName, charsetName);
                    if (command.getName().equals("load")) {
                        if (message.startsWith(Main.DATA_DIR) || message.startsWith("~") || message.equals("data/data.csv") ) {
                            fileName = message;
                            creatureMap = IOTools.getCreatureMapFromFile(fileName, "UTF-8");
                            message = "File loaded";
                        } else {
                            message = "File didnt load: FORBIDDEN, path to file on server must starts with " + Main.DATA_DIR + "\n";
                        }
                    }
//                    command = CommandUtils.getCommand(IOTools.getMultiline(br));
//                    command.executeCommand(creatureMap, fileName, "UTF-8");
                } catch (InvalidParameterException e) {
                    message = "Invalid: " + e.getMessage();
                    ////////////////////////////////
                } catch (FileSavingException e) {
                    message = e.getMessage();
                } catch ( SocketException e) {
                    System.out.printf("Connection with %s:%d broke\n", client.getInetAddress().getHostAddress(), client.getPort());
                    break;
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    break;
                } catch (Exception e) {
                    RuntimeException innerExc = new RuntimeException(String.format("Unknow exception: %s:%d ", client.getInetAddress().getHostAddress(), client.getPort()));
                    innerExc.initCause(e);
                    System.out.println(e.getMessage());
                    break;
                }

                if (!message.isEmpty()) {
                    IOTools.sendObject(oos, message, String.class.getName());
                }


                if (command != null && command.getName().equals("exit"))
                    break;

            }
            throw new IOException();
        } catch (InterruptedException | IOException e) {
            System.out.printf("Connection with %s:%d broke\n", client.getInetAddress().getHostAddress(), client.getPort());
            try {
                client.close();
            } catch (IOException ex) {
            }
        } catch (Exception e) {
            System.out.println("Unknow exception: " + e.getMessage());
        }

    }
}

