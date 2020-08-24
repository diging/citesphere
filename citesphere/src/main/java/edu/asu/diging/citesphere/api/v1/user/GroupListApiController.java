package edu.asu.diging.citesphere.api.v1.user;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class GroupListApiController extends V1Controller {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private ObjectMapper objectMapper;


    @RequestMapping(value = { "/groups" }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE } )
    public ResponseEntity<String> list(Principal principal) {
        IUser user = userManager.findByUsername(principal.getName());
        if (user == null) {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
        
        List<ICitationGroup> groups = citationManager.getGroups(user);
        List<JsonCitationGroup> jsonGroups = groups.stream().map(g -> createGroup(g)).collect(Collectors.toList());
        
        String jsonResponse = "";
        try {
            jsonResponse = objectMapper.writeValueAsString(jsonGroups);
        } catch (IOException e) {
            logger.error("Unable to process JSON response ", e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(jsonResponse, HttpStatus.OK);
    }
    
    private JsonCitationGroup createGroup(ICitationGroup group) {
        JsonCitationGroup jsonGroup = new JsonCitationGroup();
        jsonGroup.setCreated(group.getCreated());
        jsonGroup.setDescription(group.getDescription());
        jsonGroup.setId(group.getGroupId());
        jsonGroup.setLastModified(group.getLastModified());
        jsonGroup.setName(group.getName());
        jsonGroup.setNumItems(group.getNumItems());
        jsonGroup.setOwner(group.getOwner());
        jsonGroup.setType(group.getType());
        jsonGroup.setUrl(group.getUrl());
        jsonGroup.setVersion(group.getVersion());
        return jsonGroup;
    }
    
    class JsonCitationGroup {
        private long id;
        private String name;
        private long version;
        private String created;
        private String lastModified;
        private long numItems;
        private long owner;
        private String type;
        private String description;
        private String url;
        
        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public long getVersion() {
            return version;
        }
        public void setVersion(long version) {
            this.version = version;
        }
        public String getCreated() {
            return created;
        }
        public void setCreated(String created) {
            this.created = created;
        }
        public String getLastModified() {
            return lastModified;
        }
        public void setLastModified(String lastModified) {
            this.lastModified = lastModified;
        }
        public long getNumItems() {
            return numItems;
        }
        public void setNumItems(long numItems) {
            this.numItems = numItems;
        }
        public long getOwner() {
            return owner;
        }
        public void setOwner(long owner) {
            this.owner = owner;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
    }
}
