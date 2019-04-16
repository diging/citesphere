package edu.asu.diging.citesphere.api.endpoints;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Controller
@RequestMapping("/api/v1")
public class TestController {
    
    @Autowired
    @Qualifier("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMappings;
    
    @PostConstruct
    public void init() {
        System.out.println(handlerMappings.getHandlerMethods());
    }
    

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
