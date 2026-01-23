package br.dev.allantoledo.psc.controller.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ResponseStatusHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handler(ResponseStatusException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(exception.getStatusCode());
        problem.setTitle(exception.getMessage());
        problem.setDetail(exception.getReason());
        return problem;
    }
}
