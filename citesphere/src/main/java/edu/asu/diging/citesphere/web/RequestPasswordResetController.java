package edu.asu.diging.citesphere.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.asu.diging.citesphere.core.exceptions.UserDoesNotExistException;
import edu.asu.diging.citesphere.core.service.IPasswordResetTokenService;

@Controller
public class RequestPasswordResetController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPasswordResetTokenService tokenService;

    @RequestMapping(value="/login/reset", method=RequestMethod.POST)
    public ModelAndView handlePost(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getParameter("email");
        logger.debug("Attempting password reset for " + email);
        if (email == null || email.trim().isEmpty()) {
            logger.debug("No email provided.");
            ModelAndView model = new ModelAndView();
            model.setViewName("/?error=passwordResetNoEmail");
            return model;
        }
        
        try {
            tokenService.resetPassword(email);
            logger.debug("Password reset initiated.");
        } catch (UserDoesNotExistException | IOException ex) {
            logger.error("A password reset was attempted for " + email, ex);
        }
        
        ModelAndView model = new ModelAndView();
        model.setViewName("redirect:/?success=passwordResetInitiated");
        return model;
    }
    
    @RequestMapping(value="/login/reset", method=RequestMethod.GET)
    public String handleGet() {
        return "resetPassword";
    }

}