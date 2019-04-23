package capt.sunny.labs.l6.serv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;

public class Main {
    //Изменять в зависимоссти от оси на которой заупскают
    public static final String DATA_DIR = "/home/s278068/trash/lab/data/";
    //static int servPort = 1340;
    //static ExecutorService executeIt = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        String messFromAdmin;
        ServerSocket server;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Enter host and port: 127.0.0.10:1337");
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
                        } else System.out.println("Wrong\n");
                    } else System.exit(0);
                } catch (NullPointerException | StringIndexOutOfBoundsException | IllegalArgumentException | IOException e) {
                    System.out.println("Invalid host:port entered or not admitted to the network");
                } catch (NoSuchElementException e) {
                    System.out.println("exit");
                }
                System.exit(0);
            }
            System.out.printf("Server is running on port %d\n", server.getLocalPort());
            while (!server.isClosed()) {
                if (br.ready()) {
                    messFromAdmin = br.readLine();
                    System.out.printf("You write: %s", messFromAdmin);
                }

                Socket client = server.accept();
                new Thread(new MSocket(client)).start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } catch (Exception e){
            System.out.println("Unknow exception: " + e.getMessage());
        }


    }

}


