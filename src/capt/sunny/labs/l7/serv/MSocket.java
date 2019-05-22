package capt.sunny.labs.l7.serv;

import capt.sunny.labs.l7.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidParameterException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//import capt.sunny.labs.l7.StringWrapper;


/**
 * An instance of the class is created for each new customer.
 */
public class MSocket implements Runnable {

    private final Exception[] exception = {null};
    protected DataManager dataManager;
    private Socket client = null;
    private Command command = null;
    private User user = new User();
    //private Status status = new Status();
    private ExecutorService executeIt = Executors.newFixedThreadPool(Server.getNumberOfAllowedRequests()); //5 is number of allowed requests


    /**
     * @param _client - Socket obj which you can get by <code>serverSocket.accept();</code>
     */
    MSocket(Socket _client, DataManager _dataManager) {
        try {
            this.client = _client;
            this.dataManager = _dataManager;
            String threadName = Thread.currentThread().getName();
            dataManager.putUser(threadName, user);
        } catch (Exception e) {
            System.out.println("\nCan not create new connection(MSocket)");
        }
    }

    @Override
    public void run() {

        System.out.printf("New connection: %s:%d\n", client.getInetAddress().getHostAddress(), client.getPort());

        try (ObjectOutputStreamWrapper oos = new ObjectOutputStreamWrapper(new ObjectOutputStream(client.getOutputStream()), new ReentrantLock());
             InputStream inputStream = client.getInputStream()) {
            command = null;
            Thread tokenCheckerThread = new Thread(new TokenChecker(oos, dataManager.getUsers()));
            tokenCheckerThread.setDaemon(true);
            tokenCheckerThread.start();
            while (!client.isClosed()) {

                String message = "";
                try {
                    try {
                        command = CommandUtils.readCommand(inputStream);
                        user.updateLastReqTime();
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

    private void createAnswerThread(ObjectOutputStreamWrapper oos) throws Exception {
        Thread answerThread = new Thread(new AnswerMSocket(command, dataManager, oos, exception, user));
        answerThread.start();
        answerThread.interrupt();
        check();
    }

    private void check() throws Exception {
        if (exception[0] != null) {
            throw exception[0];
        }
    }

    private void printRequest(Command command) {
        System.out.printf("\n[New request from: %s:%d]{%s}\n", client.getInetAddress().getHostAddress(), client.getPort(), command.toString());
    }


}

