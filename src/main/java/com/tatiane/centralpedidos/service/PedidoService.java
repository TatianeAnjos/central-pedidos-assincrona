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
import com.tatiane.centralpedidos.repository.PedidoRepository;
import com.tatiane.centralpedidos.repository.ProdutoRepository;
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


    public void processarPedidoAsync(List<PedidoRequest> listaPedidosRequest) throws ErroNoProcessamentoDoPedidoException {
        log.info("Iniciando processamento de pedidos assíncrono");
        try {
            List<CompletableFuture<Void>> futures = listaPedidosRequest
                    .parallelStream()
                    .map(pedidoRequest -> CompletableFuture.runAsync(() -> {
                        try {
                            Pedido pedido = this.processarPedido(pedidoRequest);
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

    private Pedido processarPedido(PedidoRequest pedidoRequest) throws PedidoInvalidoException {
        log.info("PedidoService: Processamento de pedido em andamento");

        Pedido pedido = pedidoRepository.save(pedidoMapper.pedidoRequestToPedido(pedidoRequest));

        log.info("PedidoService: Processando itens do pedido");
        this.salvarProdutos(pedidoRequest.getListaProdutoRequest(), pedido);

        log.info("Pedido id {} foi gravado com sucesso", pedido.getIdPedido());
        return pedido;
    }

    private void salvarProdutos(List<ProdutoRequest> listaProdutos, Pedido pedido) throws PedidoInvalidoException {

        if(isNull(listaProdutos) || isEmpty(listaProdutos)){
            throw new PedidoInvalidoException("A lista de produtos não pode ser vazia.");
        }
        List<Object> listaProduto = listaProdutos
                .stream()
                .map(produto -> produtoRepository.save(produtoMapper.produtoRequestToProduto(produto, pedido))
                ).collect(Collectors.toUnmodifiableList());
    }

    public List<PedidoResponse> listarPedidos() {

        List<PedidoResponse> listaPedidoResponse = pedidoMapper.pedidoToPedidoResponse(pedidoRepository.findAll());

        listaPedidoResponse
                .forEach(pedido -> {
                    List<Produto> a = produtoRepository.findByIdPedido(pedido.getIdPedido());
                    pedido.setListaProdutoResponse(a
                            .stream()
                            .map(produtoMapper::produtoToProdutoResponse).collect(Collectors.toList()));
                });
        return listaPedidoResponse;
    }
}
