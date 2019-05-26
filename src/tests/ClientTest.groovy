import capt.sunny.labs.l7.client.Client
import org.junit.Test


class ClientTest extends GroovyTestCase {

    @Test
    void testRun() {
        Client client = new Client(Runtime.getRuntime(), new BufferedReader(new StringReader("localhost:1399\n\r" + Commands.all)));
        client.run();

    }
}

enum Commands {
    SHOW("show;"),
    EXIT("exit;"),
    INFO("info;"),
    SAVE("save;"),
    HELP("help;"),
    INSERT("insert {\"creature\":{\"name\":\"%s\",\"age\":250,\"height\":345.34, \"species\":\"human\",\"isLive\":true,\"creationDate\":\"2019-05-18T23:58:40\",\"location\":{\"x\":23.23,\"y\":23.2,\"z\":25.2}}};"),
    ADD_IF_MIN("add_if_min {\"creature\":{\"name\":\"%s\",\"age\":270,\"height\":345.34, \"species\":\"human\",\"isLive\":true,\"creationDate\":\"2019-05-18T23:58:40\",\"location\":{\"x\":23.23,\"y\":23.2,\"z\":25.2}}};"),
    REMOVE_LOWER("remove_lower {\"name\":\"%s\"};"),
    REMOVE("remove {\"name\":\"%s\"};"),
    LOGIN("login {\"nick\":\"Ivan_Alexander\"} {\"password\":\"STupID STudeNt mUSt Die\"};");

    public static String all =  LOGIN.command + "\n\r" +
                                HELP.command + "\n\r" +
                                String.format(ADD_IF_MIN.command, "pers#4") + "\n\r" +
                                String.format(INSERT.command, "pers#4") + "\n\r" +
                                String.format(REMOVE_LOWER.command, "pers#4") + "\n\r" +
                                //String.format(REMOVE.command, "pers#4") + "\n\r" +
                                SHOW.command + "\n\r" +
                                INFO.command + "\n\r" +
                                SAVE.command + "\n\r" +
                                EXIT.command + "\n";
    private String command;

    Commands(String _manual) {
        command = _manual;
    }

}
