package com.seunome.centralpedidos.repository;

import com.seunome.centralpedidos.entities.Pedido;
import org.springframework.stereotype.Repository;

@Repository
public class PedidoRepository {
    public Pedido gravarPedido(Pedido pedido){
        //Implementar
        return Pedido.builder().build();
    }
}
