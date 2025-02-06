package core;

import java.util.concurrent.atomic.AtomicLong;

public class ClientRequest {
    private final long id;
    private Object content;
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    private String command;
    
    public ClientRequest() {
        this.id = ID_GENERATOR.getAndIncrement();
    }

    public ClientRequest(Object content) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.content = content;
    }


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }


    public long getId() {
        return id;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
