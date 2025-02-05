package netty.user.remote;

import netty.user.bean.User;
import netty.utils.Response;
import java.util.List;

public interface UserRemote {
    Response saveUser(User user);
    Response saveUsers(List<User> users);
    Response getNameById(Integer id);
}
