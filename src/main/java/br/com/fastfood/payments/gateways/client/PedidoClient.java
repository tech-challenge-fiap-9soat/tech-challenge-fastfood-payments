package br.com.fastfood.payments.gateways.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "pedido-client", url = "${external.order.url}")
public interface PedidoClient {

    @PutMapping(value = "/pedido/proxima-operacao/{idPedido}")
    void updateStatusPedido(@PathVariable Long idPedido);

}
