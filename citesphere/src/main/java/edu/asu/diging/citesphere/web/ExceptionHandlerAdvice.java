package edu.asu.diging.citesphere.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({ CannotFindCitationException.class })
    public String handleCitationKeyDoesNotExist() {
        return "error/citation/notFound";
    }
    
    @ExceptionHandler({AccessForbiddenException.class})
    public String handleForbidden() {
        return "error/403";
    }
    
    @ExceptionHandler({ZoteroHttpStatusException.class})
    public String handleZoteroException() {
        return "error/500";
    }
}
