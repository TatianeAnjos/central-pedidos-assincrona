package com.tatiane.centralpedidos.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EventoProducer {
    private String mensagemPedido;
    private String idMensagem;
}
