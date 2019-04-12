package capt.sunny.labs.l6.client;

import capt.sunny.labs.l6.IOTools;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {
    static String charsetName;

    public static void main(String[] args) {
        setClientConfig();
        int attNum = 1;
        final boolean[] closeApp = {false};
        Thread ctrlC = new Thread() {
            @Override
            public void run() {
                //saving before exit
                closeApp[0] = true;
                System.exit(-1);
            }
        };

        while (true) {
            try (Socket socket = new Socket("localhost", 1337);
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, charsetName));
                 DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                 DataInputStream inputStream = new DataInputStream(socket.getInputStream());) {
                closeApp[0] = false;
                attNum = 1;

                Runtime.getRuntime().addShutdownHook( ctrlC);
                String multilineCommand;
                String resp;
                System.out.print("Sign of the end of the command - ';'\n\n>>> ");

                Thread.sleep(100);
                //exit after saving
                if (closeApp[0]) {
                    outputStream.writeUTF("save");
                }

                for (; ; ) {

                    multilineCommand = IOTools.getMultiline(bufferedReader);
                    outputStream.writeUTF(multilineCommand);
                    Thread.sleep(300);
                    resp = inputStream.readUTF();
                    System.out.println(resp);
                    System.out.print(">>> ");
                }
            } catch (SocketException e) {
                if (attNum < 2) {
                    System.out.printf("No server connection. Trying to connect: committed %d/8 attempts.\n", attNum);
                    attNum++;
                    try {
                        System.out.println("Connecting...");
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                    }
                } else {
                    System.out.println("No server connection. The available number of attempts has been exhausted.");
                    System.out.println("point 1");
                    Runtime.getRuntime().removeShutdownHook(ctrlC);
                    System.exit(-1);

                }
            } catch (UnknownHostException e) {
                System.out.println("Faild connenction: unknown host");
                System.exit(-1);
            } catch (IOException e) {
                System.out.print("Failed to read(write) input(output) stream: " + e.getMessage());
                System.exit(-1);
            } catch (IllegalArgumentException e) {
                System.out.println("Faild connenction: wrong port value");
                System.exit(-1);
            } catch (InterruptedException e) {
                System.out.print(e.getMessage());
                System.exit(-1);
            }
        }
    }

    public static void setClientConfig() {
        charsetName = System.getenv().get("CHARSET5");
        if (charsetName == null) {
            charsetName = "UTF-8";
        }
        try {
            System.setOut(new PrintStream(System.out, true, charsetName));
        } catch (UnsupportedEncodingException e) {
            System.out.printf("Change CHARSET5 environment variable: UnsupportedEncodingException %s", charsetName);
        }
    }

}


/*
Creature's structure
{
    "name": "_name",
    "age": 2019,
    "height": 100.0,
    "type": "_type",
    "isLive": true,
    "creationDate": "createionDate",
    "location": {
        "x": 1.0,
        "y": 2.0,
        "z": 3.0
     }
}
 */
/*
"key","name","age","height","type","isLive","x","y","z","creationDate"
*/

/*
              {
                "key":"dganerjgnagaekj"
              }{
                "element":{
                             "name":"sdfsjdjhguashdchbe",
                             "age":666,
                             "height":179,
                             "type":"afjhsdfsdfskj",
                             "isLive":true,
                             "creationDate":"10 Apr 2019 17:42:26 GMT",
                             "location":{
                                          "x":59.9506417,
                                          "y":30.3029817,
                                          "z":0
                                        }
                          }
               };

              {
                "key":"god2"
              }{
                "element":{
                             "name":"p",
                             "age":666,
                             "height":178,
                             "type":"animal",
                             "isLive":true,
                             "creationDate":"10 Apr 2019 17:42:26 GMT",
                             "location":{
                                          "x":59.9506417,
                                          "y":30.3029817,
                                          "z":0
                                        }
                          }
               };
 */
/*
"key1","name1",323,345.34,"human",true,23.23,23.2,25.2
"key2","name2",300,345.34,"human",true,23.23,23.2,25.2
"key3","name3",290,345.34,"human",true,23.23,23.2,25.2
 */