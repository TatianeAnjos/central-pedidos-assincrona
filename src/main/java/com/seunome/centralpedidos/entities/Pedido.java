package com.seunome.centralpedidos.entities;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Entity
@Builder
@Data
public class Pedido {
    private Integer idPedido;
    private List<Produto> listaProdutos;
}
