# java-ms-ecommerce

Projeto de microservicos que simula um fluxo real de pedidos:
criar pedido, reservar estoque, processar pagamento e compensar em caso de falha.

## Arquitetura e fluxo
Servicos principais:
- gateway
- order-service
- inventory-service
- payment-service

Fluxo principal:
1) Criar pedido
2) Reservar estoque
3) Processar pagamento
4) Se falhar, liberar estoque e cancelar pedido

## Estrutura do repositorio
```
java-ms-ecommerce/
  gateway/
  order-service/
  inventory-service/
  payment-service/
  postman/
```

## Requisitos
- Java 17+ (projeto compila com release 17)
- Maven Wrapper (ja incluido nos servicos)
- Docker Desktop (para banco de dados e testes de integracao)

## Quick start (order-service)
Windows (CMD):
```
cd C:\Users\Vinic\Desktop\E-commerce\java-ms-ecommerce\order-service
docker compose up -d
.\mvnw.cmd spring-boot:run
```

Linux/macOS:
```
cd java-ms-ecommerce/order-service
docker compose up -d
./mvnw spring-boot:run
```

## Executar servicos localmente
Cada servico pode ser iniciado separadamente com o Maven Wrapper do proprio diretorio.

Exemplo (Windows):
```
cd java-ms-ecommerce\gateway
.\mvnw.cmd spring-boot:run
```

Exemplo (Linux/macOS):
```
cd java-ms-ecommerce/gateway
./mvnw spring-boot:run
```

Obs: portas e configuracoes especificas estao nos `application.yml` de cada servico.

## Banco de dados
O `order-service` usa Postgres via Docker Compose:
```
cd java-ms-ecommerce/order-service
docker compose up -d
```

Para parar:
```
docker compose down
```

## Testes de integracao (order-service)
O teste de integracao usa Postgres via Docker Compose e WireMock para dependencias externas.

1) Suba o banco:
```
cd java-ms-ecommerce/order-service
docker compose up -d
```

2) Execute o teste:
```
.\mvnw.cmd -Dtest=OrderServiceIT test
```
ou
```
./mvnw -Dtest=OrderServiceIT test
```

Config de teste:
- `order-service/src/test/resources/application-test.yml`

## Postman
Colecoes de teste ficam em:
```
postman/
```

## Troubleshooting rapido
- Se ver warning sobre `version` no compose, pode remover a chave `version` do `docker-compose.yml`.
- Se o Postgres nao estiver respondendo, verifique `docker ps` e os logs do container.

## Observacoes tecnicas
- Resiliencia no fluxo de pagamento com Retry e Circuit Breaker
- Compensacao de estoque em falhas
- Tratamento consistente de erros via `RestExceptionHandler`
