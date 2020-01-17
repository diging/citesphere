package edu.asu.diging.citesphere.web.admin.oauth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.asu.diging.citesphere.core.service.oauth.GrantTypes;
import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;
import edu.asu.diging.citesphere.core.service.oauth.OAuthCredentials;
import edu.asu.diging.citesphere.core.service.oauth.OAuthScope;
import edu.asu.diging.citesphere.web.admin.forms.AppForm;

@Controller
public class AddOAuthClientController {
    
    @Autowired
    private IOAuthClientManager clientManager;
    
    private List<String> allowedGrantTypes;
    
    @PostConstruct
    public void init() {
        allowedGrantTypes = new ArrayList<String>();
        allowedGrantTypes.add(GrantTypes.AUTHORIZATION_CODE);
        allowedGrantTypes.add(GrantTypes.CLIENT_CREDENTIALS);
    }

    @RequestMapping(value="/admin/apps/add", method=RequestMethod.GET)
    public String show(Model model) {
        model.addAttribute("appForm", new AppForm());
        return "admin/apps/add";
    }
    
    @RequestMapping(value="/admin/apps/add", method=RequestMethod.POST)
    public String add(@Validated AppForm appForm, Model model, BindingResult errors, RedirectAttributes redirectAttrs) {
        if (!allowedGrantTypes.contains(appForm.getGrantType())) {
            errors.rejectValue("grantType", "app.creation.invalid.granttype");
            model.addAttribute("appForm", appForm);
            return "admin/apps/add";
        }
        
        Set<String> grantTypes = new HashSet<>();
        grantTypes.add(appForm.getGrantType());
        if (appForm.getGrantType().equals(GrantTypes.AUTHORIZATION_CODE)) {
            grantTypes.add(GrantTypes.REFRESH_TOKEN);
        }
        OAuthCredentials creds = clientManager.create(appForm.getName(), appForm.getDescription(), Arrays.asList(OAuthScope.READ), grantTypes, appForm.getRedirectUrl());
        model.addAttribute("clientId", creds.getClientId());
        model.addAttribute("secret", creds.getSecret());
        model.addAttribute("clientName", appForm.getName());
        model.addAttribute("description", appForm.getDescription());
        return "admin/apps/details";
    }
}
