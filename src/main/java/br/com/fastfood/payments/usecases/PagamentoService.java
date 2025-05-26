package br.com.fastfood.payments.usecases;

import br.com.fastfood.payments.domain.entities.PagamentoEntity;
import br.com.fastfood.payments.infra.dto.PagamentoDTO;
import org.springframework.http.ResponseEntity;

public interface PagamentoService {
    void processaRetornoPagamento(PagamentoEntity pagamento);

    void fazPagamento(PagamentoDTO pagamento);

    void deleteAll();

    ResponseEntity<String> consultarStatusPagamentoPedido(Long idPedido);
}