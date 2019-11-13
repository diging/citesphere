package edu.asu.diging.citesphere.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import edu.asu.diging.citesphere.core.exceptions.TokenExpiredException;
import edu.asu.diging.citesphere.core.service.IPasswordResetTokenService;

@Controller
public class ResetPasswordInitiatedController  {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPasswordResetTokenService tokenService;

   
    @RequestMapping(value="/login/reset/initiated")
    public ModelAndView handleGet(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("user");
        String token = request.getParameter("token");
        if (username == null || username.trim().isEmpty() || token == null || token.trim().isEmpty()) {
            logger.info("No username or no token provided.");
            return generateFailureModel("invalidToken");
        }
        try {
            tokenService.validateToken(token, username);
        } catch(InvalidTokenException | TokenExpiredException ex) {
            logger.warn("Token failed to validate.", ex);
            return generateFailureModel("invalidToken");
            
        }
        logger.debug("Allow password change for user " + username);
        ModelAndView model = new ModelAndView();
        model.setViewName("redirect:/password/reset");
        return model;
    }

    protected ModelAndView generateFailureModel(String errorMsg) {
        ModelAndView model = new ModelAndView();
        model.setViewName("redirect:/?error=" + errorMsg);
        return model;
    }
    
    
}
