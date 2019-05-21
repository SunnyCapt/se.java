package capt.sunny.labs.l7.serv;

import capt.sunny.labs.l7.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidParameterException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import capt.sunny.labs.l7.StringWrapper;


/**
 * An instance of the class is created for each new customer.
 */
public class MSocket implements Runnable {

    protected DataManager dataManager;
    private String fileName;
    private String message;
    private Socket client = null;
    private final Exception[] exception = {null};
    private Command command = null;
    private User[] user = new User[1];
    //private Status status = new Status();
    private ExecutorService executeIt = Executors.newFixedThreadPool(Server.getNumberOfAllowedRequests()); //5 is number of allowed requests


    /**
     * @param _client - Socket obj which you can get by <code>serverSocket.accept();</code>
     */
    MSocket(Socket _client, DataManager _dataManager) {
        try {
            fileName = Server.config.getString("full_path_to_file");//full path to the file
            this.client = _client;
            this.dataManager = _dataManager;
        } catch (Exception e) {
            System.out.println("\nCan not create new connection(MSocket)");
        }
    }

    @Override
    public void run() {

        System.out.printf("New connection: %s:%d\n", client.getInetAddress().getHostAddress(), client.getPort());

        try (ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
             InputStream inputStream = client.getInputStream()) {
            command = null;
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

                    createAnswerThread(oos);


                } catch (RequestException | StreamCorruptedException e) {
                    System.out.printf("Incorrect request from [%s:%d]\n", client.getInetAddress().getHostAddress(), client.getPort());
                    message = "Incorrect request: " + e.getMessage();
                } catch (InvalidParameterException e) {
                    message = "Invalid: " + e.getMessage();
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
        try {
            executeIt.shutdown();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void createAnswerThread(ObjectOutputStream oos) throws Exception {
        Thread answerThread = new Thread(new AnswerMSocket(command, dataManager, oos, exception, user));
        answerThread.start();
        answerThread.interrupt();
        check();
    }

    private void check() throws Exception {
        if (exception[0] != null){
            throw exception[0];
        }
    }

    private void printRequest(Command command) {
        System.out.printf("\n[New request from: %s:%d]{%s}\n", client.getInetAddress().getHostAddress(), client.getPort(), command.toString());
    }


}

