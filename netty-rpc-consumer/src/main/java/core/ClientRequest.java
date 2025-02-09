package core;

import protocal.Request;

import java.util.concurrent.atomic.AtomicLong;

public class ClientRequest extends Request {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    
    public ClientRequest() {
        this.setId(ID_GENERATOR.getAndIncrement());
    }

    public ClientRequest(Object content) {
        this.setId(ID_GENERATOR.getAndIncrement());
        this.setContent(content);
    }
}
