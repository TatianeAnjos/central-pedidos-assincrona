package com.seunome.centralpedidos.mappers;

import com.seunome.centralpedidos.dto.PedidoRequest;
import com.seunome.centralpedidos.dto.ProdutoRequest;
import com.seunome.centralpedidos.entities.Pedido;
import com.seunome.centralpedidos.entities.Produto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class PedidoMapper {

    public Pedido pedidoRequestToPedido(PedidoRequest pedidoRequest) {
        return Pedido.builder()
                .listaProdutos(this.mapListaPedidos(pedidoRequest.getListaProdutoRequest()))
                .build();
    }

    private List<Produto> mapListaPedidos(List<ProdutoRequest> listaProdutoRequest) {
        return listaProdutoRequest
                .parallelStream()
                .map(produtoRequest ->
                        Produto.builder()
                                .nomeProduto(produtoRequest.getNomeProduto())
                                .quantidadeProduto(produtoRequest.getQuantidadeProduto())
                                .build())
                .collect(Collectors.toList());
    }
}
