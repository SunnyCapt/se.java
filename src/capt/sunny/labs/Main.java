package capt.sunny.labs;

import javax.management.StandardEmitterMBean;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//import capt.sunny.labs.l6.StringWrapper;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
//        JSONObject obj = new JSONObject("{\"name\":\"sdfsjdjhguashdchbe\",\"age\":666,\"height\":179, \"type\":\"afjhsdfsdfskj\",\"isLive\":true,\"creationDate\":\"10 Apr 2019 17:42:26 GMT\",\"location\":{\"x\":59.9506417,\"y\":30.3029817,\"z\":0}}");
//        Creature creature = new Creature(obj);
//        System.out.println(creature);
//        Class c = Class.forName("capt.sunny.labs.l6.serv.Creature");
//        CommandWithObject<Creature> wrapper = new CommandWithObject<>("add_if_min{\"element\":{\"name\":\"sdfsjdjhguashdchbe\",\"age\":666,\"height\":179,\"type\":\"afjhsdfsdfskj\",\"isLive\":true,\"creationDate\":\"10 Apr 2019 17:42:26 GMT\",\"location\":{\"x\":59.9506417,\"y\":30.3029817,\"z\":0}}}", c);
//        String str = "abcdfgdtrenchfyyrhdshfndhskc";
//        List<byte[]> b = IOTools.getSerializedStringWrapper(str);
//        b.add(new byte[]{-84, -19, 0, 5, 115, 114, 0, 32, 99, 97, 112, 116, 46, 115, 117, 110, 110, 121, 46, 108});
//        StringWrapper sw = new StringWrapper("abcdfgdtrench", 1, 2);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream(1);
//        ObjectOutputStream oos = new ObjectOutputStream(baos);
//        oos.writeObject(sw);
//        oos.flush();
//        System.out.println(IOTools.getDeserializedStringWrapper(baos.toByteArray()).chunk);
//        TIntList l = new TIntArrayList();
//        byte[] a = Arrays.copyOfRange(new byte[]{1,2,3,4,5,6,7,8}, 1,2);
//        System.out.println(Arrays.toString(a));
        //byte[] wrp = IOTools.getSerializedObj(new Wrapper(IOTools.getSerializedObj("show;"), 0, 1, String.class.getName()));
//        byte[] str = IOTools.getSerializedObj("show;");
//        //byte[] wrp = new byte[]{-84, -19, 0, 5, 115, 114, 0, 26, 99, 97, 112, 116, 46, 115, 117, 110, 110, 121, 46, 108, 97, 98, 115, 46, 108, 54, 46, 87, 114, 97, 112, 112, 101, 114, 73, 105, -127, -27, -121, 110, 95, 109, 2, 0, 4, 73, 0, 11, 99, 104, 117, 110, 107, 78, 117, 109, 98, 101, 114, 73, 0, 16, 116, 111, 116, 97, 108, 67, 104, 117, 110, 107, 78, 117, 109, 98, 101, 114, 91, 0, 5, 99, 104, 117, 110, 107, 116, 0, 2, 91, 66, 76, 0, 9, 99, 108, 97, 115, 115, 78, 97, 109, 101, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 0, 0, 0, 0, 0, 0, 0, 1, 117, 114, 0, 2, 91, 66, -84, -13, 23, -8, 6, 8, 84, -32, 2, 0, 0, 120, 112, 0, 0, 0, 12, -84, -19, 0, 5, 116, 0, 5, 115, 104, 111, 119, 59, 116, 0, 16, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 83, 116, 114, 105, 110, 103};
//        byte[] wrp = new byte[]{-84, -19, 0, 5, 115, 114, 0, 26, 99, 97, 112, 116, 46, 115, 117, 110, 110, 121, 46, 108, 97, 98, 115, 46, 108, 54, 46, 87, 114, 97, 112, 112, 101, 114, 73, 105, -127, -27, -121, 110, 95, 109, 2, 0, 4, 73, 0, 11, 99, 104, 117, 110, 107, 78, 117, 109, 98, 101, 114, 73, 0, 16, 116, 111, 116, 97, 108, 67, 104, 117, 110, 107, 78, 117, 109, 98, 101, 114, 91, 0, 5, 99, 104, 117, 110, 107, 116, 0, 2, 91, 66, 76, 0, 9, 99, 108, 97, 115, 115, 78, 97, 109, 101, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 0, 0, 0, 2, 0, 0, 0, 6, 117, 114, 0, 2, 91, 66, -84, -13, 23, -8, 6, 8, 84, -32, 2, 0, 0, 120, 112, 0, 0, 0, 20, -46, -25, 2, 0, 3, 76, 0, 14, 102, 105, 114, 115, 116, 80, 97, 114, 97, 109, 101, 116, 116, 0, 26, 99, 97, 112, 116, 46, 115, 117, 110, 110, 121, 46, 108, 97, 98, 115, 46, 108, 54, 46, 67, 111, 109, 109, 97, 110, 100,0,0,0,0,0,0,0,0,0,0,0,0,0};

