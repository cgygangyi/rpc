package core;

import io.netty.channel.ChannelFuture;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Comparator;

public class ChannelManager {
    private static final CopyOnWriteArrayList<WeightedServer> servers = new CopyOnWriteArrayList<>();
    
    public static void addServer(String serverInfo, ChannelFuture channelFuture) {
        String[] parts = serverInfo.split("#");
        if (parts.length == 3) {
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
            int weight = Integer.parseInt(parts[2]);
            servers.add(new WeightedServer(host, port, weight, channelFuture));
        }
    }
    
    public static synchronized ChannelFuture get() {
        if (servers.isEmpty()) {
            return null;
        }
        
        // Calculate total weight
        int totalWeight = servers.stream().mapToInt(WeightedServer::getWeight).sum();
        
        // Add weight to current weight
        servers.forEach(server -> 
            server.setCurrentWeight(server.getCurrentWeight() + server.getWeight()));
        
        // Find the server with the maximum current weight
        WeightedServer selected = servers.stream()
            .max(Comparator.comparingInt(WeightedServer::getCurrentWeight))
            .orElse(null);
        
        System.out.println("selected server: " + selected);
            
        if (selected != null) {
            // Subtract total weight from the current weight of the selected server
            selected.setCurrentWeight(selected.getCurrentWeight() - totalWeight);
            return selected.getChannelFuture();
        }

        System.out.println("selected server: " + selected);
        
        return null;
    }
    
    public static void clear() {
        servers.clear();
    }
    
    public static void removeServer(ChannelFuture channelFuture) {
        servers.removeIf(server -> server.getChannelFuture() == channelFuture);
    }
}