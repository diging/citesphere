package edu.asu.diging.citesphere.api.v1.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.api.v1.model.impl.ItemDetails;
import edu.asu.diging.citesphere.api.v1.model.impl.ItemWithGiles;
import edu.asu.diging.citesphere.web.forms.CitationForm;


@Controller
public class AddNewItemController extends V1Controller {

    @PostMapping(value = {"items/create1"}, consumes = {
        MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<String> create1(Authentication authentication, @RequestBody ItemWithGiles itemWithGiles) {
        return new ResponseEntity<String>("Success!", HttpStatus.OK);

    }

}