//        Command command = Command.getCommand("show");
//        List<Wrapper<Command>> wrappedSerializedCommand = WrapperUtils.wrapUp(IOTools.<Command>getSerializedObj(command), Command.class.getName());
//        for (int i = 0; i < wrappedSerializedCommand.size(); i++) {
//            IOTools.getSerializedObj(wrappedSerializedCommand.get(i));
//        }
//         Command command = Command.getCommand("show");
//        List<Wrapper> wsd = WrapperUtils.wrapUp(IOTools.<Command>getSerializedObj(command), Command.class.getName());
//
//        byte[] c = new byte[] {-84, -19, 0, 5, 115, 114, 0, 26, 99, 97, 112, 116, 46, 115, 117, 110, 110, 121, 46, 108, 97, 98, 115, 46, 108, 54, 46, 67, 111, 109, 109, 97, 110, 100, -68, 81, -84, -60, -34, 36, -46, -25, 2, 0, 3, 76, 0, 14, 102, 105, 114, 115, 116, 80, 97, 114, 97, 109, 101, 116, 101, 114, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 76, 0, 4, 110, 97, 109, 101, 113, 0, 126, 0, 1, 76, 0, 15, 115, 101, 99, 111, 110, 100, 80, 97, 114, 97, 109, 101, 116, 101, 114, 113, 0, 126, 0, 1, 120, 112};
//        System.out.println("c");
//        System.out.println(Arrays.toString(c));
//        System.out.println(WrapperUtils.<Command>getDeserializedObject(wsd).name);
//       byte[] a = new byte[20];
//        System.arraycopy(new byte[]{1,2,3,4}, 0, a, 0, 20);
//       System.arraycopy(new byte[]{1,2,3,4}, 0, 4, 0, 4);
//        System.out.println();
//        System.out.println("str");
//        System.out.println(Arrays.toString(str));
        //new NioMain().run();
//        CreatureMap cmap = IOTools.getCreatureMapFromFile("data/data.csv", "UTF-8");
//        byte[] a = IOTools.getSerializedObj(cmap);
//        System.out.println(Arrays.toString(a));
////        System.out.println(cmap.map.entrySet().stream()
////                .min().get().getValue());
//            new NioMain().run();
//        String cLine = "insert {\n" +
//                "\t       \"key\":\"key0\"\n" +
//                "\t      } {\n" +
//                "\t         \"element\":{\n" +
//                "\t                   \"name\":\"name3\",\n" +
//                "\t                   \"age\":270,\n" +
//                "\t                   \"height\":345.34,\n" +
//                "\t                   \"type\":\"human\",\n" +
//                "\t                   \"isLive\":true,\n" +
//                "\t                   \"creationDate\":\"10 Apr 2019 17:42:26 GMT\",\n" +
//                "\t                   \"location\":{\n" +
//                "\t                           \"x\":23.23,\n" +
//                "\t                           \"y\":23.2,\n" +
//                "\t                           \"z\":25.2\n" +
//                "\t                          }\n" +
//                "\t                   }\n" +
//                "\t          };";
//        System.out.println(cLine);
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
//        Command command = CommandUtils.getCommand(IOTools.getMultiline(bufferedReader));
//        System.out.println(IOTools.<Command>getDeserializedObject(IOTools.getSerializedObj(command)));
//        System.out.println();
//        long a = 2342;
//        System.out.println("as"+a);
//        SocketChannel channel = capt.sunny.labs.l6.client.Main.getChannel("localhost", 1337);
//        channel.write(ByteBuffer.wrap("test".getBytes()));
//        ServerSocket  server = new ServerSocket(1337, 1000, Inet4Address.getByName("localhost"));
//        Socket client = server.accept();
//        System.out.println("accepted");
//        InputStream in = client.getInputStream();
//        Command command = CommandUtils.readCommand(in);
//        System.out.println("get command");
//        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
//        oos.writeObject("test");
//        oos.flush();
//        client.close();
//        server.close();
//            Thread thr = new Thread((Runnable) new capt.sunny.labs.l4.Main());
//            System.out.println("[??????]"+thr.getState());
//            thr.start();
//            System.out.println("[??????]"+thr.getState());
//            thr.interrupt();
//            System.out.println("[??????]"+thr.getState());
//            Thread.sleep(5000);
//            System.out.println("[??????]"+thr.getState());
//
//        S str = new S();
//        new Thread(()->{
//            try {
//                Thread.sleep(2000);
//                System.out.println("str was notify");
//                str.notify();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
//        System.out.println("str was block");
//        str.wait();
//        java.util.concurrent.
        Semaphore sem = new Semaphore(1, true);
        Lock lock = new ReentrantLock();
        for (int i =0; i<10; i+=2){
            S s1 = new S(sem, i);
            S s2 = new S(sem, i+1);
            s1.start();
            s2.start();
        }



    }

    public static synchronized void test() {
        System.out.println("test");
    }
}

class S extends Thread{
    Semaphore sem;
    Lock lock;
    int val;

    S(Semaphore _sem, int i){
        sem = _sem;
        val = i;
    }

    public S(Lock _lock, int i) {
        lock = _lock;
        val = i;
    }

    @Override
    public void run(){
        try {
            sem.acquire();
           // lock.lockInterruptibly();
            System.out.println(val);
        } catch (InterruptedException e) {
            System.out.println("я прервался" + val);
        }finally {
            sem.release();
            //lock.unlock();
        }
    }

    public void check(){
        try {
            sem.acquire();
            System.out.println(val%2==0?"четный":"нечетный");
        }catch (InterruptedException e) {
            System.out.println("я прервался" + val);
        }finally {
            sem.release();
            //lock.unlock();
        }
    }
}