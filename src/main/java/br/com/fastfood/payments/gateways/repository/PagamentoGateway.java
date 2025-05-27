package br.com.fastfood.payments.gateways.repository;

import br.com.fastfood.payments.domain.entities.PagamentoEntity;

public interface PagamentoGateway {
    PagamentoEntity save(PagamentoEntity pagamento);

    boolean existsByPedidoIdAndPagamentoStatusAprovado(Long id);

    PagamentoEntity findByPedidoId(Long id);
}
