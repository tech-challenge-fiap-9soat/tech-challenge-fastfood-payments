package br.com.fastfood.payments.infra.controller;

import br.com.fastfood.payments.domain.entities.PagamentoEntity;
import br.com.fastfood.payments.gateways.client.PagamentoClient;
import br.com.fastfood.payments.infra.dto.PagamentoDTO;
import br.com.fastfood.payments.infra.enums.FormaPagamento;
import br.com.fastfood.payments.infra.enums.StatusPagamento;
import br.com.fastfood.payments.infra.repository.JpaPagamentoRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class PagamentoControllerIT {

    @MockBean
    private PagamentoClient pagamentoClient;

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

        doNothing().when(pagamentoClient).enviaPagamento(ArgumentMatchers.any());

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
}
