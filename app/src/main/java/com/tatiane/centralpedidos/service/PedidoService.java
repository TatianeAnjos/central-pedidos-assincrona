package com.tatiane.centralpedidos.service;

import com.tatiane.centralpedidos.dto.PedidoRequest;
import com.tatiane.centralpedidos.dto.PedidoResponse;
import com.tatiane.centralpedidos.dto.ProdutoRequest;
import com.tatiane.centralpedidos.entities.Pedido;
import com.tatiane.centralpedidos.entities.Produto;
import com.tatiane.centralpedidos.exceptions.ErroNoProcessamentoDoPedidoException;
import com.tatiane.centralpedidos.exceptions.PedidoInvalidoException;
import com.tatiane.centralpedidos.mappers.PedidoMapper;
import com.tatiane.centralpedidos.mappers.ProdutoMapper;
import com.tatiane.centralpedidos.producer.NotificacaoPedidoProducer;
import com.tatiane.centralpedidos.repository.PedidoRepository;
import com.tatiane.centralpedidos.repository.ProdutoRepository;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoMapper pedidoMapper;
    private final ProdutoMapper produtoMapper;
    private final NotificacaoPedidoProducer notificacaoPedidoProducer;
    private final MeterRegistry meterRegistry;


    public void processarPedidoAsync(List<PedidoRequest> listaPedidosRequest) throws ErroNoProcessamentoDoPedidoException, RuntimeException {
        log.info("Iniciando processamento de pedidos assíncrono");
        try {
            List<CompletableFuture<Void>> futures = listaPedidosRequest
                    .parallelStream()
                    .map(pedidoRequest -> CompletableFuture.runAsync(() -> {
                        try {
                            Pedido pedido = this.processarPedido(pedidoRequest);

                            log.info("Iniciando processamento de envio de confirmacao do pedido {}", pedido.getIdPedido());
                            this.enviarConfirmacaoPedido(pedido);
                        } catch (Exception | PedidoInvalidoException e) {
                            log.error("Erro ao processar o pedido", e);
                            throw new RuntimeException("Ocorreu um erro ao processar o pedido.", e);
                        }
                    })).toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info("Pedidos gravados com sucesso");
        } catch (Exception e) {
            log.error("Ocorreu um erro ao processar o pedido.", e);
            throw new ErroNoProcessamentoDoPedidoException("Ocorreu um erro ao processar o pedido.");
        }
    }

    private void enviarConfirmacaoPedido(Pedido pedido) {
       notificacaoPedidoProducer.notificarClienteProducer(pedido);
    }

    @Timed(value = "pedido.criar.tempo", description = "Tempo para criar pedido")
    @Counted(value = "pedido.criar.total", description = "Total de pedidos criados")
    private Pedido processarPedido(PedidoRequest pedidoRequest) throws PedidoInvalidoException {

        if(isNull(pedidoRequest.getListaProdutoRequest()) || isEmpty(pedidoRequest.getListaProdutoRequest())){
            throw new PedidoInvalidoException("A lista de produtos não pode ser vazia.");
        }

        log.info("PedidoService: Processamento de pedido em andamento");

        Pedido pedido = pedidoRepository.save(pedidoMapper.pedidoRequestToPedido(pedidoRequest));

        log.info("PedidoService: Processando itens do pedido");
        this.salvarProdutos(pedidoRequest.getListaProdutoRequest(), pedido);

        log.info("Pedido id {} foi gravado com sucesso", pedido.getIdPedido());
        return pedido;
    }

    private void salvarProdutos(List<ProdutoRequest> listaProdutosRequest, Pedido pedido) throws PedidoInvalidoException {

        if(isNull(listaProdutosRequest) || isEmpty(listaProdutosRequest)){
            throw new PedidoInvalidoException("A lista de produtos não pode ser vazia.");
        }
        List<Produto> listaProduto = listaProdutosRequest
                .stream()
                .map(produtoRequest -> {
                    meterRegistry.counter("pedido.por.tipo.produto", "tipo", produtoRequest.getIdProduto().toString()).increment();
                    Produto produto = produtoMapper.produtoRequestToProduto(produtoRequest);
                    Produto ProdutoSalvo = produtoRepository.save(produto);
                    return produto;
                }).collect(Collectors.toList());

        pedido.setListaProdutos(listaProduto);
    }

    public List<PedidoResponse> listarPedidos() {

        List<PedidoResponse> listaPedidoResponse = pedidoMapper.pedidoToPedidoResponse(pedidoRepository.findAll());

        listaPedidoResponse
                .forEach(pedido -> {
                    List<Produto> listaProdutos = produtoRepository.findByIdPedido(pedido.getIdPedido());
                    pedido.setListaProdutoResponse(listaProdutos
                            .stream()
                            .map(produtoMapper::produtoToProdutoResponse).collect(Collectors.toList()));
                });
        return listaPedidoResponse;
    }
}
