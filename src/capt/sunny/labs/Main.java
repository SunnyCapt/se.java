package capt.sunny.labs;

import capt.sunny.labs.l6.IOTools;
import capt.sunny.labs.l6.StringWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
//        JSONObject obj = new JSONObject("{\"name\":\"sdfsjdjhguashdchbe\",\"age\":666,\"height\":179, \"type\":\"afjhsdfsdfskj\",\"isLive\":true,\"creationDate\":\"10 Apr 2019 17:42:26 GMT\",\"location\":{\"x\":59.9506417,\"y\":30.3029817,\"z\":0}}");
//        Creature creature = new Creature(obj);
//        System.out.println(creature);
//        Class c = Class.forName("capt.sunny.labs.l6.serv.Creature");
//        CommandWithObject<Creature> wrapper = new CommandWithObject<>("add_if_min{\"element\":{\"name\":\"sdfsjdjhguashdchbe\",\"age\":666,\"height\":179,\"type\":\"afjhsdfsdfskj\",\"isLive\":true,\"creationDate\":\"10 Apr 2019 17:42:26 GMT\",\"location\":{\"x\":59.9506417,\"y\":30.3029817,\"z\":0}}}", c);
        String str = "abcdfgdtrenchfyyrhdshfndhskc";
        List<byte[]> b = IOTools.getSerializedStringWrapper(str);
        b.add(new byte[]{-84, -19, 0, 5, 115, 114, 0, 32, 99, 97, 112, 116, 46, 115, 117, 110, 110, 121, 46, 108});
        StringWrapper sw = new StringWrapper("abcdfgdtrench", 1, 2);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(sw);
        oos.flush();
        System.out.println(IOTools.getDeserializedStringWrapper(baos.toByteArray()).chunk);

    }
}
