package com.seunome.centralpedidos.entities;

import lombok.Builder;

@Entity
@Builder
public class Produto {
    private Integer idProduto;
    private String nomeProduto;
    private Integer quantidadeProduto;
}
