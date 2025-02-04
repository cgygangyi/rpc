package netty;

import netty.client.ClientRequest;
import netty.client.TcpClient;
import netty.user.bean.User;
import org.junit.Test;

public class TestTcp {
    @Test
    public void testSaveUser() {
        ClientRequest saveRequest = new ClientRequest();
        User user = new User();
        user.setId(1);
        user.setName("John Doe");
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
}