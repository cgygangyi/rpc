package netty.client;

import java.util.concurrent.atomic.AtomicLong;

public class ClientRequest {
    private final long id;
    private final Object content;
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    public ClientRequest(Object content) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public Object getContent() {
        return content;
    }
}
