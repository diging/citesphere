package edu.asu.diging.citesphere.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class TestController extends V1Controller {
    
    @Autowired
    @Qualifier("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMappings;
  

    @RequestMapping("/test")
    public ResponseEntity<ArrayNode> test() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode array = mapper.createArrayNode();
        for (RequestMappingInfo info : handlerMappings.getHandlerMethods().keySet()) {
            if (info.getPatternsCondition().getPatterns().stream().anyMatch(p -> p.startsWith("/api/v1"))) {
                ObjectNode infoNode = mapper.createObjectNode();
                infoNode.put("info", info.toString());
                array.add(infoNode);
            }
        }
        return new ResponseEntity<>(array, HttpStatus.OK);
    }
}
