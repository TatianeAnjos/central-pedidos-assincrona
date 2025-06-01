package com.tatiane.centralpedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {

    private String nomeCliente;

    private Long idPedido;

    private List<ProdutoResponse> listaProdutoResponse;
}
