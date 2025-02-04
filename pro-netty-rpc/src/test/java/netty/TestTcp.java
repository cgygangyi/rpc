package netty;

import netty.client.ClientRequest;
import netty.client.TcpClient;
import netty.user.bean.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestTcp {
    @Test
    public void testSaveUser() {

        ClientRequest saveRequest = new ClientRequest();
        User user = new User(1, "John Doe");
        saveRequest.setCommand("netty.user.controllor.UserController.saveUser");
        saveRequest.setContent(user);

        System.out.println("[Test] Sending save user request:");
        System.out.println("[Test] User ID: " + user.getId());
        System.out.println("[Test] User Name: " + user.getName());

        Object response = TcpClient.send(saveRequest);
        System.out.println("[Test] Save response: " + response);


        // Query user
        ClientRequest queryRequest = new ClientRequest();
        queryRequest.setCommand("netty.user.controllor.UserController.getNameById");
        queryRequest.setContent(1);  // Query user with ID 1

        System.out.println("\n[Test] Sending query user request:");
        System.out.println("[Test] Query user ID: 1");

        Object queryResponse = TcpClient.send(queryRequest);
        System.out.println("[Test] Query response: " + queryResponse);
    }

    @Test
    public void testSaveUsers() {
        ClientRequest saveRequest = new ClientRequest();

        List<User> users = new ArrayList<>();
        users.add(new User(1, "John Doe"));
        users.add(new User(2, "Jane Smith"));

        saveRequest.setCommand("netty.user.controllor.UserController.saveUsers");
        saveRequest.setContent(users);

        System.out.println("[Test] Sending save users request:");

        Object response = TcpClient.send(saveRequest);
        System.out.println("[Test] Save response: " + response);


        // Query user
        ClientRequest queryRequest = new ClientRequest();
        queryRequest.setCommand("netty.user.controllor.UserController.getNameById");
        queryRequest.setContent(1);  // Query user with ID 1

        System.out.println("\n[Test] Sending query user request:");
        System.out.println("[Test] Query user ID: 1");

        Object queryResponse = TcpClient.send(queryRequest);
        System.out.println("[Test] Query response: " + queryResponse);
    }
}