package capt.sunny.labs.l6.serv;

import capt.sunny.labs.l6.Command;
import capt.sunny.labs.l6.CommandUtils;
import capt.sunny.labs.l6.IOTools;
import capt.sunny.labs.l6.client.ClientExitException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.NoSuchElementException;

public class Main implements Runnable {
    //Изменять в зависимоссти от оси на которой заупскают
    static final String DATA_DIR = "/home/s278068/labs/data/";
    private ServerSocket server = null;
    private Thread ctrlC = null;
    private boolean needClose = false;

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

    private void checkHook() throws ServerExitException {
//        Thread.sleep(100);
        if (needClose) {
            System.out.println("\nBye...\n");
            throw new ServerExitException("Bye...");
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
    //ctrl c
    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            checkHook();
            startServer(br);

            while (!server.isClosed()) {
                checkHook();
                readServCommand(br);
                accept();
            }

        } catch (ServerExitException e){
            exit(e.getMessage());
        }catch (IOException e) {
            exit("IOException: " + e.getMessage());
        } catch (Exception e) {
            exit("Unknow exception: " + e.getMessage());
        } finally {
            try{
                server.close();
            } catch (IOException ignored) {
                exit("cannt close server socket");
            }
        }
    }

    private void startServer(BufferedReader br) throws Exception {
        System.out.println("Enter host and port: 127.0.0.10:1337");
        setHostPort(br);
        if (server == null)
            throw new Exception(" failed to start server\n");
        System.out.printf("Server is running on port %d\n", server.getLocalPort());
    }

    private void readServCommand(BufferedReader br) throws IOException {
        while (br.ready()) {
            String messFromAdmin = br.readLine();
            System.out.printf("You write: %s", messFromAdmin);
            if (messFromAdmin.equals("exit")) {
                System.out.println("\nBye...\n");
                System.exit(0);
            }
        }
    }

    private void exit(String message) {
        System.out.println(message);
        if (ctrlC != null)
            Runtime.getRuntime().removeShutdownHook(ctrlC);
        System.exit(-1);
    }

    private void accept() throws IOException {
        Socket client = server.accept();
        Thread thread = new Thread(new MSocket(client));
        thread.setDaemon(true);
        thread.start();
    }

    private void setHostPort(BufferedReader br) {
        while (true) {
            try {
                String hostPort = br.readLine();
                hostPort = hostPort.trim();
                if (!"exit".equals(hostPort)) {
                    String[] data = hostPort.split(":");
                    if (data.length == 2) {
                        String ip = data[0].trim();
                        int port = Integer.valueOf(data[1].trim());
                        server = new ServerSocket(port, 1000, Inet4Address.getByName(ip));
                        break;
                    } else {
                        System.out.println("Wrong\n");
                    }
                } else System.exit(0);
            } catch (NullPointerException | StringIndexOutOfBoundsException | IllegalArgumentException | IOException e) {
                System.out.println("\nInvalid host:port entered or not admitted to the network.\nTry again.\n");
            } catch (NoSuchElementException e) {
                System.out.println("exit");
                System.exit(0);
            } catch (Exception e){
                System.out.println("Unknown exception: "+e.getMessage());
                System.exit(-1);
            }
        }
    }

}


