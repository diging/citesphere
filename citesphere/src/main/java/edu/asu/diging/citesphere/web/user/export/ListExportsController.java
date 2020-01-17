package edu.asu.diging.citesphere.web.user.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.export.IExportManager;
import edu.asu.diging.citesphere.model.IUser;

@Controller
public class ListExportsController {
    
    @Autowired
    private IExportManager exportManager;

    @RequestMapping(value="/auth/exports")
    public String list(Authentication authentication, Model model, @RequestParam(value="page", defaultValue="1", required=false) int page) {
        page = page-1; // we start counting at 1 in GUI
        if (page < 0 ) {
            page = 0;
        }
        IUser user = (IUser) authentication.getPrincipal();
        model.addAttribute("tasks", exportManager.getTasks(user, page));
        model.addAttribute("total", exportManager.getTasksTotalPages(user));
        model.addAttribute("page", page+1);
        return "auth/exports";
    }
}
