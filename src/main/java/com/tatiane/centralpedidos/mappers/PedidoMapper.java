package com.tatiane.centralpedidos.mappers;

import com.tatiane.centralpedidos.dto.PedidoRequest;
import com.tatiane.centralpedidos.dto.PedidoResponse;
import com.tatiane.centralpedidos.dto.ProdutoRequest;
import com.tatiane.centralpedidos.entities.Pedido;
import com.tatiane.centralpedidos.entities.Produto;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@Component
public class PedidoMapper {

    public Pedido pedidoRequestToPedido(PedidoRequest pedidoRequest) {
        return Pedido.builder()
                .nomeCliente(pedidoRequest.getNomeCliente())
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

    public List<PedidoResponse> pedidoToPedidoResponse(List<Pedido> listaPedidos) {
        return listaPedidos
                .stream()
                .map(pedido -> PedidoResponse
                        .builder()
                        .nomeCliente(pedido.getNomeCliente())
                        .idPedido(pedido.getIdPedido())
                        .build()
                ).collect(Collectors.toList());
    }
}
