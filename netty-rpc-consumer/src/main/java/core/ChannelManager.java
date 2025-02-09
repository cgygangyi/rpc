package core;

import io.netty.channel.ChannelFuture;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelManager {
    public static CopyOnWriteArrayList<ChannelFuture>  channelFutures = new CopyOnWriteArrayList<ChannelFuture>();
    public static CopyOnWriteArrayList<String> realServerPath = new CopyOnWriteArrayList<String>();
    public static AtomicInteger position = new AtomicInteger(0);

    public static void removeChnannel(ChannelFuture channel){
        channelFutures.remove(channel);
    }

    public static void addChnannel(ChannelFuture channel){
        channelFutures.add(channel);
    }
    public static void clearChnannel(){
        channelFutures.clear();
    }

    public static ChannelFuture get(AtomicInteger i) {
        ChannelFuture channelFuture = null;
        int size = channelFutures.size();
        if(i.get()>=size){
            channelFuture = channelFutures.get(0);
            ChannelManager.position= new AtomicInteger(1);
        }else{
            channelFuture = channelFutures.get(i.getAndIncrement());
        }
        return channelFuture;
    }

}