package br.dev.allantoledo.psc.controller.exception;

import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ObjectNotFoundExceptionHandler {
    @ExceptionHandler(ObjectNotFoundException.class)
    public ProblemDetail handle(ObjectNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Objeto não encontrado no banco de dados.");
        problemDetail.setDetail(
                String.format(
                        "Não encontrada entidade '%s' com identificador '%s'.",
                        exception.getEntityName(),
                        exception.getIdentifier()
                )
        );

        return problemDetail;
    }
}
