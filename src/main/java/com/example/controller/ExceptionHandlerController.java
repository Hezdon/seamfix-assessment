package com.example.controller;

import com.example.dto.BvnResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@ResponseBody
@ControllerAdvice(annotations = RestController.class, basePackages = "com.example")
public class ExceptionHandlerController {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> handleBadRequestException(RuntimeException e) {
        return new ResponseEntity<>(new BvnResponse().withBvn("400").withMessage("Error Occurred"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> handleCustomValidationException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(new BvnResponse().withBvn("400").withMessage("Error Occurred"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<?> handleBadRequestException(Exception e) {
        return new ResponseEntity<>(new BvnResponse().withBvn("400").withMessage("Error Occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}