package capt.sunny.labs.l6.client;

import capt.sunny.labs.l6.Command;
import capt.sunny.labs.l6.CommandUtils;
import capt.sunny.labs.l6.IOTools;
import capt.sunny.labs.l6.RequestException;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

public class Main implements Runnable {
    private static String HOST = "";
    private static int PORT = -1;
    private String message = "";
    private boolean needClose = false;
    private Thread ctrlC = null;

    public static void main(String[] args) {
        new Main().run();
    }

    public static SocketChannel getChannel(String _host, int _port) throws IOException, InterruptedException {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(_host, _port));
        channel.finishConnect();
        channel.configureBlocking(true);
        Thread.sleep(200);
        return channel;
    }

    private void setCtrlC() {
        try {
            ctrlC = new Thread(() -> {
                //saving before exit
                needClose = true;
                System.exit(-1);
            });
            Runtime.getRuntime().addShutdownHook(ctrlC);
        } catch (Exception e) {
            System.out.println("cannt add ctrl c hook, bye...");
            System.exit(-1);
        }
    }

    private void printResp(Object obj) throws RequestException {
        if (!(obj instanceof String)) {
            throw new RequestException("server sent an unknown type object \n");
        } else {
            System.out.println("\n[from server]:" + obj);
        }
    }

    private void checkHook(SocketChannel channel) throws InterruptedException, ClientExitException, IOException {
//        Thread.sleep(100);
        if (needClose) {
            IOTools.<Command>sendObject(channel, CommandUtils.getCommand("save;"), Command.class.getName());
            throw new ClientExitException("The collection has been saved");
        }
    }

    @Override
    public void run() {
        setCtrlC();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            System.out.print("Sign of the end of the command - ';'\n\n");
            connection_cycle:
            while (true) {
                try (SocketChannel channel = getChannel(HOST, PORT);
                     ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(channel))) {
                    for (; ; ) {
                        try {
                            System.out.print(">>> ");

                            Command command = CommandUtils.readCommand(bufferedReader);

                            IOTools.<Command>sendObject(channel, command, Command.class.getName());
                            //channel.write(ByteBuffer.wrap(bufferedReader.readLine().getBytes()));
                            //channel.write(ByteBuffer.wrap(IOTools.getSerializedObj("sata")));
                            // bufferedReader.readLine();
                            // IOTools.<Double>sendObject(channel,new Double(1.3), Double.class.getName());
                            //disconnect after 60 seconds of waiting
                            //System.out.println("point1");
                            Object obj = IOTools.readObject(ois, true);
                            printResp(obj);

                            checkCommand(command);
                            checkHook(channel);
                        } catch (RequestException | StreamCorruptedException e) {
                            System.out.println("\n[local] Invalid server response: " + e.getMessage());
                        } catch (InvalidParameterException e) {
                            System.out.println("\n[local] Invalid command: " + e.getMessage());
                        }
                    }
                } catch (IOException | IllegalArgumentException e) {
                    System.out.println("\n[local] " + e.getMessage());
                    System.out.println("\nNo server connection\nEnter host and port, example: 127.0.0.10:1337\n");

                    while (true) {
                        String hostPort = bufferedReader.readLine().trim();
                        if (!"exit".equals(hostPort)) {
                            String[] data = hostPort.split(":");
                            if (data.length == 2) {
                                try {
                                    HOST = data[0].trim();
                                    PORT = Integer.valueOf(data[1].trim());
                                    break;
                                } catch (Exception e2) {
                                    System.out.printf("Wrong[%s], try again\n", e2.getMessage());
                                }
                            } else System.out.println("Wrong, try again\n");
                        } else break connection_cycle;
                    }
                } catch (NullPointerException | InterruptedException | ClassNotFoundException e) {
                    System.out.println("\n[local] " + e.getMessage());
                } catch (ClientExitException e) {
                    System.out.println("\n[local] " + e.getMessage());
                    break;
                }
            }
            exit(message);
        }catch (NoSuchElementException e){
            exit("\nBye...\n");
        }catch (Exception e) {
            exit(e.getMessage());
        }

    }

    private void exit(String message) {
        System.out.println(message);
        if (ctrlC != null)
            Runtime.getRuntime().removeShutdownHook(ctrlC);
        System.exit(-1);
    }

    private void checkCommand(Command command) throws IOException {
        if (command.getName().equals("exit"))
            throw new IOException();
    }

}



