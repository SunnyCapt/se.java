package capt.sunny.labs.l6.serv;

import capt.sunny.labs.l6.CreatureMap;
import capt.sunny.labs.l6.IOTools;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

public class Server implements Runnable {
    //Изменять в зависимоссти от оси на которой заупскают
    public static JSONObject config;
    private static String DATA_DIR;
    private static String HOST;
    private static int PORT;
    private static int numberOfAllowedRequests;

    static {
        try {
            config = IOTools.getJsonFile("data/config.json");
        } catch (Exception e) {
            System.out.println("\nCannt find/read config file(data/config.csv), sorry..\n");
            System.exit(-1);
        }
    }

    static {
        try {
            DATA_DIR = Server.config.getString("working_directory");
            HOST = Server.config.getString("host_name");
            PORT = Server.config.getInt("port_number");
            numberOfAllowedRequests = Server.config.getInt("number_of_allowed_requests");
        } catch (Exception e) {
            System.out.println("\nwrong config: " + e.getMessage());
            System.exit(-1);
        }
    }

    CreatureMap creatureMap = new CreatureMap();
    private String message = "";

    public Server(Runtime runtime) {
        runtime.addShutdownHook(new Thread(() -> System.out.println("\nBye, kitty!")));
    }
    public Server(Runtime runtime, String _HOST, int _PORT) {
        runtime.addShutdownHook(new Thread(() -> System.out.println("\nBye, kitty!")));
        HOST = _HOST;
        PORT = _PORT;
    }

    public static String getDataDirectory() {
        return DATA_DIR;
    }

    public static int getNumberOfAllowedRequests() {
        return numberOfAllowedRequests;
    }

    public static void main(String[] args) {
        new Server(Runtime.getRuntime()).run();
    }


    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            main_cycle:
            while (true) {
                message = "";
                try (ServerSocket server = new ServerSocket(PORT, 1000, Inet4Address.getByName(HOST))) {
                    System.out.printf("Server is running on port %d\n", server.getLocalPort());
                    accept_cycle:
                    while (!server.isClosed()) {
                        message = "";
                        try {
                            readServCommand(bufferedReader);
                            accept(server);
                        } catch (NullPointerException e) {
                            message = "";
                            break main_cycle;
                        } catch (IOException e) {
                            System.out.println("\n[local] Cannt read admin messages: " + e.getMessage());
                        } catch (Exception e) {
                            exit("Unknow exception: " + e.getMessage());
                        }
                    }
                } catch (IOException | IllegalArgumentException e) {
                    try {
                        System.out.println("\n[local] " + e.getMessage());
                        System.out.println("\nEnter host and port: 127.0.0.10:1337\n");
                        server_up_cycle:
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
                        message = "";
                        break main_cycle;
                    }
                } catch (NullPointerException e) {
                    message = "";
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
        System.exit(0);
    }

    private void accept(ServerSocket _server) throws IOException {
        Socket client = _server.accept();
        Thread thread = new Thread(new MSocket(client, creatureMap));
        //thread.setDaemon(true);
        thread.start();
    }

}


