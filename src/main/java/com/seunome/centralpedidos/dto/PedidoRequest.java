package com.seunome.centralpedidos.dto;

import lombok.Data;

import java.util.List;

@Data
public class PedidoRequest {

    private List<ProdutoRequest> listaProdutoRequest;
}
