package br.com.fastfood.payments.infra.enums;

public enum StatusPagamento {
    PENDENTE(1, "Pendente"),
    APROVADO(2, "Aprovado"),
    REPROVADO(3, "Reprovado");

    private int id;
    private String descricao;

    StatusPagamento(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }
}