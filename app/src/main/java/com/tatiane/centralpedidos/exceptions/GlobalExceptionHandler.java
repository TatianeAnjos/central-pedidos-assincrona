package com.tatiane.centralpedidos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PedidoInvalidoException.class)
    public ResponseEntity<String> handlePedidoInvalido(PedidoInvalidoException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ErroNoProcessamentoDoPedidoException.class)
    public ResponseEntity<String> handleErroNoProcessamentoDoPedidoException(ErroNoProcessamentoDoPedidoException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
