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
    static String fileName;
    static String charsetName = "UTF-8";
    static int servPort = 1337;
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


        setClientConfig();

        while (true) {

        }


    }

    public static void setClientConfig() {
        Map<String, String> env = System.getenv();
        fileName = env.get("FILE_FOR_5LAB");
//        charsetName = env.get("CHARSET5");
//        if (charsetName == null) {
//            charsetName = "UTF-8";
//        }
        if (fileName == null) {
            System.out.println("Set an environment variable named \"FILE_FOR_5LAB\"");
            System.exit(-1);
        }
//        try {
//            System.setOut(new PrintStream(System.out, true, charsetName));
//        } catch (UnsupportedEncodingException e) {
//            System.out.printf("Change CHARSET5 environment variable: UnsupportedEncodingException %s", charsetName);
//        }
    }
}
