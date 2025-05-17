package br.com.fastfood.payments.bdd;

import br.com.fastfood.payments.domain.entities.PagamentoEntity;
import br.com.fastfood.payments.gateways.client.PagamentoClient;
import br.com.fastfood.payments.infra.dto.PagamentoDTO;
import br.com.fastfood.payments.infra.enums.FormaPagamento;
import br.com.fastfood.payments.infra.enums.StatusPagamento;
import br.com.fastfood.payments.infra.repository.JpaPagamentoRepository;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PagamentoSteps {

    @LocalServerPort
    private int port;

    private final JpaPagamentoRepository pagamentoRepository;
    private final PagamentoClient pagamentoClient;

    private Response response;
    private final Long pedidoId = 1L;

    public PagamentoSteps(JpaPagamentoRepository pagamentoRepository, PagamentoClient pagamentoClient) {
        this.pagamentoRepository = pagamentoRepository;
        this.pagamentoClient = pagamentoClient;
    }

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/fastfood";
    }

    @Quando("eu envio um pagamento com valor {double} e forma de pagamento {string} e ID {long}")
    public void euEnvioUmPagamentoComValorEFormaDePagamento(Double valor, String formaPagamento, Long id) {
        PagamentoDTO dto = new PagamentoDTO();
        dto.setIdPedido(id);
        dto.setValor(valor);
        dto.setFormaPagamento(FormaPagamento.valueOf(formaPagamento));

        doNothing().when(pagamentoClient).enviaPagamento(ArgumentMatchers.any());

        response = RestAssured.given()
                .contentType("application/json")
                .body(dto)
                .when()
                .post("/pagamento/pagar");
    }

    @Entao("o pagamento deve ser salvo com status {string}")
    public void oPagamentoDeveSerSalvoComStatus(String status) {
        PagamentoEntity saved = pagamentoRepository.findByPedidoId(pedidoId);
        assertThat(saved).isNotNull();
        assertThat(saved.getStatus().name()).isEqualTo(status);
    }

    @E("o status HTTP da resposta deve ser {int}")
    public void oStatusHTTPDaRespostaDeveSer(Integer statusCode) {
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }

    @Dado("que existe um pagamento com status {string} para o pedido com ID {long}")
    public void queExisteUmPagamentoComStatusParaOPedido(String status, Long id) {
        pagamentoRepository.deleteAll();

        PagamentoEntity pagamento = new PagamentoEntity();
        pagamento.setPedidoId(id);
        pagamento.setValor(150.0);
        pagamento.setFormaPagamento(FormaPagamento.CREDITO);
        pagamento.setStatus(StatusPagamento.valueOf(status));
        pagamentoRepository.save(pagamento);
    }

    @Quando("eu consultar o status do pagamento para o pedido com ID {long}")
    public void euConsultarOStatusDoPagamentoParaOPedido(Long id) {
        response = RestAssured
                .when()
                .get("/pagamento/" + id);
    }

    @Entao("a resposta deve conter {string}")
    public void aRespostaDeveConter(String conteudoEsperado) {
        assertThat(response.getBody().asString()).contains(conteudoEsperado);
    }

    @Dado("que não existe pagamento para o pedido com ID {long}")
    public void queNaoExistePagamentoParaOPedido(Long id) {
        pagamentoRepository.deleteAll();
    }
}
