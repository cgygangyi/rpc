package client.user.remote;


import client.core.param.Response;
import client.user.bean.User;

import java.util.List;

public interface UserRemote {
    Response saveUser(User user);
    Response saveUsers(List<User> users);
    Response getNameById(Integer id);
}
