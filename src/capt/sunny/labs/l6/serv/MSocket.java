package capt.sunny.labs.l6.serv;

import capt.sunny.labs.l6.CommandWithObject;
import capt.sunny.labs.l6.Creature;
import capt.sunny.labs.l6.IOTools;
import capt.sunny.labs.l6.StringWrapper;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidParameterException;
import java.util.List;
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
        setClientConfig();


        String message = "";
        CreatureMap creatureMap = null;
        try (ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
             InputStream inputStream = client.getInputStream()) {
            finish:
            while (!client.isClosed()) {
                try {
                    CommandWithObject<Creature> commandWithObject = IOTools.readObjectFromStream(inputStream);

                    System.out.println(commandWithObject.getCommand());
                    String multilineCommand = commandWithObject.getCommand();
                    int indexOfFullCommandName = multilineCommand.lastIndexOf("\n");
                    String commandLine = multilineCommand.substring(0, indexOfFullCommandName);
                    System.out.printf("Command from %s:%d : %s\n", client.getInetAddress().getHostAddress(), client.getPort(), commandLine);
                    //String commandLine = rawCommand.getString("fullCommandInput");
                    Command command = Command.getCommand(commandLine);
                    if (!(multilineCommand.substring(indexOfFullCommandName).contains(command.getName()))) {
                        throw new InvalidParameterException("command not found\n");
                    }

                    if (commandLine.equals("")) {
                        message = "Enter command\n";
                    } else {
                        message = command.executeCommand(creatureMap, fileName, charsetName);
                        if (command.name.equals("load")) {
                            //if (fileName == null)
                            fileName = message;
                            creatureMap = IOTools.getCreatureMapFromFile(fileName, charsetName);
                            message = "File loaded";
                        }

                    }

                } catch (EOFException e) {
                    System.out.printf("Connection with %s:%d broke: %s\n", client.getInetAddress().getHostAddress(), client.getPort(), e.getMessage());
                    break finish;
                } catch (InvalidParameterException e) {
                    message = "Invalid command: " + e.getMessage();
                    ////////////////////////////////
                } catch (FileSavingException e) {
                    message = e.getMessage();
                } catch (SocketException e) {
                    System.out.printf("Connection with %s:%d broke\n", client.getInetAddress().getHostAddress(), client.getPort());
                    break finish;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    RuntimeException innerExc = new RuntimeException(String.format("Unknow exception: %s:%d ", client.getInetAddress().getHostAddress(), client.getPort()));
                    innerExc.initCause(e);
                    System.out.println(e.getMessage());
                    break finish;
                }

                List<String> strWrp = IOTools.getStringChunks(message);
                for (int i = 0; i < strWrp.size(); i++) {
                    oos.writeObject(new StringWrapper(strWrp.get(i), i, strWrp.size()));
                    oos.flush();
                    Thread.sleep(300);

                    System.out.printf("Send: %d\n", i);
                }


                System.out.println(message);


            }
            client.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("point1");
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

