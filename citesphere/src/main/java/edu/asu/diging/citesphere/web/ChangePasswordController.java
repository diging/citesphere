package edu.asu.diging.citesphere.web;

import javax.mail.MethodNotSupportedException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.asu.diging.citesphere.core.exceptions.UserDoesNotExistException;
import edu.asu.diging.citesphere.core.model.IUser;

@Controller
public class ChangePasswordController {

    @Autowired
    private edu.asu.diging.citesphere.core.user.IUserManager userManager;

    @RequestMapping(value = "/password/reset", method = RequestMethod.POST)
    public String handlePost(HttpServletRequest request, HttpServletResponse response, Model model) throws UserDoesNotExistException, ServletException {
        Object userObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userObj == null || !(userObj instanceof IUser)) {
            model.addAttribute("error", "notAuthenticated");
            return "redirect:/";
        }

        IUser user = (IUser) userObj;
        String password = request.getParameter("password");
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "noPassword");
            return "redirect:/password/reset";
        }

        String repeatedPassword = request.getParameter("passwordRepeat");
        if (!password.equals(repeatedPassword)) {
            model.addAttribute("error", "notMatching");
            return "redirect:/password/reset";
        }

        userManager.changePassword(user, password);

        request.logout();
        model.addAttribute("success", "PasswordChanged");
        return "redirect:/";
    }

    @RequestMapping(value = "/password/reset", method = RequestMethod.GET)
    public ModelAndView handleGet(HttpServletRequest request, HttpServletResponse response)
            throws MethodNotSupportedException, Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("password/change");
        return model;
    }

}