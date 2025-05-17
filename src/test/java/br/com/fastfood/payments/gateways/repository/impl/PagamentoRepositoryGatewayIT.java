package br.com.fastfood.payments.gateways.repository.impl;

import br.com.fastfood.payments.domain.entities.PagamentoEntity;
import br.com.fastfood.payments.infra.enums.FormaPagamento;
import br.com.fastfood.payments.infra.enums.StatusPagamento;
import br.com.fastfood.payments.infra.repository.JpaPagamentoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(PagamentoRepositoryGateway.class) // importa a implementação a ser testada
class PagamentoRepositoryGatewayIT {

    @Autowired
    private PagamentoRepositoryGateway pagamentoRepositoryGateway;

    @Autowired
    private JpaPagamentoRepository jpaPagamentoRepository; // necessário para garantir que o Spring crie o contexto corretamente

    @Test
    void devePermitirCriarTabela(){
        var total = jpaPagamentoRepository.count();
        assertThat(total).isNotNegative();
    }

    @Test
    void deveSalvarEPersistirPagamento() {
        PagamentoEntity pagamento = new PagamentoEntity();
        pagamento.setPedidoId(1L);
        pagamento.setValor(100.0);
        pagamento.setFormaPagamento(FormaPagamento.DEBITO);
        pagamento.setStatus(StatusPagamento.PENDENTE);

        PagamentoEntity saved = pagamentoRepositoryGateway.save(pagamento);

        assertNotNull(saved.getId());
        assertEquals(1L, saved.getPedidoId());
        assertEquals(StatusPagamento.PENDENTE, saved.getStatus());
    }

    @Test
    void deveRetornarPagamentoPorPedidoId() {
        PagamentoEntity pagamento = new PagamentoEntity();
        pagamento.setPedidoId(2L);
        pagamento.setValor(50.0);
        pagamento.setFormaPagamento(FormaPagamento.PIX);
        pagamento.setStatus(StatusPagamento.APROVADO);
        jpaPagamentoRepository.save(pagamento);

        PagamentoEntity encontrado = pagamentoRepositoryGateway.findByPedidoId(2L);

        assertNotNull(encontrado);
        assertEquals(2L, encontrado.getPedidoId());
        assertEquals(StatusPagamento.APROVADO, encontrado.getStatus());
    }

    @Test
    void deveRetornarTrueSePagamentoAprovadoExistirParaPedido() {
        PagamentoEntity pagamento = new PagamentoEntity();
        pagamento.setPedidoId(3L);
        pagamento.setValor(75.0);
        pagamento.setFormaPagamento(FormaPagamento.ESPECIE);
        pagamento.setStatus(StatusPagamento.APROVADO);
        jpaPagamentoRepository.save(pagamento);

        boolean exists = pagamentoRepositoryGateway.existsByPedidoIdAndPagamentoStatusAprovado(3L);

        assertTrue(exists);
    }

    @Test
    void deveRetornarFalseSePagamentoNaoForAprovado() {
        PagamentoEntity pagamento = new PagamentoEntity();
        pagamento.setPedidoId(4L);
        pagamento.setValor(80.0);
        pagamento.setFormaPagamento(FormaPagamento.CREDITO);
        pagamento.setStatus(StatusPagamento.PENDENTE);
        jpaPagamentoRepository.save(pagamento);

        boolean exists = pagamentoRepositoryGateway.existsByPedidoIdAndPagamentoStatusAprovado(4L);

        assertFalse(exists);
    }
}
