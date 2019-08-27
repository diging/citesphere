package edu.asu.diging.citesphere.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({ CannotFindCitationException.class })
    public String handleCitationKeyDoesNotExist() {
        return "error/citation/notFound";
    }
}
