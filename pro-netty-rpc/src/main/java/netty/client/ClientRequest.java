package netty.client;

import java.util.concurrent.atomic.AtomicLong;

public class ClientRequest {
    private final long id;
    private Object content;
    private final AtomicLong aid = new AtomicLong(1);

    public ClientRequest(Object request) {
        this.id = aid.getAndIncrement();
        this.content = request;
    }

    public long getId() {
        return id; 
    }

    public Object getContent() {
        return content;
    }
}
