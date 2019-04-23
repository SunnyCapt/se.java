package capt.sunny.labs.l6.client;

import capt.sunny.labs.l6.Command;
import capt.sunny.labs.l6.CommandUtils;
import capt.sunny.labs.l6.IOTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.security.InvalidParameterException;

public class Main implements Runnable {
    static String HOST = "";
    static int PORT = -1;
    static Class clazz;

    static {
        try {
            clazz = Class.forName("capt.sunny.labs.l6.Creature");
        } catch (ClassNotFoundException e) {
            System.out.println("class not found: " + e.getMessage());
        }
    }

    private static SocketChannel getChannel(InetSocketAddress inetSocketAddress) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.connect(inetSocketAddress);
        channel.finishConnect();
        channel.configureBlocking(true);
        return channel;
    }

    private static void printResp(Object obj) throws ServerRespException {
        if (!(obj instanceof String)) {
            throw new ServerRespException("server sent an unknown type object \n");
        } else {
            System.out.println("\n\n" + obj);
        }
    }

    private static void checkHook(boolean needClose, SocketChannel channel) throws InterruptedException, ClientExitException, IOException {
        Thread.sleep(100);
        if (needClose) {
            IOTools.<Command>sendObject(channel, CommandUtils.getCommand("save;"), Command.class.getName());
            throw new ClientExitException("The collection has been saved");
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

    @Override
    public void run() {
        final boolean[] closeApp = {false};

        Thread ctrlC = new Thread(() -> {
            //saving before exit
            closeApp[0] = true;
            System.exit(-1);
        });
        ctrlC.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(ctrlC);

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"))) {
            System.out.print("Sign of the end of the command - ';'\n\n");
            String message = "";
            connection_cycle:
            while (true) {
                try (SocketChannel channel = getChannel(new InetSocketAddress(HOST, PORT));
                     ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(channel))) {

                    for (; ; ) {
                        System.out.print(">>> ");
                        Command command = IOTools.readCommand(bufferedReader);
                        IOTools.<Command>sendObject(channel, command, Command.class.getName());
                        //disconnect after 60 seconds of waiting
                        Object obj = IOTools.readObject(ois, true);
                        printResp(obj);
                        if (command.getName().equals("exit"))
                            throw new IOException();
                        checkHook(closeApp[0], channel);
                    }
                } catch (InvalidParameterException e) {
                    message = "\nInvalid command: " + e.getMessage();
                } catch (IOException | ServerRespException | IllegalArgumentException e) {

                    System.out.println(e.getMessage());
                    System.out.println("\nNo server connection\nEnter host and port, example: 127.0.0.10:1337\n");

                    waiting_for_input_addr:
                    while (true) {

                        String hostPort = bufferedReader.readLine().trim();
                        if (!"exit".equals(hostPort)) {
                            String[] data = hostPort.split(":");
                            if (data.length == 2) {
                                HOST = data[0].trim();
                                PORT = Integer.valueOf(data[1].trim());
                                break waiting_for_input_addr;
                            } else System.out.println("Wrong\n");
                        } else System.exit(0);

                        break waiting_for_input_addr;
                    }
                } catch (ClientExitException e) {
                    message = e.getMessage();
                    break connection_cycle;
                } catch (InterruptedException | ClassNotFoundException e) {
                    message = e.getMessage();
                }
                System.out.println(message);
            }

            System.out.println(message);
            Runtime.getRuntime().removeShutdownHook(ctrlC);
            System.exit(-1);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}



