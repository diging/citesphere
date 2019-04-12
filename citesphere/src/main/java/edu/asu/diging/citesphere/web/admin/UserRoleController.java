package edu.asu.diging.citesphere.web.admin;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import edu.asu.diging.citesphere.core.model.Role;
import edu.asu.diging.citesphere.core.user.IUserManager;

@Controller
public class UserRoleController {

    @Autowired
    private IUserManager userManager;

    @RequestMapping(value="/admin/user/{username}/admin", method=RequestMethod.POST)
    public String makeAdmin(@PathVariable("username") String username, Principal principal) {
        userManager.addRole(username, principal.getName(), Role.ADMIN);
        return "redirect:/admin/user/list";
    }
    
    @RequestMapping(value="/admin/user/{username}/admin/remove", method=RequestMethod.POST)
    public String removeAdmin(@PathVariable("username") String username, Principal principal) {
        userManager.removeRole(username, principal.getName(), Role.ADMIN);
        return "redirect:/admin/user/list";
    }
    
    @RequestMapping(value="/admin/user/{username}/disable", method=RequestMethod.POST)
    public String disableUser(@PathVariable("username") String username, Principal principal) {
        userManager.disableUser(username, principal.getName());
        return "redirect:/admin/user/list";
    }
}
