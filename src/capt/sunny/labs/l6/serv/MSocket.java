package capt.sunny.labs.l6.serv;

import capt.sunny.labs.l6.Command;
import capt.sunny.labs.l6.Creature;
import capt.sunny.labs.l6.CreatureMap;
import capt.sunny.labs.l6.IOTools;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidParameterException;
import java.util.Map;

//import capt.sunny.labs.l6.StringWrapper;


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
        //setClientConfig();
        fileName = client.toString();
        String message = "";
        CreatureMap creatureMap = null;
        try (ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
             InputStream inputStream = client.getInputStream();) {

            while (!client.isClosed()) {
                try {
                    Command command = IOTools.readCommand(inputStream);
                    System.out.printf("[New request from: %s:%d]{\n%s}\n", client.getInetAddress().getHostAddress(), client.getPort(), command.toString());

                    if (command.name.equals("import")) {
                        System.out.println(command.getObjectMap());
                        command.deleteEmpty();
                        creatureMap = new CreatureMap(command.getObjectMap());
                    }
                    System.out.println("point1");
                    message = command.executeCommand(creatureMap, fileName, "UTF-8");
                    //message = command.executeCommand(creatureMap, fileName, charsetName);
                    System.out.println("point2");
                    if (command.name.equals("load")) {
                        if (message.startsWith(Main.DATA_DIR) || true) {
                            fileName = message;
                            creatureMap = IOTools.getCreatureMapFromFile(fileName, "UTF-8");
                            message = "File loaded";
                        }else{
                            message = "File didnt load: FORBIDDEN, path to file on server must starts with "+Main.DATA_DIR+"\n";
                        }
                    }

                } catch (EOFException e) {
                    System.out.printf("Connection with %s:%d broke: %s\n", client.getInetAddress().getHostAddress(), client.getPort(), e.getMessage());
                    break;
                } catch (InvalidParameterException e) {
                    message = "Invalid: " + e.getMessage();
                    ////////////////////////////////
                } catch (FileSavingException e) {
                    message = e.getMessage();
                } catch (SocketException e) {
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


            }
            client.close();
        } catch (InterruptedException | IOException e) {
            System.out.printf("Connection with %s:%d broke\n", client.getInetAddress().getHostAddress(), client.getPort());
            try {
                client.close();
            } catch (IOException ex) {
            }
        } catch (Exception e){
            System.out.println("Unknow exception: " + e.getMessage());
        }

    }

//    private Command readCommand(){
//
//    }

//
//    public void setClientConfig() {
//        Map<String, String> env = System.getenv();
//        fileName = env.get("FILE_FOR_5LAB");
////        charsetName = env.get("CHARSET5");
////        if (charsetName == null) {
////            charsetName = "UTF-8";
////        }
//        if (fileName == null) {
//            System.out.println("Set an environment variable named \"FILE_FOR_5LAB\"");
//            System.exit(-1);
//        }
////        try {
////            System.setOut(new PrintStream(System.out, true, charsetName));
////        } catch (UnsupportedEncodingException e) {
////            System.out.printf("Change CHARSET5 environment variable: UnsupportedEncodingException %s", charsetName);
////        }
//    }
}

