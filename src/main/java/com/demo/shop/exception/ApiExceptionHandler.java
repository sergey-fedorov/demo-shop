package com.demo.shop.exception;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Data;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorItem> handle(ConstraintViolationException e) {
        ErrorItem errorItem = new ErrorItem();
        String validationMessages = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));
        errorItem.setErrorMessage(validationMessages);
        return new ResponseEntity<>(errorItem, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorItem> handle(DataIntegrityViolationException e) {
        String message = e.getMessage().contains("Duplicate entry") ? "Entry already exists" : e.getMessage();
        ErrorItem error = new ErrorItem();
        error.setErrorMessage(message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorItem> handle(HttpClientErrorException e) {
        ErrorItem error = new ErrorItem();
        error.setErrorMessage(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorItem> handle(ResourceNotFoundException e) {
        ErrorItem error = new ErrorItem();
        error.setErrorMessage(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorItem> handle(BadRequestException e) {
        ErrorItem error = new ErrorItem();
        error.setErrorMessage(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorItem> handle(ForbiddenException e) {
        ErrorItem error = new ErrorItem();
        error.setErrorMessage(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorItem> handle(MethodArgumentNotValidException e) {
//        String validationMessages = e.getBindingResult().getFieldErrors().stream().map(ve -> ve.getField() + " " + ve.getDefaultMessage()).collect(Collectors.joining(", "));
        String validationMessages = e.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
        ErrorItem error = new ErrorItem();
        error.setErrorMessage(validationMessages);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorItem> handle(MethodArgumentTypeMismatchException e) {
        String validationMessages = "Invalid type of parameter " + e.getName();
        ErrorItem error = new ErrorItem();
        error.setErrorMessage(validationMessages);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentServiceException.class)
    public ResponseEntity<ErrorItem> handle(PaymentServiceException e) {
        ErrorItem error = new ErrorItem();
        error.setErrorMessage(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Data
    public static class ErrorItem {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String errorCode;
        private String errorMessage;

    }

    @Data
    public static class ErrorResponse {
        private List<ErrorItem> errors = new ArrayList<>();
        public void addError(ErrorItem error) {
            this.errors.add(error);
        }
    }
}
