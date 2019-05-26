package capt.sunny.labs.l7.client;

import capt.sunny.labs.l7.*;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;


public class Client implements Runnable {
    private String HOST = "localhost";
    private int PORT = -1;
    private String message = "";
    private SocketChannel channel = null;
    private User me = new User();
    private BufferedReader reader;
    private ReaderThread objReader = null;


    public Client(Runtime runtime, BufferedReader _reader) {
        reader = _reader;

        runtime.addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(1);
                try {
                    IOTools.<Command>sendObject(channel, CommandUtils.getCommand("save", me), Command.class.getName());
                    message = "\nAll saved";
                } catch (Exception e2) {
                    message = "\nDidt save, sorry";
                }
                System.out.println(message);
                System.out.println("\nBye, kitty!");
            } catch (InterruptedException e) {
            }
        }));
    }

    public Client(Runtime runtime, String _HOST, int _PORT, BufferedReader _reader) {
        reader = _reader;
        runtime.addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(1);
                try {
                    IOTools.<Command>sendObject(channel, CommandUtils.getCommand("save", me), Command.class.getName());
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
        new Client(Runtime.getRuntime(), new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))).run();
    }


    public static SocketChannel getChannel(String _host, int _port) throws IOException, InterruptedException {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(_host, _port));
        channel.finishConnect();
        channel.configureBlocking(true);
        Thread.sleep(200);
        return channel;
    }


    private void checkAndPrintResp(Object obj, Status status, User user) throws RequestException {
        if (!(obj instanceof String)) {
            throw new RequestException("server sent an unknown species object \n");
        } else {
            if (status.is_LOGGING()) {
                try {
                    JSONObject tempJSON = new JSONObject((String) obj);
                    obj = tempJSON.getString("message");
                    user.setNick(tempJSON.getString("nick") == "" ? null : tempJSON.getString("nick"));
                    user.updateToken(tempJSON.getString("token") == "" ? null : tempJSON.getString("token"));
                } catch (Exception c) {
                    obj = c.getMessage();
                }
                if (((String) obj).contains("Wrong login/password")) {
                    status.do_NOT_LOGGED_IN();
                } else {
                    status.do_OK();
                }
            }else if (status.is_SIGIN_FINISH()){
                try {
                    JSONObject tempJSON = new JSONObject((String) obj);
                    obj = tempJSON.getString("message");
                    user.setNick(tempJSON.getString("nick") == "" ? null : tempJSON.getString("nick"));
                    user.updateToken(tempJSON.getString("token") == "" ? null : tempJSON.getString("token"));
                } catch (Exception c) {
                    obj = c.getMessage();
                }
                if (((String) obj).contains("Wrong login/password")) {
                    status.do_NOT_LOGGED_IN();
                } else {
                    status.do_OK();
                }
            }
            System.out.println("\n[from server]:" + obj);
        }
    }


    @Override
    public void run() {
        try {
            System.out.print("Sign of the end of the command - ';'\n\n");
            Status status = new Status();
            main_cycle:
            while (true) {
                message = "";
                try (SocketChannel preChannel = getChannel(HOST, PORT);
                     ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(preChannel))) {
                    channel = preChannel;
                    objReader = new ReaderThread(ois, System.out);
                    Thread threadOfReader = new Thread(objReader);
                    threadOfReader.setDaemon(true);
                    threadOfReader.start();
                    command_cycle:
                    for (; ; ) {
                        message = "";
                        try {

                            System.out.print(">>> ");

                            Command command = CommandUtils.readCommand(reader, me);
                            if (command.getName().equals("login")) status.do_LOGGING();
                            //if (command.getName().equals("signin")) status.do_SIGIN();
                            if (command.getName().equals("signin_finish")) status.do_SIGIN_FINISH();
                            if (command.getName().equals("exit"))
                                objReader.close();
                            IOTools.<Command>sendObject(channel, command, Command.class.getName());
                            if (command.getName().equals("exit"))
                                throw new IOException(" you are disconnected from the server\n");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ignored) {
                            }
                            Object obj = objReader.getFromQueue();

                            checkAndPrintResp(obj, status, me);


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
                            String hostPort = reader.readLine().trim();
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
                } catch (InterruptedException e) {
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
        if (objReader != null)
            objReader.close();
        System.exit(0);
    }


}


class ReaderThread implements Runnable, Closeable {

    private ObjectInputStream ois;
    private boolean keepThread = true;
    private Queue<Object> queue = new PriorityQueue<>();
    private PrintStream out;

    public ReaderThread(ObjectInputStream _ois, PrintStream _out) {
        ois = _ois;
        out = _out;
    }

    @Override
    public void run() {
        while (keepThread ) {
            try {
                Object obj = IOTools.readObject(ois, false);
                String resp = (String) obj;
                if (resp.startsWith("{us}"))
                    out.println("\n\n" + resp.substring(4) +"\n\n");
                else
                    queue.add(obj);
            } catch (IOException e) {
                if (!e.getMessage().contains("stream header: 00000000"))System.out.println("Cannt read server responces");
            } catch (ClassNotFoundException e) {
                if (!e.getMessage().contains("stream header: 00000000"))System.out.println("Cannt read server responce: unknown data");
            } catch (InterruptedException e) {
                close();
            } catch (Exception e) {
                System.out.println("Cannt read server responce: unknown error: " + e.getMessage());
                close();
            }
        }
    }

    @Override
    public void close() {
        keepThread = false;
    }

    public Object getFromQueue() {

        while (queue.size() == 0) {
        }
        return queue.poll();
    }
}


