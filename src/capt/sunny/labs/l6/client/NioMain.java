package capt.sunny.labs.l6.client;

import capt.sunny.labs.l6.IOTools;
import capt.sunny.labs.l6.StringWrapper;
import com.sun.jmx.remote.internal.ArrayQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

public class NioMain {
    static final String HOST = "localhost";
    static final int PORT = 1339;
    static Class clazz;

    static {
        try {
            clazz = Class.forName("capt.sunny.labs.l6.Creature");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        int attNum = 1;
        final boolean[] closeApp = {false};
        ArrayQueue<String> messages = new ArrayQueue<>(500);
        ArrayQueue<Exception> exceptions = new ArrayQueue<>(500);

        Thread ctrlC = new Thread(() -> {
            //saving before exit
            closeApp[0] = true;
            System.exit(-1);
        });
        ctrlC.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(ctrlC);
//1048576


        try (
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));) {


            System.out.print("Sign of the end of the command - ';'\n\n>>> ");
            //new ObjectInputStream(channel.socket().getInputStream());

            while (true) {
                try (SocketChannel channel = SocketChannel.open()) {

                    channel.connect(new InetSocketAddress(HOST, PORT));
                    channel.finishConnect();
                    channel.configureBlocking(true);
                    ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(channel));

                    for (; ; ) {
                        String multilineCommand = IOTools.getMultiline(bufferedReader);
                        channel.write(ByteBuffer.wrap(IOTools.getSerializedObj(multilineCommand, clazz)));
                        Thread.sleep(500);

                        StringWrapper chunk = null;
                        do {
                            chunk = (StringWrapper) ois.readObject();
                            System.out.print(chunk.chunk);
                        } while (!chunk.isLast());

                        Thread.sleep(100);
                        System.out.print(">>> ");
                    }
                } catch (IOException e) {
                    if (attNum < 8) {
                        System.out.printf("No server connection. Trying to connect: committed %d/8 attempts.\n", attNum);
                        attNum++;
                        try {
                            System.out.println("Connecting...");
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                        }
                    } else {
                        System.out.println("No server connection. The available number of attempts has been exhausted.");
                        Runtime.getRuntime().removeShutdownHook(ctrlC);
                        System.exit(-1);
                    }
                }// catch (UnknownHostException e) {
//                    System.out.println("Faild connenction: unknown host");
//                    exit(ctrlC);
//                } catch (IOException e) {
//                    System.out.print("Failed to read(write) input(output) stream: " + e.getMessage());
//                    exit(ctrlC);
//                } catch (IllegalArgumentException e) {
//                    System.out.println("Faild connenction: wrong port value");
//                    exit(ctrlC);
//                } catch (InterruptedException e) {
//                    System.out.print(e.getMessage());
//                    exit(ctrlC);
//                }
            }


            //System.exit(-1);
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

    private static void exit(Thread otherThread) {
        //Runtime.getRuntime().removeShutdownHook(otherThread);

        System.exit(-1);
    }


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


