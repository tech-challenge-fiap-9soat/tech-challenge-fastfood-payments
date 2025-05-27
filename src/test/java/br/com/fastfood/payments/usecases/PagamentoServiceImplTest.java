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
import br.com.fastfood.payments.infra.enums.FormaPagamento;
import br.com.fastfood.payments.infra.enums.StatusPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class PagamentoServiceImplTest {

    @InjectMocks
    private PagamentoServiceImpl pagamentoService;

    @Mock
    private PagamentoGateway pagamentoGateway;

    @Mock
    private PagamentoClient pagamentoClient;

    @Mock
    private CachePedidosClient cachePedidosClient;

    @Mock
    private PedidoClient pedidoClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveProcessarRetornoPagamentoAlterandoStatusParaAprovado() {
        PagamentoEntity pagamento = new PagamentoEntity();
        pagamento.setStatus(StatusPagamento.PENDENTE);

        pagamentoService.processaRetornoPagamento(pagamento);

        assertEquals(StatusPagamento.APROVADO, pagamento.getStatus());
        verify(pagamentoGateway, times(1)).save(pagamento);
    }

    @Test
    void deveFazerPagamentoComSucesso() {
        PagamentoDTO pagamentoDTO = new PagamentoDTO();
        pagamentoDTO.setIdPedido(1L);
        pagamentoDTO.setValor(100.0);
        pagamentoDTO.setFormaPagamento(FormaPagamento.CREDITO);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(1L);
        pedidoDTO.setCpf("483.621.128.23");
        pedidoDTO.setValorTotal(100.0);
        pedidoDTO.setStatusPedido("RECEBIDO");

        when(cachePedidosClient.getPedido(pagamentoDTO.getIdPedido())).thenReturn(pedidoDTO);

        ArgumentCaptor<PagamentoEntity> captor = ArgumentCaptor.forClass(PagamentoEntity.class);

        pagamentoService.fazPagamento(pagamentoDTO);

        verify(pagamentoGateway, times(1)).save(captor.capture());
        verify(pagamentoClient, times(1)).enviaPagamento(any(PagamentoEntity.class));
        verify(pedidoClient, times(1)).updateStatusPedido(any(Long.class));

        PagamentoEntity savedPagamento = captor.getValue();
        assertEquals(StatusPagamento.PENDENTE, savedPagamento.getStatus());
        assertEquals(pagamentoDTO.getValor(), savedPagamento.getValor());
        assertEquals(pagamentoDTO.getFormaPagamento(), savedPagamento.getFormaPagamento());
        assertEquals(pagamentoDTO.getIdPedido(), savedPagamento.getPedidoId());
    }

    @Test
    void deveRetornarStatusPagamentoQuandoExistente() {
        Long pedidoId = 1L;
        PagamentoEntity pagamentoEntity = new PagamentoEntity();
        pagamentoEntity.setPedidoId(pedidoId);
        pagamentoEntity.setStatus(StatusPagamento.APROVADO);

        when(pagamentoGateway.findByPedidoId(pedidoId)).thenReturn(pagamentoEntity);

        ResponseEntity<String> response = pagamentoService.consultarStatusPagamentoPedido(pedidoId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("APROVADO", response.getBody());
    }

    @Test
    void deveRetornarMensagemDeErroQuandoPagamentoNaoExistir() {
        Long pedidoId = 1L;
        when(pagamentoGateway.findByPedidoId(pedidoId)).thenReturn(null);

        ResponseEntity<String> response = pagamentoService.consultarStatusPagamentoPedido(pedidoId);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Não existe pagamento para este pedido", response.getBody());
    }

    @Test
    void deveLancarExcecaoQuandoValorPagamentoForDiferenteDoPedido() {
        PagamentoDTO pagamentoDTO = new PagamentoDTO();
        pagamentoDTO.setIdPedido(1L);
        pagamentoDTO.setValor(100.0);
        pagamentoDTO.setFormaPagamento(FormaPagamento.CREDITO);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(1L);
        pedidoDTO.setCpf("483.621.128.23");
        pedidoDTO.setValorTotal(200.0); // <- Valor diferente!
        pedidoDTO.setStatusPedido("RECEBIDO");

        when(cachePedidosClient.getPedido(pagamentoDTO.getIdPedido())).thenReturn(pedidoDTO);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> pagamentoService.fazPagamento(pagamentoDTO)
        );

        assertEquals(ExceptionEnum.VALOR_INCORRETO, exception.getExceptionEnum());
    }

}
