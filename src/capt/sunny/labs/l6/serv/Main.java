package capt.sunny.labs.l6.serv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static int servPort = 1339;
    static ExecutorService executeIt = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        String messFromAdmin;
        try (ServerSocket server = new ServerSocket(servPort);
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.printf("Server is running on port %d\n", servPort);
            while (!server.isClosed()) {
                if (br.ready()) {
                    messFromAdmin = br.readLine();
                    System.out.printf("You write: %s", messFromAdmin);
                }

                Socket client = server.accept();
                executeIt.execute(new MSocket(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
