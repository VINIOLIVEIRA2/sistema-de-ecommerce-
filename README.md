
# 🛒 Order Service — Java Microservice (E-commerce Case)

Este projeto implementa um **Order Service** de um e-commerce, seguindo **arquitetura de microsserviços**, com foco em **boas decisões arquiteturais**, **integrações reais**, **resiliência** e **testes de integração confiáveis**.

> ⚠️ **Não é projeto de curso.**
> É um **case técnico**, construído para simular problemas e decisões reais de ambiente e produção.

---

## 🎯 Objetivo do projeto

Demonstrar, na prática, que o desenvolvedor:

* Domina **arquitetura limpa (Clean / Hexagonal)**
* Entende **Spring Boot em profundidade** (AOP, transações, proxies)
* Sabe **integrar serviços externos** com resiliência
* Prioriza **valor técnico sobre dogmas**
* Resolve **problemas reais de infra e runtime**
* Escreve **testes de integração realistas**

---

## 🧱 Arquitetura

Arquitetura baseada em **Ports & Adapters (Hexagonal)**:

```
Controller (REST)
 └── Application Service (Use Cases)
      └── Domain (Entidades + Regras)
           └── Ports (Interfaces)
                └── Adapters (DB, HTTP, Infra)
```

### Princípios aplicados

* Inversão de dependência
* Domain independente de frameworks
* Application Service orquestra casos de uso
* Controllers apenas traduzem HTTP ↔ domínio
* Infra completamente substituível

---

## ⚙️ Stack tecnológica

* **Java 17+** (rodando localmente com Java 25)
* **Spring Boot 3.3.x**
* **Spring Data JPA / Hibernate**
* **PostgreSQL 16**
* **Docker + Docker Compose**
* **WireMock** (mock de serviços externos)
* **Resilience4j** (Retry / CircuitBreaker)
* **JUnit 5**
* **Maven Wrapper**

---

## 🧪 Estratégia de testes (ponto central do case)

### Abordagem

O projeto foca em **testes de integração reais**, não apenas mocks unitários.

Os testes:

* Sobem o **Spring Boot completo**
* Usam **PostgreSQL real**
* Simulam serviços externos via **WireMock**
* Validam **fluxos de negócio completos**

### Casos testados

* Criar pedido com pagamento aprovado
* Criar pedido com pagamento recusado → **pedido cancelado**
* Buscar pedido inexistente → **404**

---

## 🔁 Decisão arquitetural importante (Testcontainers → Docker Compose)

### Estado inicial

O projeto começou usando **Testcontainers**.

### Problemas reais encontrados

* Incompatibilidade entre Docker Desktop, docker-java e WSL2
* Falhas intermitentes de socket (Unix / Npipe / TCP)
* Tempo excessivo gasto em **infra**, não em código

### Decisão madura

👉 **Troca consciente para Docker Compose**

Motivo:

> Para um case de portfólio, o valor está na **arquitetura e nos testes**, não em debug de runtime Docker.

### Resultado

* Infra estável e previsível
* Testes mais rápidos
* Execução garantida em qualquer máquina com Docker

---

## 🧨 Problemas reais enfrentados (e resolvidos)

### 1️⃣ HTTP 500 indevido em exceção de domínio

**Causa**
Exceção de negócio (`OrderNotFound`) não era corretamente mapeada.

**Solução**

* Exceção específica
* Mapeamento correto → `404 NOT_FOUND`

---

### 2️⃣ Resilience4j não executava fallback

**Causa clássica de Spring**
Anotações `@Retry` / `@CircuitBreaker` não funcionam em **chamadas internas** (AOP proxy não aplicado).

**Solução**

* Uso de `ObjectProvider`
* Chamada passando pelo proxy Spring

> Demonstra conhecimento real de **Spring AOP**, não superficial.

---

### 3️⃣ Transaction rollback-only (bug final)

**Sintoma**

* Pagamento recusado gerava `500 INTERNAL_SERVER_ERROR`
* Mensagem: *“Transaction silently rolled back because it has been marked as rollback-only”*

**Causa**

* Exceção de pagamento era tratada como falha técnica
* RuntimeException marcava a transação como rollback-only

**Solução correta**

* **Pagamento recusado tratado como regra de negócio**, não falha técnica
* Criação de `PaymentDeclinedException`
* Fluxo de cancelamento/liberação executado **sem contaminar a transação**

✅ Resultado: pedido criado com status **CANCELED** e resposta **201 CREATED**

---

## 🚀 Como rodar o projeto

### Subir infra

```bash
docker compose up -d
```

### Rodar testes

```bash
./mvnw test
```

---

## 📊 Estado atual

| Item                 | Status     |
| -------------------- | ---------- |
| Arquitetura          | ✅          |
| Infra local          | ✅          |
| Integrações externas | ✅          |
| Resiliência          | ✅          |
| Testes de integração | ✅          |
| Build                | 🟢 SUCCESS |

---

## 🧠 Lições aprendidas

* Nem todo erro deve virar exception
* Regra de negócio ≠ falha técnica
* Testes de integração revelam bugs que unit tests escondem
* Às vezes, **trocar a ferramenta é a melhor decisão técnica**
* Conhecimento real de Spring aparece em edge cases (AOP, transações)

---

## 🏁 Conclusão

Este projeto demonstra **maturidade técnica**, foco em **valor real** e experiência com **problemas de produção**.

> Não é um projeto “perfeito”.
> É um projeto **honesto**, **realista** e **profissional**.

