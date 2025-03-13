package edu.asu.diging.citesphere.web.user.errors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {
    @GetMapping("/gilesDocumentError")
    public String showGilesDocumentErrorPage() {
        return "error/gilesDocumentError";
    }
}
