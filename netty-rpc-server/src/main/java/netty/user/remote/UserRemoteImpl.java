package netty.user.remote;

import netty.annotation.Remote;
import netty.user.bean.User;
import netty.user.service.UserService;
import netty.utils.Response;
import netty.utils.ResponseUtil;

import javax.annotation.Resource;
import java.util.List;

@Remote
public class UserRemoteImpl implements UserRemote{
    @Resource
    private UserService userService;

    public Response saveUser(User user) {
        System.out.println("Saving user: " + user);
        userService.saveUSer(user);
        return ResponseUtil.setSuccessResponse(user);
    }

    public Response saveUsers(List<User> users) {
        System.out.println("Saving users: " + users);
        userService.saveUsers(users);
        return ResponseUtil.setSuccessResponse(users);
    }

    public Response getNameById(Integer id) {
        System.out.println("Getting name for id: " + id);

        if (id == null) {
            return ResponseUtil.setErrorResponse("Invalid ID");
        }
        return ResponseUtil.setSuccessResponse("User name for ID " + id);
    }
}
