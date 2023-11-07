package edu.asu.diging.citesphere.web.admin.userOAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.citesphere.core.service.oauth.IUserTokenManager;
import edu.asu.diging.citesphere.web.admin.forms.AppForm;
import edu.asu.diging.citesphere.web.admin.forms.UserAccessTokenForm;

@Controller
public class AddUserAccessTokenController {
    @Autowired
    private IUserTokenManager userTokenManager;
    
    @RequestMapping(value="/admin/users/accessTokens/add", method=RequestMethod.GET)
    public String add(@Validated UserAccessTokenForm useerAccessTokenForm, Model model, BindingResult errors, RedirectAttributes redirectAttrs) {
        return "admin/users/accessTokens/add";
    }
}
