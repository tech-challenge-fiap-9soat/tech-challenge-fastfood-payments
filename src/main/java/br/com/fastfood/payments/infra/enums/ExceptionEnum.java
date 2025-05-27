package br.com.fastfood.payments.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {

    VALOR_INCORRETO("Valor do pagamento não é igual ao valor do pedido"),
    PAGAMENTO_REALIZADO("Pagamento para este pedido já foi realizado");

    private String message;

}
