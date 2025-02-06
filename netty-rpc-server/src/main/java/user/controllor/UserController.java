package user.controllor;

import javax.annotation.Resource;
import bean.User;
import user.service.UserService;
import utils.Response;
import utils.ResponseUtil;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController {

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
