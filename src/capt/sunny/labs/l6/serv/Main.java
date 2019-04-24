package capt.sunny.labs.l6.serv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;

public class Main implements Runnable {
    //Изменять в зависимоссти от оси на которой заупскают
    public static final String DATA_DIR = "/home/s278068/trash/lab/data/";
    ServerSocket server = null;
    //static int servPort = 1340;
    //static ExecutorService executeIt = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        new Main().run();
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            startServer(br);

            while (!server.isClosed()) {
                readServCommand(br);
                accept();
            }

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unknow exception: " + e.getMessage());
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
                System.out.println("Invalid host:port entered or not admitted to the network");
            } catch (NoSuchElementException e) {
                System.out.println("exit");
            }
            System.exit(0);
        }
    }

}


