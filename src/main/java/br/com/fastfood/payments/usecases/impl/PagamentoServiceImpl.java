package br.com.fastfood.payments.usecases.impl;

import br.com.fastfood.payments.domain.entities.PagamentoEntity;
import br.com.fastfood.payments.domain.exception.BusinessException;
import br.com.fastfood.payments.gateways.client.CachePedidosClient;
import br.com.fastfood.payments.gateways.client.PagamentoClient;
import br.com.fastfood.payments.gateways.client.PedidoClient;
import br.com.fastfood.payments.gateways.repository.PagamentoGateway;
import br.com.fastfood.payments.infra.dto.PagamentoDTO;
import br.com.fastfood.payments.infra.dto.PedidoDTO;
import br.com.fastfood.payments.infra.enums.ExceptionEnum;
import br.com.fastfood.payments.infra.enums.StatusPagamento;
import br.com.fastfood.payments.usecases.PagamentoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PagamentoServiceImpl implements PagamentoService {

    @Autowired
    private PagamentoGateway pagamentoGateway;

    @Autowired
    private PagamentoClient pagamentoClient;

    @Autowired
    private PedidoClient pedidoClient;

    @Autowired
    private CachePedidosClient cachePedidosClient;

    @Override
    public void processaRetornoPagamento(PagamentoEntity pagamento) {
        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamentoGateway.save(pagamento);
    }

    @Override
    public void fazPagamento(PagamentoDTO pagamento) {
        PedidoDTO pedido = cachePedidosClient.getPedido(pagamento.getIdPedido());

        if (!Objects.equals(pagamento.getValor(), pedido.getValorTotal())) {
            throw new BusinessException(ExceptionEnum.VALOR_INCORRETO);
        }

        PagamentoEntity existPagamento = pagamentoGateway.findByPedidoId(pedido.getId());
        if (existPagamento != null && existPagamento.getStatus().equals(StatusPagamento.APROVADO)) {
            throw new BusinessException(ExceptionEnum.PAGAMENTO_REALIZADO);
        }

        PagamentoEntity pagamentoEntity = new PagamentoEntity();
        pagamentoEntity.setValor(pagamento.getValor());
        pagamentoEntity.setFormaPagamento(pagamento.getFormaPagamento());
        pagamentoEntity.setStatus(StatusPagamento.PENDENTE);
        pagamentoEntity.setPedidoId(pagamento.getIdPedido());

        pagamentoGateway.save(pagamentoEntity);
        pedidoClient.updateStatusPedido(pedido.getId());
        pagamentoClient.enviaPagamento(pagamentoEntity);
    }

    @Override
    public ResponseEntity<String> consultarStatusPagamentoPedido(Long idPedido) {
        PagamentoEntity pagamentoEntity = pagamentoGateway.findByPedidoId(idPedido);
        if (pagamentoEntity == null) {
            return new ResponseEntity<>("Não existe pagamento para este pedido", HttpStatusCode.valueOf(400));
        }
        return ResponseEntity.ok(pagamentoEntity.getStatus().name());
    }
}
