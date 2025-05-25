package br.com.fastfood.payments.infra.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDTO {

    @NotNull
    private Long id;

    @NotNull
    private String cpf;

    @NotNull
    private String statusPedido;

    @NotNull
    private Double valorTotal;

    @NotNull
    private String criadoEm;

}
