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

public class NioMain implements Runnable {
    static String HOST = "";
    static int PORT = 1337;
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
        new NioMain().run();
    }

    @Override
    public void run() {
        int attNum = 1;
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

                    attNum = 1;

                    for (; ; ) {
                        System.out.print(">>> ");

                        Command command = IOTools.readCommand(bufferedReader);
                        IOTools.<Command>sendObject(channel, command, Command.class.getName());
                        Object obj = IOTools.readObject(ois, true);
                        printResp(obj);
                        checkHook(closeApp[0], channel);
                    }
                } catch (ServerRespException e) {
                    message = "\nWrong server response: " + e.getMessage();
                } catch (InvalidParameterException e) {
                    message = "\nInvalid command: " + e.getMessage();
                } catch (IOException e) {
//                    if (attNum < 8) {
//                        System.out.printf("\nNo server connection. Trying to connect: committed %d/8 attempts.\n", attNum);
//                        attNum++;
//                        try {
//                            System.out.println("Connecting...");
//                            Thread.sleep(2000);
//                        } catch (InterruptedException ex) {
//                        }
//                    } else {
//                        message = "\nNo server connection. The available number of attempts has been exhausted.\n";
//                        break;
//                    }

                    System.out.println("No server connection\nEnter host and port, example: 127.0.0.10:1337\n");

                    waiting_for_input:
                    while (true) {

                        String hostPort = bufferedReader.readLine().trim();
                        if (!"exit".equals(hostPort)) {
                            String[] data = hostPort.split(":");
                            if (data.length == 2) {
                                HOST = data[0].trim();
                                PORT = Integer.valueOf(data[1].trim());
                                break waiting_for_input;
                            } else System.out.println("Wrong\n");
                        } else System.exit(0);

                        break waiting_for_input;
                    }
                } catch (ClientExitException e) {
                    message = e.getMessage();
                    break connection_cycle;
                } catch (InterruptedException | ClassNotFoundException e) {
                    message = e.getMessage();
                } catch (IllegalArgumentException e) {
                    message = "Faild connenction: wrong port value";
                    break connection_cycle;
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


//    public static StringWrapper readStringWrapper(SocketChannel channel) throws IOException, ClassNotFoundException {
//
//        channel.read(buffer);
//        StringWrapper chunk = IOTools.getDeserializedStringWrapper(buffer.array());
//        buffer.clear();
//        channel.configureBlocking(false);
//        return chunk;
//    }

//    private static void exit(Thread otherThread) {
//        //Runtime.getRuntime().removeShutdownHook(otherThread);
//
//        System.exit(-1);
//    }


    //  private void run() throws Exception {
//        int attNum = 1;
//        final boolean[] closeApp = {false};
//        ArrayQueue<String> messages = new ArrayQueue<>(500);
//
//        Thread ctrlC = new Thread(() -> {
//            //saving before exit
//            closeApp[0] = true;
//            System.exit(-1);
//        });
//        ctrlC.setDaemon(true);
//        Runtime.getRuntime().addShutdownHook(ctrlC);
//
//        ByteBuffer buffer = ByteBuffer.allocate(500);
//        buffer.flip();
//
//        try (SocketChannel channel = SocketChannel.open();
//             Selector selector = Selector.open();) {
//            channel.configureBlocking(false);
//            channel.register(selector, SelectionKey.OP_CONNECT);
//            channel.connect(new InetSocketAddress(HOST, PORT));
//
//            BlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
//
//            Thread console = new Thread(() -> {
//                System.out.print("Sign of the end of the command - ';'\n\n>>> ");
//
//                while (true) {
//                    try {
//                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
//                        String multilineCommand = IOTools.getMultiline(bufferedReader);
//                        System.out.println("in console: " + multilineCommand);
//                        //String outputStream.writeUTF(multilineCommand);
//
//                        queue.put(multilineCommand);
//                        SelectionKey key = channel.keyFor(selector);
//
//                        key.interestOps(SelectionKey.OP_WRITE);
//                        selector.wakeup();
//
//                        Thread.sleep(100);
//                        System.out.print(">>> ");
//                    } catch (InvalidParameterException e){
//                        System.out.println(e.getMessage());
//                    }catch (IOException | InterruptedException e) {
//                        messages.add(e.getMessage());
//                        closeApp[0] = true;
//                        break;
//                    }
//                }
//
//                System.exit(-1);
//
//            });
//
//            console.setDaemon(true);
//            System.out.println(1);
//            console.start();
//
//            //String outputStream.writeUTF(multilineCommand);

//            System.out.println(2);
//            StringBuilder data = new StringBuilder();
//            while (true) {
//                //System.out.println("point 3");
//                System.out.println(3);
//                selector.select();
//                for (SelectionKey selectionKey : selector.selectedKeys()) {
//                    if (selectionKey.isConnectable()) {
//                        channel.finishConnect();
//                        System.out.println("01");
//                        selectionKey.interestOps(SelectionKey.OP_WRITE);
//
//                    } else if (selectionKey.isReadable()) {
//                        channel.read(buffer);
//                        System.out.println(getDeserializedString(buffer.array()));
//                        buffer.clear();
////                        String message = null;
////                        if (buffer.hasRemaining()) {
////                            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));) {
////                                message = (String) ois.readObject();
////                            } catch (ClassNotFoundException e) {
////                                e.printStackTrace();
////                            }
////                        }
//                        System.out.println("02");
//                    } else if (selectionKey.isWritable()) {
//                        if (!queue.isEmpty()) {
//                            String line = queue.poll();
//                            if (line != null) {
//                                channel.write(ByteBuffer.wrap(getSerializedObj(line)));
//                                selectionKey.interestOps(SelectionKey.OP_READ);
//                            }
//                        }
//                        System.out.println("03");
//                    }
//
//                }
//                System.out.println(4);
//                if (closeApp[0]) {
//                    //outputStream.writeUTF("save");
//                    messages.forEach(System.out::println);
//                    exit(ctrlC);
//                }
//                System.out.println(5);
//            }
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
}


/*
    user->client +
    client->serv +
    serv<-client +
serv->client
    client<-serv +
 */

