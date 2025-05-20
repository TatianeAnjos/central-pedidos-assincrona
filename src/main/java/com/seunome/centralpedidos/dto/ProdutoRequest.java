package com.seunome.centralpedidos.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ProdutoRequest {
    private String nomeProduto;
    private Integer quantidadeProduto;
}
