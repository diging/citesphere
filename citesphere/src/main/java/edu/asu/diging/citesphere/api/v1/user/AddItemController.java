package edu.asu.diging.citesphere.api.v1.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.web.forms.CitationForm;


@Controller
public class AddItemController extends V1Controller {

    @RequestMapping(value = { "items/create1" })
    public ResponseEntity<String> testCreate(Authentication authentication, Model model, CitationForm form,
            @RequestParam(required = false, value = "index") String index, @RequestParam(defaultValue = "1", required = false, value = "page") int page,@RequestParam(value="collectionId", required=false) String collectionId,
            @RequestParam(defaultValue = "title", required = false, value = "sortBy") String sortBy) {
        return new ResponseEntity<String>("uccess!", HttpStatus.OK);

    }

}