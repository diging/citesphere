package edu.asu.diging.citesphere.api.v1.user;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.api.v1.V1Controller;

@Controller
public class ProfileController extends V1Controller {

    @RequestMapping(value="/user/profile")
    public ResponseEntity<String> getProfile(Principal principal) {
        System.out.println(principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
