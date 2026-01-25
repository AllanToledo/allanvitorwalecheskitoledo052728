package br.dev.allantoledo.psc.controller.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.dev.allantoledo.psc.util.StreamUtility.mapToList;

@RestControllerAdvice
public class ConstraintViolationHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handler(ConstraintViolationException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Campos inválidos");
        problem.setDetail("Os campos da requisição violam as restrições da aplicação.");
        Map<String, Object> properties = new HashMap<>(1);
        List<String> violations = mapToList(exception.getConstraintViolations(), ConstraintViolation::getMessage);
        properties.put("violations", violations);
        problem.setProperties(properties);
        return problem;
    }
}
