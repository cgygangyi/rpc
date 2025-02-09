package core;

import io.netty.channel.ChannelFuture;

public class WeightedServer {
    private String host;
    private int port;
    private int weight;
    private ChannelFuture channelFuture;

    private int currentWeight;
    
    public WeightedServer(String host, int port, int weight, ChannelFuture channelFuture) {
        this.host = host;
        this.port = port;
        this.weight = weight;
        this.currentWeight = weight;
        this.channelFuture = channelFuture;
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public int getCurrentWeight() {
        return currentWeight;
    }
    
    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }
    
    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }
    
    @Override
    public String toString() {
        return "WeightedServer{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", weight=" + weight +
                ", currentWeight=" + currentWeight +
                '}';
    }
} 