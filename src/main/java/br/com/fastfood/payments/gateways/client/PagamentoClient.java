package br.com.fastfood.payments.gateways.client;

import br.com.fastfood.payments.domain.entities.PagamentoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pagamento-client", url = "${external.api.url}")
public interface PagamentoClient {

    @PostMapping
    void enviaPagamento(@RequestBody PagamentoEntity pagamento);
}
