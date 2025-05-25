package br.com.fastfood.payments.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {
    PAGAMENTO_PENDENTE("O pagamento do pedido %s ainda está pendente."),
    PEDIDO_INVALIDO("Não é possível criar um pedido sem produtos."),
    VALOR_INCORRETO("Valor do pagamento não é igual ao valor do pedido");

    private String message;
}
