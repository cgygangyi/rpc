package netty.user.controllor;

import javax.annotation.Resource;
import netty.user.bean.User;
import netty.user.service.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    @Resource
    private UserService userService;

    public void saveUser(User user) {
        System.out.println("Saving user: " + user);
        userService.saveUSer(user);
    }

    public String getNameById(Integer id) {
        System.out.println("Getting name for id: " + id);
        if (id == null) {
            return "Invalid ID";
        }
        return "User name for ID " + id;
    }
}
