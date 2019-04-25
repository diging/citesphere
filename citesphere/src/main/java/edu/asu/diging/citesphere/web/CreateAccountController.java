package edu.asu.diging.citesphere.web;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.exceptions.UserAlreadyExistsException;
import edu.asu.diging.citesphere.core.factory.IUserFactory;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.web.forms.UserForm;
import edu.asu.diging.citesphere.email.IEmailNotificationManager;

@Controller
public class CreateAccountController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private IUserFactory userFactory;
    
    @Autowired
    private IEmailNotificationManager emailNotificationManager;

    @RequestMapping(value = "/register", method=RequestMethod.GET)
    public String get(Model model) {
        model.addAttribute("user", new UserForm());
        return "register";
    }
    
    @RequestMapping(value = "/register", method=RequestMethod.POST)
    public String post(@Valid @ModelAttribute("user") UserForm userForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", userForm);
            return "register";
        }
        
        IUser user = userFactory.createUser(userForm);
        try {
            List<IUser> adminList = new ArrayList<>();
            for(IUser admin: userManager.loadUsersByRole()) {
                if(user.getEmail()!=null && !user.getEmail().equals("")) {
                    adminList.add(admin);
                }
            }
            userManager.create(user);
            emailNotificationManager.sendNewAccountRequestPlacementEmail(user, adminList);
        } catch (UserAlreadyExistsException e) {
            logger.error("User could not be created. Username already in use.");
            model.addAttribute("user", userForm);
            result.rejectValue("username", "username", "Username is already in use.");
            return "register";
        }
        
        return "redirect:/ ";
    }
}
