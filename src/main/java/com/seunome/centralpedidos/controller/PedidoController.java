package com.seunome.centralpedidos.controller;

import com.seunome.centralpedidos.dto.PedidoRequest;
import com.seunome.centralpedidos.exceptions.ErroNoProcessamentoDoPedidoException;
import com.seunome.centralpedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/central-pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @RequestMapping("/pedidos")
    @PostMapping
    public ResponseEntity<String> criarPedido(@RequestBody @Valid List<PedidoRequest> listaPedidosRequest) throws ErroNoProcessamentoDoPedidoException {
        pedidoService.processarPedidoAsync(listaPedidosRequest);
        return ResponseEntity.ok().body("Pedido recebido e será processado.");
    }

    @GetMapping("/pedidos")
    public ResponseEntity<String> listarPedidos() {
        return ResponseEntity.ok ("Hello world!");
    }
}
