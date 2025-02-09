import annotation.RemoteInvoke;
import remote.UserRemote;
import protocal.Response;
import bean.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RemoteInvokingTest.class)
@ComponentScan("\\")
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
