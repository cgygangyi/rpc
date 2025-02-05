import client.annotation.RemoteInvoke;
import client.core.param.Response;
import client.user.bean.User;
import client.user.remote.UserRemote;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RemoteInvokingTest.class)
@ComponentScan("client")
public class RemoteInvokingTest {

    @RemoteInvoke
    private UserRemote userRemote;

    @Test
    public void testSaveUser() {
        User user = new User(1, "John Doe");
        Response response = userRemote.saveUser(user);
        System.out.println("Response from server: " + response.getContent());
    }
}
