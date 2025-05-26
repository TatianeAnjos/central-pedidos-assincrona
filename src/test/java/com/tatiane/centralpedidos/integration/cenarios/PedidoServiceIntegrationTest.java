package com.tatiane.centralpedidos.integration.cenarios;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tatiane.centralpedidos.dto.PedidoRequest;
import com.tatiane.centralpedidos.dto.ProdutoRequest;
import com.tatiane.centralpedidos.entities.Pedido;
import com.tatiane.centralpedidos.exceptions.ErroNoProcessamentoDoPedidoException;
import com.tatiane.centralpedidos.repository.PedidoRepository;
import com.tatiane.centralpedidos.service.PedidoService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class PedidoServiceIntegrationTest {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void deveProcessarEGravarOPedidoNoBanco() throws ErroNoProcessamentoDoPedidoException {
        // Arrange
        List<PedidoRequest> listaPedidosRequest = Collections.singletonList(criaListaPedidosRequestMock().get(0));

        pedidoService.processarPedidoAsync(listaPedidosRequest);

        //Act
        List<Pedido> listaPedidosResponse = pedidoRepository.findAll();

        // Assert
        assertNotNull(listaPedidosResponse);
        assertEquals("Televisao", listaPedidosResponse.get(0).getListaProdutos().get(0).getNomeProduto());
    }

    @Test
    public void deveCriarVariosPedidosComProdutosAPartirDoController() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(criaListaPedidosRequestMock());

        mockMvc.perform(post("/central-pedidos/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void deveBuscarPedidosNoBancoAPartirDoController() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(criaListaPedidosRequestMock());
        mockMvc.perform(post("/central-pedidos/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        mockMvc.perform(get("/central-pedidos/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomeCliente").value("Cliente 2"))
                .andExpect(jsonPath("$[0].listaProdutoResponse").isArray())
                .andExpect(jsonPath("$[0].listaProdutoResponse[0].nomeProduto").value("Televisao"));
    }

    @Test
    @Disabled
    public void deveRetornarErroSePedidoForInvalido() throws Exception {
        PedidoRequest pedidoInvalido = PedidoRequest.builder()
                .nomeCliente("Cliente Sem Produto")
                .listaProdutoRequest(Collections.emptyList())
                .build();

        String jsonRequest = objectMapper.writeValueAsString(List.of(pedidoInvalido));

        mockMvc.perform(post("/central-pedidos/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest()); // ou o que seu controller estiver lançando
    }


    private List<PedidoRequest> criaListaPedidosRequestMock() {
        return Arrays.asList(PedidoRequest.builder()
                        .nomeCliente("Cliente 1")
                        .listaProdutoRequest(Collections.singletonList(criaListaProdutosRequest().get(0)))
                        .build(),
                PedidoRequest.builder()
                        .nomeCliente("Cliente 2")
                        .listaProdutoRequest(criaListaProdutosRequest())
                        .build()
        );
    }

    private List<ProdutoRequest> criaListaProdutosRequest() {
        return Arrays.asList(ProdutoRequest.builder()
                        .nomeProduto("Televisao")
                        .quantidadeProduto(1)
                        .build(),
                ProdutoRequest.builder()
                        .nomeProduto("Sofa")
                        .quantidadeProduto(1)
                        .build()

        );
    }
}
