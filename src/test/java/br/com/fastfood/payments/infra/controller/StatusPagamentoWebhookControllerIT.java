package br.com.fastfood.payments.infra.controller;

import br.com.fastfood.payments.domain.entities.PagamentoEntity;
import br.com.fastfood.payments.gateways.repository.PagamentoGateway;
import br.com.fastfood.payments.infra.enums.FormaPagamento;
import br.com.fastfood.payments.infra.enums.StatusPagamento;
import br.com.fastfood.payments.usecases.PagamentoService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StatusPagamentoWebhookControllerIT {

    @LocalServerPort
    private int port;

    @MockBean
    private PagamentoService pagamentoService;

    @MockBean
    private PagamentoGateway pagamentoGateway;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/fastfood";
    }

    @Test
    void deveReceberWebhookEChamarServicoComSucesso() {
        PagamentoEntity pagamento = buildPagamento(StatusPagamento.PENDENTE); // simula vindo com status errado

        doNothing().when(pagamentoService).processaRetornoPagamento(any());

        given()
                .contentType(ContentType.JSON)
                .body(pagamento)
                .when()
                .post("/webhook/status-pagamento")
                .then()
                .statusCode(200);

        verify(pagamentoService, times(1)).processaRetornoPagamento(Mockito.any());
    }

    @Test
    void deveRetornar400QuandoPayloadForInvalido() {
        given()
                .contentType(ContentType.JSON)
                .body("json_invalido") // corpo inválido
                .when()
                .post("/webhook/status-pagamento")
                .then()
                .statusCode(400); // Bad Request esperado
    }

    private PagamentoEntity buildPagamento(StatusPagamento status) {
        PagamentoEntity pagamento = new PagamentoEntity();
        pagamento.setPedidoId(123L);
        pagamento.setValor(99.9);
        pagamento.setFormaPagamento(FormaPagamento.PIX);
        pagamento.setStatus(status);
        return pagamento;
    }
}
