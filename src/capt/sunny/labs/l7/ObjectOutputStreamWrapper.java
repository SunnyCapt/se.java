package capt.sunny.labs.l7;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

public class ObjectOutputStreamWrapper implements Closeable {
    private ObjectOutputStream oos;
    private Lock locker;

    public ObjectOutputStreamWrapper(ObjectOutputStream _oos, Lock _locker) {
        oos = _oos;
        locker =_locker;
    }

    private void lock(){
        locker.lock();
    }

    private void unlock(){
        locker.unlock();
    }

    public void write(byte[] _buff) throws IOException {
        locker.lock();
        IOException e = null;
        try {
            oos.write(_buff);
            oos.flush();
        } catch (IOException _e) {
            e = _e;
        }finally {
            locker.unlock();
        }

        if (e!=null)
            throw e;
    }

    @Override
    public void close() throws IOException {
        oos.close();
    }
}

