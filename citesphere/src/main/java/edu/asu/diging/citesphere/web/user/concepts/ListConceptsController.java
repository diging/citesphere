package edu.asu.diging.citesphere.web.user.concepts;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.user.IUserManager;

@Controller
public class ListConceptsController {
    
    @Autowired
    private ICitationConceptManager conceptManager;
    
    @Autowired
    private IUserManager userManager;

    @RequestMapping("/auth/concepts/list")
    public String list(Model model, Principal principal) {
        model.addAttribute("concepts", conceptManager.findAll(userManager.findByUsername(principal.getName())));
        return "auth/concepts/list";
    }
}
