package com.revhire.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception e, Model model) {
        model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
        return "error"; // Needs error.html
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDateIntegrityViolation(IllegalArgumentException e, Model model) {
        model.addAttribute("error", "Invalid Request: " + e.getMessage());
        return "error";
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e,
            Model model) {
        model.addAttribute("error", "Access Denied: You do not have permission to access this resource.");
        return "error";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public String handleFileUploadException(MaxUploadSizeExceededException e, Model model) {
        model.addAttribute("error", "File size too large! Please upload a file smaller than 5MB.");
        return "error";
    }
}
