# language: pt

Funcionalidade: Gerenciar pagamentos de pedidos

  Cenário: Registrar um novo pagamento com sucesso
    Quando eu envio um pagamento com valor 100.00 e forma de pagamento "PIX" e ID 1
    Então o pagamento deve ser salvo com status "PENDENTE"
    E o status HTTP da resposta deve ser 200

  Cenário: Consultar o status de um pagamento existente
    Dado que existe um pagamento com status "APROVADO" para o pedido com ID 1
    Quando eu consultar o status do pagamento para o pedido com ID 1
    Então a resposta deve conter "APROVADO"
    E o status HTTP da resposta deve ser 200

  Cenário: Consultar o status de um pagamento inexistente
    Dado que não existe pagamento para o pedido com ID 999
    Quando eu consultar o status do pagamento para o pedido com ID 999
    Então a resposta deve conter "Não existe pagamento para este pedido"
    E o status HTTP da resposta deve ser 400
