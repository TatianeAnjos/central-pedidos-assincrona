package com.tatiane.centralpedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {

    @NotNull
    private String nomeCliente;

    @Valid
    private List<ProdutoRequest> listaProdutoRequest;
}
