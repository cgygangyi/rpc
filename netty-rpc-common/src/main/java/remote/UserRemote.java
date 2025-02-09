package remote;


import protocal.Response;
import bean.User;

import java.util.List;

public interface UserRemote {
    Response saveUser(User user);
    Response saveUsers(List<User> users);
    Response getNameById(Integer id);
}
