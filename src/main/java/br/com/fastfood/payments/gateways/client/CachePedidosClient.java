package br.com.fastfood.payments.gateways.client;

import br.com.fastfood.payments.infra.dto.PedidoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cache-pedido-client", url = "${external.cache.order.url}")
public interface CachePedidosClient {

    @GetMapping(value = "/pedido/{idPedido}")
    PedidoDTO getPedido(@PathVariable Long idPedido);

}
