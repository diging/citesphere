package edu.asu.diging.citesphere.web.admin;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.service.IUserManager;

@Controller
public class ApproveAccountController {
    
    @Autowired
    private IUserManager userManager;

    @RequestMapping("/admin/user/{username}/approve")
    public String approveAccount(@PathVariable("username") String username, Principal principal) {
        userManager.approveAccount(username, principal.getName());
        return "redirect:/admin/user/list";
    }
}
