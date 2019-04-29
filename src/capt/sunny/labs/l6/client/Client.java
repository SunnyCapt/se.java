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

public class Client implements Runnable {
    private String HOST = "localhost";
    private int PORT = 1488;
    private String message = "";
    private SocketChannel channel = null;

    public Client(Runtime runtime) {
        runtime.addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(1);
                try {
                    IOTools.<Command>sendObject(channel, CommandUtils.getCommand("save;"), Command.class.getName());
                    message = "\nAll saved";
                } catch (Exception e2) {
                    message = "\nDidt save, sorry";
                }
                System.out.println(message);
                System.out.println("\nBye, kitty!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    public Client(Runtime runtime, String _HOST, int _PORT) {
        runtime.addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(1);
                try {
                    IOTools.<Command>sendObject(channel, CommandUtils.getCommand("save;"), Command.class.getName());
                    message = "\nAll saved";
                } catch (Exception e2) {
                    message = "\nDidt save, sorry";
                }
                System.out.println(message);
                System.out.println("\nBye, kitty!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        this.HOST = _HOST;
        this.PORT = _PORT;
    }

    public static void main(String[] args) {
        new Client(Runtime.getRuntime()).run();
    }


    public static SocketChannel getChannel(String _host, int _port) throws IOException, InterruptedException {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(_host, _port));
        channel.finishConnect();
        channel.configureBlocking(true);
        Thread.sleep(200);
        return channel;
    }


    private void printResp(Object obj) throws RequestException {
        if (!(obj instanceof String)) {
            throw new RequestException("server sent an unknown type object \n");
        } else {
            System.out.println("\n[from server]:" + obj);
        }
    }


    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            System.out.print("Sign of the end of the command - ';'\n\n");
            main_cycle:
            while (true) {
                message = "";
                try (SocketChannel preChannel = getChannel(HOST, PORT);
                     ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(preChannel))) {
                    channel = preChannel;
                    command_cycle:
                    for (; ; ) {
                        message = "";
                        try {
                            System.out.print(">>> ");

                            Command command = CommandUtils.readCommand(bufferedReader);

                            IOTools.<Command>sendObject(channel, command, Command.class.getName());
                            Object obj = IOTools.readObject(ois, true);
                            printResp(obj);

                            if (checkCommand(command))
                                throw new IOException(" you are disconnected from the server\n");

                        } catch (RequestException | StreamCorruptedException e) {
                            System.out.println("\n[local] Invalid server response: " + e.getMessage());
                        } catch (InvalidParameterException e) {
                            System.out.println("\n[local] Invalid command: " + e.getMessage());
                        } catch (NullPointerException e) {
                            break main_cycle;
                        }
                    }
                } catch (IOException | IllegalArgumentException e) {
                    try {
                        System.out.println("\n[local] " + e.getMessage());
                        System.out.println("\nNo server connection\nEnter host and port, example: 127.0.0.10:1337\n");
                        connection_cycle:
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
                            } else break main_cycle;
                        }
                    } catch (NullPointerException e2) {
                        break main_cycle;
                    }
                } catch (InterruptedException | ClassNotFoundException e) {
                    System.out.println("\n[local] " + e.getMessage());
                } catch (NullPointerException e) {
                    break main_cycle;
                }
            }
            exit(message);
        } catch (NoSuchElementException e) {
            exit("\nBye...\n");
        } catch (Exception e) {
            exit(e.getMessage());
        }

    }

    private void exit(String message) {
        if (!message.isEmpty())
            System.out.println(message);
        System.exit(0);
    }

    private boolean checkCommand(Command command) {
        return command.getName().equals("exit");
    }

}



