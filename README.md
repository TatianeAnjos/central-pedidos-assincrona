# Central de Pedidos Assíncrona
Este projeto implementa um sistema distribuído e resiliente para processamento de pedidos e envio de notificações via mensageria, utilizando Spring Boot, Apache Kafka e padrões de microsserviços.

## A Central de Pedidos Assíncrona tem como foco:
Processar pedidos de forma desacoplada e resiliente

Disparar notificações por e-mail em eventos de criação de pedidos

Garantir confiabilidade na entrega das mensagens e resiliência a falhas

## Fluxo de dados
Cliente realiza requisição POST /pedidos

Pedido é persistido no banco de dados

Um evento é publicado no Kafka

O projeto central-pedidos-notificacoes consome o evento e envia a notificação ao cliente

## API Central de Pedidos
Expõe endpoints REST

Publica eventos no Kafka

## Central Pedidos Notificacoes
Serviço desacoplado

Consome eventos do Kafka

Envia e-mails com resiliência

## Tecnologias Utilizadas

  - Spring Boot 3

  - Apache Kafka

  - Spring Kafka

  - PostgreSQL

  - JPA

  - Docker

  - Micrometer + Prometheus + Grafana (para observabilidade)

  - Resilience4j (retry, circuit breaker)

  - Mockito + MockMvc + JUnit

🔐 Resiliência
  - Retry com backoff para falhas transitórias
  - Circuit Breaker para isolar falhas persistentes
  - Idempotência para evitar retrabalho

📊 Observabilidade

Métricas expostas via /actuator/prometheus

Dashboards Grafana com métricas de:
 - Volume de pedidos por tipo
 - Tempo de resposta por endpoint
 - Falhas e tempo de retry nos consumidores Kafka

🧪 Testes

  - Testes unitários com JUnit + Mockito

  - Testes de integração com Embedded Kafka e mockMvc

🔮 Melhorias Futuras

  - Autenticação via OAuth2
