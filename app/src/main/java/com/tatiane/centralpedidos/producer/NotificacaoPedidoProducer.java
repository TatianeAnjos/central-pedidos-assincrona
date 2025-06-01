package com.tatiane.centralpedidos.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tatiane.centralpedidos.dto.EventoProducer;
import com.tatiane.centralpedidos.entities.Pedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificacaoPedidoProducer {

    private final KafkaTemplate<String, EventoProducer> kafkaTemplate;

    private final ObjectMapper objectMapper;

    private static final String TOPICO = "pedido.criado";

    public void notificarClienteProducer(Pedido pedido){
        EventoProducer evento = this.converterMensagemParaJson(pedido);
        kafkaTemplate.send(TOPICO, evento);
        log.info("Evento enviado para o Kafka: {}", evento.getMensagemPedido());
    }

    private EventoProducer converterMensagemParaJson(Pedido pedido) {
        String mensagem = null;
        try {
            mensagem = objectMapper.writeValueAsString(pedido);
            return EventoProducer
                    .builder()
                    .mensagemPedido(mensagem)
                    .idMensagem(UUID.randomUUID().toString())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
