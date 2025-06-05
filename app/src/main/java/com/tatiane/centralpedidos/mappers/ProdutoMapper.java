package com.tatiane.centralpedidos.mappers;

import com.tatiane.centralpedidos.dto.ProdutoRequest;
import com.tatiane.centralpedidos.dto.ProdutoResponse;
import com.tatiane.centralpedidos.entities.Pedido;
import com.tatiane.centralpedidos.entities.Produto;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

@Builder
@Data
@Component
public class ProdutoMapper {

    public Produto produtoRequestToProduto(ProdutoRequest produtoRequest) {
        return Produto.builder()
                .nomeProduto(produtoRequest.getNomeProduto())
                .quantidadeProduto(produtoRequest.getQuantidadeProduto())
                .idProduto(produtoRequest.getIdProduto())
                .build();
    }

    public ProdutoResponse produtoToProdutoResponse(Produto produto) {
        return ProdutoResponse
                .builder()
                .nomeProduto(produto.getNomeProduto())
                .quantidadeProduto(produto.getQuantidadeProduto())
                .build();
    }
}
