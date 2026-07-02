package com.project.shedrive.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String , String>> handleValidationErrors(
            MethodArgumentNotValidException exception
    ){
        var errors = new HashMap<String ,String>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField() , error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
    //UsernameAlreadyRegisteredException
    @ExceptionHandler(UsernameAlreadyRegisteredException.class)
    public ResponseEntity<Map<String , String>> handleUsernameAlreadyRegisteredException(
            UsernameAlreadyRegisteredException ex
    ){
        var errors = new HashMap<String ,String>();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error" , ex.getMessage() ));
    }
    //NotAuthException
    @ExceptionHandler(NotAuthException.class)
    public ResponseEntity<Map<String , String>> handleNotAuthException(
            NotAuthException ex
    ){
        var errors = new HashMap<String ,String>();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error" , ex.getMessage() ));
    }

    //FalseInputData
    @ExceptionHandler(FalseInputData.class)
    public ResponseEntity<Map<String , String>> handleFalseInputData(
            FalseInputData ex
    ){
        var errors = new HashMap<String ,String>();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error" , ex.getMessage() ));
    }
}
