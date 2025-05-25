package com.tatiane.centralpedidos.controller;

import com.tatiane.centralpedidos.dto.PedidoRequest;
import com.tatiane.centralpedidos.dto.PedidoResponse;
import com.tatiane.centralpedidos.exceptions.ErroNoProcessamentoDoPedidoException;
import com.tatiane.centralpedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/central-pedidos")
@Slf4j
public class PedidoController {

    private final PedidoService pedidoService;

    @RequestMapping("/pedidos")
    @PostMapping
    public ResponseEntity<String> criarPedido(@RequestBody List<PedidoRequest> listaPedidosRequest) throws ErroNoProcessamentoDoPedidoException {
        log.info("Inicio controller criar pedido");
        pedidoService.processarPedidoAsync(listaPedidosRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/pedidos")
    public ResponseEntity<List<PedidoResponse>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }
}
