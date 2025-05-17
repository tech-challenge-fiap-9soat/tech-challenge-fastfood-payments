package br.com.fastfood.payments.usecases.impl;

import br.com.fastfood.payments.domain.entities.PagamentoEntity;
import br.com.fastfood.payments.gateways.client.PagamentoClient;
import br.com.fastfood.payments.gateways.repository.PagamentoGateway;
import br.com.fastfood.payments.infra.dto.PagamentoDTO;
import br.com.fastfood.payments.infra.enums.StatusPagamento;
import br.com.fastfood.payments.usecases.PagamentoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PagamentoServiceImpl implements PagamentoService {

    @Autowired
    private PagamentoGateway pagamentoGateway;

    @Autowired
    private PagamentoClient pagamentoClient;

//    @Autowired
//    private PedidoGateway pedidoGateway;

    @Override
    public void processaRetornoPagamento(PagamentoEntity pagamento) {
        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamentoGateway.save(pagamento);
    }

    /**
     * TODO: chamar API de produção para atualizar status do pedido
     **/
    @Override
    public void fazPagamento(PagamentoDTO pagamento) {
//        PedidoEntity pedidoEntity = pedidoGateway.findById(pagamento.getIdPedido())
//                .orElseThrow(() -> new EntityNotFoundException("Pedido com id " + pagamento.getIdPedido() + " não encontrado"));

//        if (!Objects.equals(pagamento.getValor(), pedidoEntity.getValorTotal())) {
//            throw new BusinessException(ExceptionEnum.VALOR_INCORRETO);
//        }

        PagamentoEntity pagamentoEntity = new PagamentoEntity();
        pagamentoEntity.setValor(pagamento.getValor());
        pagamentoEntity.setFormaPagamento(pagamento.getFormaPagamento());
        pagamentoEntity.setStatus(StatusPagamento.PENDENTE);
        pagamentoEntity.setPedidoId(pagamento.getIdPedido());
//        pedidoEntity.setStatusPedido(StatusPedido.EM_PREPARACAO);

        pagamentoGateway.save(pagamentoEntity);
//        pedidoGateway.save(pedidoEntity);
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
