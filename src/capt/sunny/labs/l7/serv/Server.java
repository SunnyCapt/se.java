package capt.sunny.labs.l7.serv;

import capt.sunny.labs.l7.IOTools;
import capt.sunny.labs.l7.serv.db.DB;
import capt.sunny.labs.l7.serv.db.DBException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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
            config = IOTools.getJsonFile("config.json");
        } catch (Exception e) {
            System.out.println("\nCannt find/read config file(config.csv), sorry..\n");
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

//        DB db = new DB("localhost", 3128,"studs");
//        db.connect("s278068", "taq704");


    }

    private int n = 0;
    private DB db = null;
    private DataManager dataManager; // = IOTools.getCreatureMapFromFile(fileName, "UTF-8");
    private String message = "";


    public Server(Runtime runtime, String[] args) {
        init(args);
        runtime.addShutdownHook(new Thread(() -> System.out.println("\nBye, kitty!")));
    }


    public Server(Runtime runtime, String _HOST, int _PORT, String[] args) {
        init(args);
        runtime.addShutdownHook(new Thread(() -> {
            System.out.println("\nBye, kitty!");
            // try {db.close();} catch (DBException e) {}
        }));
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
        new Server(Runtime.getRuntime(), args).run();
    }

    private void init(String[] args) {
        initDB(args);
        initCollection();
    }

    private void initDB(String[] args) {
        db = new DB(config.getString("db_host"), config.getInt("db_port"), config.getString("db_name"));
        try {
            db.connect(args[0], args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Cannt connect to db: please set in start arguments login and password of db");
            System.exit(-1);
        } catch (DBException e) {
            System.out.println("Cannt connect to db: " + e.getMessage());
            System.exit(-1);
        }
        //creatureMap = IOTools.getCreatureMapFromDB();
    }

    private void initCollection() {
        try {
            if (db == null)
                throw new DBException("Cannt load collection from db: db not initialized");
            dataManager = new DataManager(db);
            dataManager.loadCollection();

            Thread tokenCheckerThread = new Thread(new TokenChecker(dataManager));
            tokenCheckerThread.setDaemon(true);
            tokenCheckerThread.start();

        } catch (DBException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
            e.printStackTrace();
            //FIX IT !!!!!
        }
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
        Thread thread = new Thread(new MSocket(client, dataManager));
        thread.setName("thread" + n);
        n++;
        //thread.setDaemon(true);
        thread.start();
    }

}

class TokenChecker implements Runnable {

    private DataManager dataManager;

    TokenChecker(DataManager _dataManager) {
        dataManager = _dataManager;
    }

    @Override
    public void run() {
        Map<String, Boolean> userStatus = new HashMap<>();
        dataManager.getUsers().stream().filter(c -> !(
                c.getUser().getNick() == null ||
                        c.getUser().getToken() == null ||
                        c.getUser().getNick().isEmpty() ||
                        c.getUser().getToken().isEmpty())
        ).forEach(c -> userStatus.put(c.getUser().getNick(), c.getUser().isTokenValid()));
        while (true) {
            dataManager.getUsers().forEach(cc -> {
                Boolean status = cc.getUser().isTokenValid();
                if (cc.getUser().getNick() != null && userStatus.keySet().contains(cc.getUser().getNick())) {
                    if (!userStatus.get(cc.getUser().getNick()).equals(status)) {
                        if (status) {
                            System.out.println("\n\n" + cc.getUser().getNick() + " joined " + "\n\n");
                            sendToEach(cc.getUser().getNick() + " joined ", cc);
                        } else {
                            {
                                System.out.println("\n\n" + cc.getUser().getNick() + " left us "
                                        + "\n\n");
                                sendToEach(cc.getUser().getNick() + " joined ", cc);
                            }
                            userStatus.remove(cc.getUser().getNick());
                        }
                    }
                } else {
                    if (!(cc.getUser().getNick() == null || !cc.getUser().getNick().isEmpty())) {
                        userStatus.put(cc.getUser().getNick(), cc.getUser().isTokenValid());
                    }
                }
            });
            try {
                Thread.sleep(2);
            } catch (InterruptedException ignored) {
            }
        }
    }


    private void sendToEach(String _message, UserWrapper without) {
        dataManager.getUsers().stream().filter(c -> c!=null&&c.getUser()!=null&&c.getUser().equals(without.getUser())).forEach(c -> {
            try {
                IOTools.sendObject(c.getOOSW(), "{us}"+ without.getUser().getNick() + _message, String.class.getName());
            } catch (Exception e) {
                System.out.println("\n[local]: " + c.getUser().getNick() + " died...: " + e.getMessage());
            }
        });
    }
}
