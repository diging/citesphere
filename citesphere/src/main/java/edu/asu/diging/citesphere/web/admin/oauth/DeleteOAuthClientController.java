package edu.asu.diging.citesphere.web.admin.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;

@Controller
public class DeleteOAuthClientController {

    @Autowired
    private IOAuthClientManager clientManager;

    @RequestMapping(value = "/admin/apps/{clientId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteApp(@PathVariable("clientId") String clientId) {
        clientManager.deleteClient(clientId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
