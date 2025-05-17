package br.com.fastfood.payments.infra.enums;

public enum FormaPagamento {
    PIX(1, "PIX"),
    DEBITO(2, "DEBITO"),
    CREDITO(3, "CREDITO"),
    ESPECIE(4, "ESPECIE");

    private int id;
    private String descricao;

    FormaPagamento(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }
}