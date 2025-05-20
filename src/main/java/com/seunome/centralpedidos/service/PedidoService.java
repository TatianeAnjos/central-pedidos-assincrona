package com.seunome.centralpedidos.service;

import com.seunome.centralpedidos.dto.PedidoRequest;
import com.seunome.centralpedidos.entities.Pedido;
import com.seunome.centralpedidos.exceptions.ErroNoProcessamentoDoPedidoException;
import com.seunome.centralpedidos.mappers.PedidoMapper;
import com.seunome.centralpedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper mapper;

    public void processarPedidoAsync(List<PedidoRequest> listaPedidosRequest) throws ErroNoProcessamentoDoPedidoException {
        log.info("Iniciando processamento de pedidos assíncrono");
        try{
            List<CompletableFuture<Void>> futures = listaPedidosRequest
                    .parallelStream()
                    .map(pedidoRequest -> CompletableFuture.runAsync(() -> {
                        try {
                            Pedido pedido = pedidoRepository.gravarPedido(mapper.pedidoRequestToPedido(pedidoRequest));
                            log.info("Pedido id {} foi gravado com sucesso", pedido.getIdPedido());
                            //  return pedido;
                        } catch (Exception e) {
                            log.error("Erro ao processar o pedido", e);
                            throw new RuntimeException("Ocorreu um erro ao processar o pedido.", e);
                        }
                    })).collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info("Pedidos gravados com sucesso");
        }catch(Exception e){
            log.error("Ocorreu um erro ao processar o pedido.", e);
            throw new ErroNoProcessamentoDoPedidoException("Ocorreu um erro ao processar o pedido.");
        }
    }
}
