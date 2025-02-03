package netty.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class DefaultFuture {
    public static ConcurrentHashMap<Long, DefaultFuture> allDefaultFuture = new ConcurrentHashMap<>();
    public static final Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private Response response;

    public DefaultFuture(ClientRequest request) {
        allDefaultFuture.put(request.getId(), this);
    }

    public Response get() {
        lock.lock();
        try {
            while (!isDone()) {
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return this.response;
    }

    public static void received(Response response) {
        DefaultFuture future = allDefaultFuture.get(response.getId());
        if (future != null) {
            lock.lock();
            try {
                future.response = response;
                future.condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    public boolean isDone() {
        return response != null;
    }

}
