package br.com.fastfood.payments.infra.dto;

import br.com.fastfood.payments.infra.enums.FormaPagamento;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoDTO {

    @NotNull
    private Double valor;

    @NotNull
    private FormaPagamento formaPagamento;

    @NotNull
    private Long idPedido;
}
