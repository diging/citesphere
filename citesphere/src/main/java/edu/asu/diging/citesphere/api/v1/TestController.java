package edu.asu.diging.citesphere.api.v1;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Controller
public class TestController extends V1Controller {
    
    @Autowired
    @Qualifier("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMappings;
    
    @PostConstruct
    public void init() {
        System.out.println(handlerMappings.getHandlerMethods());
    }
    

    @RequestMapping("/test")
    public ResponseEntity<JsonArray> test() {
        JsonArray mappings = new JsonArray();
        for (RequestMappingInfo info : handlerMappings.getHandlerMethods().keySet()) {
            JsonElement mapping = new JsonPrimitive(info.getPatternsCondition().toString());
            mappings.add(mapping);
        }
        return new ResponseEntity<>(mappings, HttpStatus.OK);
    }
}
