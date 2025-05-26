package br.com.fastfood.payments.gateways.repository.impl;

import br.com.fastfood.payments.domain.entities.PagamentoEntity;
import br.com.fastfood.payments.gateways.repository.PagamentoGateway;
import br.com.fastfood.payments.infra.enums.StatusPagamento;
import br.com.fastfood.payments.infra.repository.JpaPagamentoRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PagamentoRepositoryGateway implements PagamentoGateway {

    @Autowired
    private JpaPagamentoRepository pagamentoRepository;

    @Override
    public PagamentoEntity save(PagamentoEntity pagamento) {
        return pagamentoRepository.save(pagamento);
    }

    public void deleteAll() {
        pagamentoRepository.deleteAll();
    }

    @Override
    public boolean existsByPedidoIdAndPagamentoStatusAprovado(Long id) {
        return pagamentoRepository.existsByPedidoIdAndStatus(id, StatusPagamento.APROVADO);
    }

    @Override
    public PagamentoEntity findByPedidoId(Long id) {
        return pagamentoRepository.findByPedidoId(id);
    }
}
