package com.tatiane.centralpedidos;

import com.tatiane.centralpedidos.dto.PedidoRequest;
import com.tatiane.centralpedidos.dto.PedidoResponse;
import com.tatiane.centralpedidos.dto.ProdutoRequest;
import com.tatiane.centralpedidos.dto.ProdutoResponse;
import com.tatiane.centralpedidos.entities.Pedido;
import com.tatiane.centralpedidos.entities.Produto;
import com.tatiane.centralpedidos.exceptions.ErroNoProcessamentoDoPedidoException;
import com.tatiane.centralpedidos.mappers.PedidoMapper;
import com.tatiane.centralpedidos.mappers.ProdutoMapper;
import com.tatiane.centralpedidos.producer.NotificacaoPedidoProducer;
import com.tatiane.centralpedidos.repository.PedidoRepository;
import com.tatiane.centralpedidos.repository.ProdutoRepository;
import com.tatiane.centralpedidos.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    private PedidoRepository pedidoRepository;

    private ProdutoRepository produtoRepository;

    private PedidoMapper pedidoMapper;

    private ProdutoMapper produtoMapper;

    private NotificacaoPedidoProducer notificacaoPedidoProducer;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        pedidoRepository = Mockito.mock(PedidoRepository.class);
        produtoRepository = Mockito.mock(ProdutoRepository.class);
        pedidoMapper = Mockito.mock(PedidoMapper.class);
        produtoMapper = Mockito.mock(ProdutoMapper.class);
        notificacaoPedidoProducer = Mockito.mock(NotificacaoPedidoProducer.class);
        pedidoService = new PedidoService(pedidoRepository, produtoRepository, pedidoMapper, produtoMapper, notificacaoPedidoProducer);
    }


    @Test
    @DisplayName("Processa um pedido e verifica se o foi salvo com sucesso")
    public void processaProdutoComSucesso() throws ErroNoProcessamentoDoPedidoException {
        Pedido pedidoCriado = mockPedido();
        List<PedidoRequest> listaPedidosRequest = criaListaPedidosRequestMock();
        Pedido pedidoMapeado = mockPedido();

        when(pedidoRepository.save(any())).thenReturn(pedidoCriado);
        when(pedidoMapper.pedidoRequestToPedido(Mockito.any())).thenReturn(pedidoMapeado);

        when(produtoRepository.save(any())).thenReturn(pedidoCriado.getListaProdutos().get(0));
        when(produtoMapper.produtoRequestToProduto(Mockito.any(), Mockito.any())).thenReturn(pedidoMapeado.getListaProdutos().get(0));


        pedidoService.processarPedidoAsync(listaPedidosRequest);

        verify(pedidoRepository, times(1)).save(any(Pedido.class));
        verify(pedidoMapper, times(1)).pedidoRequestToPedido(any(PedidoRequest.class));
        assertEquals(12656898, pedidoCriado.getIdPedido());
    }

    @Test
    @DisplayName("Processa um pedido com erro")
    public void deveLancarExcecaoAoProcessarPedido() throws ErroNoProcessamentoDoPedidoException {
        List<PedidoRequest> listaPedidosRequest = criaListaPedidosRequestMock();
        Pedido pedidoMapeado = mockPedido();

        when(pedidoMapper.pedidoRequestToPedido(Mockito.any())).thenThrow(new RuntimeException("Erro no mapeamento"));

        assertThrows(ErroNoProcessamentoDoPedidoException.class, () -> {
            pedidoService.processarPedidoAsync(listaPedidosRequest);
        });
    }

    @Test
    @DisplayName("Recupera um pedido com sucesso")
    public void recuperaUmPedidoComSucesso() throws ErroNoProcessamentoDoPedidoException {
        Pedido pedidoCriado = mockPedido();
        List<PedidoRequest> listaPedidosRequest = criaListaPedidosRequestMock();
        List<PedidoResponse> pedidoResponse = mockPedidoResponse();

        when(pedidoRepository.findAll()).thenReturn(Collections.singletonList(pedidoCriado));
        when(pedidoMapper.pedidoToPedidoResponse(Mockito.any())).thenReturn(pedidoResponse);

        when(produtoRepository.findByIdPedido(any())).thenReturn(Collections.singletonList(pedidoCriado.getListaProdutos().get(0)));
        when(produtoMapper.produtoToProdutoResponse(Mockito.any())).thenReturn(pedidoResponse.get(0).getListaProdutoResponse().get(0));

        pedidoService.listarPedidos();

        verify(pedidoRepository, times(1)).findAll();
        verify(produtoRepository, times(1)).findByIdPedido(Mockito.any());
        verify(pedidoMapper, times(1)).pedidoToPedidoResponse(any());
        verify(produtoMapper, times(1)).produtoToProdutoResponse(any());
        assertEquals(12656898, pedidoCriado.getIdPedido());
        assertEquals("Televisão", pedidoCriado.getListaProdutos().get(0).getNomeProduto());
    }

    private List<PedidoResponse> mockPedidoResponse() {
        return Arrays.asList(PedidoResponse.builder()
                .idPedido(12656898L)
                .nomeCliente("Cliente Teste")
                .listaProdutoResponse(Arrays.asList(
                        ProdutoResponse
                                .builder()
                                .nomeProduto("Televisão")
                                .quantidadeProduto(1)
                                .build()))
                .build());
    }

    private Pedido mockPedido() {
        return Pedido.builder()
                .idPedido(12656898L)
                .nomeCliente("Cliente Teste")
                .listaProdutos(Arrays.asList(
                        Produto
                        .builder()
                        .nomeProduto("Televisão")
                        .quantidadeProduto(1)
                        .build()))
                .build();
    }

    private List<PedidoRequest> criaListaPedidosRequestMock() {
        return Arrays.asList(PedidoRequest.builder()
                .nomeCliente("Cliente Teste")
                .listaProdutoRequest(criaListaProdutosRequest())
                .build());
    }

    private List<ProdutoRequest> criaListaProdutosRequest() {
        return Arrays.asList(ProdutoRequest.builder()
                .nomeProduto("Televisão")
                .quantidadeProduto(1)
                .build());
    }

}
