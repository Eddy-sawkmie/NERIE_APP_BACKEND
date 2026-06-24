package com.nic.nerie.configs;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataAccessResourceFailureException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ControllerAdviceConfig {
    private static final Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private static final Logger authenticationLogger = LoggerFactory.getLogger("AUTHENTICATION_LOGGER");
    private static final Logger authorizationLogger = LoggerFactory.getLogger("AUTHORIZATION_LOGGER");
    private static final Logger dataAccessLogger = LoggerFactory.getLogger("DATA_ACCESS_LOGGER");
    
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public String handleAuthenticationNotFoundException(AuthenticationCredentialsNotFoundException ex) {
        authenticationLogger.error("Error retrieving authentication information.\nMessage {} \nException {}", ex.getMessage(), ex);
        return "redirect:/login?msg=unauthenticated";
    }
    
    @ExceptionHandler(MyAuthenticationCredentialsNotFoundException.class)
    public Object handleMyAuthenticationCredentialsNotFoundException(MyAuthenticationCredentialsNotFoundException ex) {
        authenticationLogger.error(ex.getMessage());

        if (ex.getResourceType().equalsIgnoreCase("page"))
            return "redirect:/login?msg=unauthenticated";
        else if (ex.getResourceType().equalsIgnoreCase("json"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        else
            throw new RuntimeException("Resource type " + ex.getResourceType() + " is not allowed.");
    }

    @ExceptionHandler(MyAuthorizationDeniedException.class)
    public Object handleMyAuthorizationDeniedException(MyAuthorizationDeniedException ex) {
        authorizationLogger.error(ex.getMessage());

        if (ex.getResourceType().equalsIgnoreCase("page"))
            return "redirect:/error/404";
        else if (ex.getResourceType().equalsIgnoreCase("json"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        else
            throw new RuntimeException("Resource type " + ex.getResourceType() + " is not allowed.");
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<String> handleDataAccessFailureException(DataAccessResourceFailureException ex) {
        dataAccessLogger.error("Error accessing data source.\nMessage {} \nException {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body("Something went wrong. " + ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        rootLogger.error("Constraint violation.\nMessage {} \nException {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body("Invalid Request: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        rootLogger.error("Something went wrong.\nMessage {} \nException {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Please try again later.");
    }

    /**
     *
     * Handles missing static resources (e.g., .js.map, .css.map, favicon) to prevent
     * them from falling into the generic Exception handler and clogging the logs with errors.
     * We do NOT log an error here.
     * We simply return a 404 Not Found status.
     *
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
    }

    /**
     * Handles cases where the client (browser) disconnects before the response is fully sent.
     * We return null because the response cannot be written anyway.
     * Log at DEBUG level only, so it doesn't clutter logs
     */
    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException(ClientAbortException ex) {
        rootLogger.debug("Client aborted connection while response was writing: {}", ex.getMessage());
    }
} 
