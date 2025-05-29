# Tech Challenge FastFood Payments

Este projeto faz parte do Tech Challenge da pós-graduação em Arquitetura de Software da FIAP. Ele representa o microsserviço responsável pelo processamento de pagamentos no sistema de autoatendimento para uma lanchonete em expansão.

## 📚 Visão Geral

O **FastFood Payments** é um microsserviço desenvolvido em Java, utilizando o framework Spring Boot, que gerencia o fluxo de pagamentos dos pedidos realizados no sistema de autoatendimento. Ele integra-se com plataformas de pagamento, como o Mercado Pago, para processar transações de forma segura e eficiente.

## 🚀 Tecnologias Utilizadas

- **Linguagem:** Java 21  
- **Framework:** Spring Boot  
- **Gerenciador de Dependências:** Maven 3.9.9  
- **Banco de Dados:** PostgreSQL 17  
- **Containerização:** Docker  
- **Orquestração:** Kubernetes  

## 🛠️ Configuração e Execução

1. **Pré-requisitos:**
   - Java 21
   - Maven 3.9.9
   - Docker
   - Kubernetes com `kubectl` configurado

2. **Clone o repositório:**

   ```bash
   git clone https://github.com/tech-challenge-fiap-9soat/tech-challenge-fastfood-payments.git
   cd tech-challenge-fastfood-payments
   ```
## Implante os recursos no Kubernetes:

3. Execute os seguintes comandos para aplicar os manifests:

```bash
  kubectl apply -f k8s-infra/env/secret.yaml
  kubectl apply -f k8s-infra/env/configmap.yaml
  kubectl apply -f k8s-infra/db/postgresdb-statefulset.yaml
  kubectl apply -f k8s-infra/db/postgresdb-service.yaml
  kubectl apply -f k8s-infra/fastfoodapi/fastfoodapi-deployment.yaml
  kubectl apply -f k8s-infra/fastfoodapi/fastfoodapi-service.yaml
  kubectl apply -f k8s-infra/hpa/fastfoodapi-hpa.yaml
```

Acesse a aplicação:

Após a implantação, a aplicação estará disponível em: http://localhost:30001/fastfood/swagger-ui/index.html

##📄 Documentação
A documentação completa da API, incluindo os endpoints disponíveis, pode ser acessada via Swagger UI no link fornecido acima.

## Segue Evidência de testes e coberturas:

![image](https://github.com/user-attachments/assets/01423189-4b05-4b15-9659-592c5a2cbac3)

#### segue os casos de testes, o ultimo "Gerenciar pagamentos de pedidos é o BDD com Cucumber

![image](https://github.com/user-attachments/assets/ecc8d6fa-0254-4a0d-be53-d5e22333aaf3)

