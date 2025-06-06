package br.com.fastfood.payments.infra.controller;

import br.com.fastfood.payments.domain.entities.PagamentoEntity;
import br.com.fastfood.payments.gateways.client.CachePedidosClient;
import br.com.fastfood.payments.gateways.client.PagamentoClient;
import br.com.fastfood.payments.gateways.client.PedidoClient;
import br.com.fastfood.payments.infra.dto.PagamentoDTO;
import br.com.fastfood.payments.infra.dto.PedidoDTO;
import br.com.fastfood.payments.infra.enums.FormaPagamento;
import br.com.fastfood.payments.infra.enums.StatusPagamento;
import br.com.fastfood.payments.infra.repository.JpaPagamentoRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class PagamentoControllerIT {

    @MockBean
    private PagamentoClient pagamentoClient;

    @MockBean
    private PedidoClient pedidoClient;

    @MockBean
    private CachePedidosClient cachePedidosClient;

    @LocalServerPort
    private int port;

    @Autowired
    private JpaPagamentoRepository pagamentoRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/fastfood";
    }

    @Test
    void deveCriarPagamentoComSucessoERetornarStatus200() {
        PagamentoDTO dto = new PagamentoDTO();
        dto.setIdPedido(2L);
        dto.setValor(100.0);
        dto.setFormaPagamento(FormaPagamento.PIX);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(2L);
        pedidoDTO.setCpf("483.621.128.23");
        pedidoDTO.setValorTotal(100.0);
        pedidoDTO.setStatusPedido("RECEBIDO");

        doNothing().when(pagamentoClient).enviaPagamento(ArgumentMatchers.any());
        doNothing().when(pedidoClient).updateStatusPedido(ArgumentMatchers.any());
        when(cachePedidosClient.getPedido(any())).thenReturn(pedidoDTO);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/pagamento/pagar")
                .then()
                .statusCode(200);

        // Verifica se foi salvo corretamente
        PagamentoEntity saved = pagamentoRepository.findByPedidoId(dto.getIdPedido());
        assert saved != null;
        assert saved.getStatus() == StatusPagamento.PENDENTE;
    }

    @Test
    void deveRetornarStatusPagamentoQuandoExistente() {
        PagamentoEntity pagamento = new PagamentoEntity();
        pagamento.setPedidoId(1L);
        pagamento.setValor(150.0);
        pagamento.setFormaPagamento(FormaPagamento.CREDITO);
        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamentoRepository.save(pagamento);

        RestAssured
                .when()
                .get("/pagamento/" + pagamento.getPedidoId())
                .then()
                .statusCode(200)
                .body(containsString("APROVADO"));
    }

    @Test
    void deveRetornarErro400QuandoPagamentoNaoExiste() {
        Long idInexistente = 999L;

        RestAssured
                .when()
                .get("/pagamento/" + idInexistente)
                .then()
                .statusCode(400)
                .body(containsString("Não existe pagamento para este pedido"));
    }

    @Test
    void deveRetornarErro400QuandoValorDoPagamentoForIncorreto() {
        PagamentoDTO dto = new PagamentoDTO();
        dto.setIdPedido(3L);
        dto.setValor(50.0); // valor diferente
        dto.setFormaPagamento(FormaPagamento.PIX);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(3L);
        pedidoDTO.setCpf("483.621.128.23");
        pedidoDTO.setValorTotal(100.0); // valor esperado
        pedidoDTO.setStatusPedido("RECEBIDO");

        when(cachePedidosClient.getPedido(any())).thenReturn(pedidoDTO);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/pagamento/pagar")
                .then()
                .statusCode(400)
                .body(containsString("Valor do pagamento não é igual ao valor do pedido"));
    }

    @Test
    void deveRetornarErro400QuandoPagamentoJaFoiRealizado() {
        PagamentoDTO dto = new PagamentoDTO();
        dto.setIdPedido(10L);
        dto.setValor(120.0);
        dto.setFormaPagamento(FormaPagamento.CREDITO);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(10L);
        pedidoDTO.setCpf("222.333.444-55");
        pedidoDTO.setValorTotal(120.0);
        pedidoDTO.setStatusPedido("RECEBIDO");

        PagamentoEntity pagamentoExistente = new PagamentoEntity();
        pagamentoExistente.setPedidoId(10L);
        pagamentoExistente.setValor(120.0);
        pagamentoExistente.setStatus(StatusPagamento.APROVADO);
        pagamentoExistente.setFormaPagamento(FormaPagamento.CREDITO);

        // Simula o pedido vindo do cache e um pagamento já salvo no banco
        when(cachePedidosClient.getPedido(dto.getIdPedido())).thenReturn(pedidoDTO);
        pagamentoRepository.save(pagamentoExistente);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/pagamento/pagar")
                .then()
                .statusCode(400)
                .body(containsString("Pagamento para este pedido já foi realizado"));
    }
}
