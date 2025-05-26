package br.com.fastfood.payments.infra.controller;

import br.com.fastfood.payments.infra.dto.PagamentoDTO;
import br.com.fastfood.payments.usecases.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamento")
public class PagamentoController {
    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping("/pagar")
    public void enviaPagamento(@RequestBody PagamentoDTO payloadPagamento) {
        pagamentoService.fazPagamento(payloadPagamento);
    }

    @GetMapping("/{idPedido}")
    public ResponseEntity<String> consultarStatusPagamentoPedido(@PathVariable Long idPedido) {
        return pagamentoService.consultarStatusPagamentoPedido(idPedido);
    }

    @GetMapping("/reset-db")
    public void resetDb() {
        pagamentoService.deleteAll();
    }

}
