package br.com.fastfood.payments.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {

    VALOR_INCORRETO("Valor do pagamento não é igual ao valor do pedido");

    private String message;

}
