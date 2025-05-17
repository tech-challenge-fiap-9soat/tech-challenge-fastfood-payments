package br.com.fastfood.payments.infra.repository;

import br.com.fastfood.payments.domain.entities.PagamentoEntity;
import br.com.fastfood.payments.infra.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPagamentoRepository extends JpaRepository<PagamentoEntity, Long> {

    boolean existsByPedidoIdAndStatus(Long pedidoId, StatusPagamento status);

    PagamentoEntity findByPedidoId(Long id);

}
