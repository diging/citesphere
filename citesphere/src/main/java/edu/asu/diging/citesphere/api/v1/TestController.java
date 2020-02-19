package edu.asu.diging.citesphere.api.v1;

import java.security.Principal;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Controller
public class TestController extends V1Controller {
    

  

    @RequestMapping("/test")
    public ResponseEntity<ArrayNode> test(Principal principal,HttpServletRequest request, HttpServletResponse response) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode array = mapper.createArrayNode();
        ObjectNode node = mapper.createObjectNode();
        node.put("user", principal.getName());
        array.add(node);
        ServletContext servletContext = request.getSession().getServletContext();
        WebApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        Map<String, HandlerMapping> allRequestMappings = BeanFactoryUtils.beansOfTypeIncludingAncestors(appContext, HandlerMapping.class, true, false);
        for (HandlerMapping handlerMapping : allRequestMappings.values()) {
            if (handlerMapping instanceof RequestMappingHandlerMapping) {
                  RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) handlerMapping;
                Map<RequestMappingInfo, org.springframework.web.method.HandlerMethod>  handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
                  for (RequestMappingInfo requestMappingInfoHandlerMethodEntry : handlerMethods.keySet()) {
                	  if (requestMappingInfoHandlerMethodEntry.getPatternsCondition().getPatterns().stream().anyMatch(p -> p.startsWith("/api/v1"))) {
                          ObjectNode infoNode = mapper.createObjectNode();
                          infoNode.put("info", requestMappingInfoHandlerMethodEntry.toString());
                          array.add(infoNode);
                      }
                  }
            }
        }
      
        return new ResponseEntity<>(array, HttpStatus.OK);
    }
}
