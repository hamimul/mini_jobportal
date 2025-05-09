package com.example.jobportal.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Determine if the request is an API request (expects JSON) or a web request (expects HTML)
     */
    private boolean isApiRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String contentType = request.getHeader("Content-Type");
        String requestUri = request.getRequestURI();

        return (accept != null && accept.contains("application/json")) ||
                (contentType != null && contentType.contains("application/json")) ||
                requestUri.startsWith("/api/");
    }

    /**
     * Handle all exceptions
     */
    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, HttpServletRequest request) {
        logger.error("Exception occurred: {} on path: {}", ex.getMessage(), request.getRequestURI(), ex);

        if (isApiRequest(request)) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "An unexpected error occurred: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("exception", ex);
            modelAndView.addObject("url", request.getRequestURL());
            modelAndView.addObject("timestamp", new java.util.Date());
            modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            modelAndView.setViewName("error/general");
            return modelAndView;
        }
    }

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        logger.error("Resource not found: {}", ex.getMessage());

        if (isApiRequest(request)) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        } else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("exception", ex);
            modelAndView.addObject("url", request.getRequestURL());
            modelAndView.addObject("timestamp", new java.util.Date());
            modelAndView.addObject("status", HttpStatus.NOT_FOUND.value());
            modelAndView.setViewName("error/not-found");
            return modelAndView;
        }
    }

    /**
     * Handle validation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleValidationExceptions(ConstraintViolationException ex, HttpServletRequest request) {
        logger.error("Validation error: {}", ex.getMessage());

        if (isApiRequest(request)) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation error: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("exception", ex);
            modelAndView.addObject("url", request.getRequestURL());
            modelAndView.addObject("timestamp", new java.util.Date());
            modelAndView.addObject("status", HttpStatus.BAD_REQUEST.value());
            modelAndView.setViewName("error/validation");
            return modelAndView;
        }
    }

    /**
     * Handle method argument validation exceptions (form validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.error("Validation error in form submission: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        if (isApiRequest(request)) {
            ValidationErrorResponse error = new ValidationErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation error in form submission",
                    request.getRequestURI(),
                    errors
            );
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("errors", errors);
            modelAndView.addObject("url", request.getRequestURL());
            modelAndView.addObject("timestamp", new java.util.Date());
            modelAndView.addObject("status", HttpStatus.BAD_REQUEST.value());
            modelAndView.setViewName("error/form-validation");
            return modelAndView;
        }
    }

    /**
     * Handle database exceptions
     */
    @ExceptionHandler(DataAccessException.class)
    public Object handleDatabaseExceptions(DataAccessException ex, HttpServletRequest request) {
        logger.error("Database error: {}", ex.getMessage(), ex);

        if (isApiRequest(request)) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "A database error occurred: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("exception", ex);
            modelAndView.addObject("url", request.getRequestURL());
            modelAndView.addObject("timestamp", new java.util.Date());
            modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            modelAndView.setViewName("error/database");
            return modelAndView;
        }
    }

    /**
     * Custom error response class for better structured API errors
     */
    public static class ErrorResponse {
        private int status;
        private String message;
        private String path;
        private long timestamp;

        public ErrorResponse(int status, String message, String path) {
            this.status = status;
            this.message = message;
            this.path = path;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters and setters
        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    /**
     * Validation error response with field errors
     */
    public static class ValidationErrorResponse extends ErrorResponse {
        private Map<String, String> errors;

        public ValidationErrorResponse(int status, String message, String path, Map<String, String> errors) {
            super(status, message, path);
            this.errors = errors;
        }

        public Map<String, String> getErrors() {
            return errors;
        }

        public void setErrors(Map<String, String> errors) {
            this.errors = errors;
        }
    }
}