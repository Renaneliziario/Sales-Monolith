package br.com.renan.sales.shared.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex) {
        return construirResposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return construirResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex) {
        return construirResposta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Throwable causa = ex.getMostSpecificCause();
        boolean chaveDuplicada = causa instanceof SQLException erroSql
                && "23505".equals(erroSql.getSQLState());

        HttpStatus status = chaveDuplicada
                ? HttpStatus.CONFLICT
                : HttpStatus.INTERNAL_SERVER_ERROR;
        String mensagem = chaveDuplicada
                ? "Conflito com dados existentes"
                : "Erro ao salvar os dados";

        return construirResposta(status, mensagem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleExcecaoNaoTratada(Exception ex) {
        logger.error("Erro nao tratado", ex);
        return construirResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errosPorCampo = new LinkedHashMap<>();

        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            errosPorCampo.put(erro.getField(), erro.getDefaultMessage());
        }

        ApiError resposta = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação nos campos",
                LocalDateTime.now(),
                errosPorCampo
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    private ResponseEntity<ApiError> construirResposta(HttpStatus status, String mensagem) {
        ApiError erro = new ApiError(
                status.value(),
                mensagem,
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(status).body(erro);
    }
}
