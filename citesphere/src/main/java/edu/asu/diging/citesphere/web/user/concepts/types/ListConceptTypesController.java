package edu.asu.diging.citesphere.web.user.concepts.types;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.user.IUserManager;

@Controller
public class ListConceptTypesController {
    
    @Autowired
    private IConceptTypeManager typesManager;
    
    @Autowired
    private IUserManager userManager;

    @RequestMapping("/auth/concepts/types/list")
    public String list(Model model, Principal principal) {
        model.addAttribute("types", typesManager.getAllTypes(userManager.findByUsername(principal.getName())));
        return "auth/concepts/types/list";
    }
}
