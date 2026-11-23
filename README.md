# Agendador de Horários

API REST para criar, consultar, alterar e cancelar agendamentos de serviços, impedindo que dois agendamentos ocupem o mesmo horário.

Projeto de estudo em Java e Spring Boot, organizado em camadas (controller, service e repository).

## Tecnologias

- Java 21
- Spring Boot 4.0.6 (Web MVC, Data JPA)
- Hibernate / JPA
- H2 (banco em memória)
- Lombok
- Maven (wrapper incluído)

## Como executar

Pré-requisito: JDK 21.

```bash
git clone https://github.com/maspoxajoao/agendador-horarios.git
cd agendador-horarios
./mvnw spring-boot:run        # no Windows: mvnw.cmd spring-boot:run
```

A API sobe em `http://localhost:8080`.

O banco é H2 **em memória**: os dados são apagados toda vez que a aplicação reinicia. O console do banco fica em `http://localhost:8080/h2-console`:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:agendamentos-db` |
| Usuário | `sa` |
| Senha | (vazia) |

## Endpoints

Base: `/agendamentos`

| Método | Rota | Descrição | Resposta |
|---|---|---|---|
| POST | `/agendamentos` | Cria um agendamento | `201 Created` |
| GET | `/agendamentos?data=2026-12-01` | Lista os agendamentos de um dia | `200 OK` |
| PUT | `/agendamentos/{id}` | Altera um agendamento | `200 OK` |
| DELETE | `/agendamentos/{id}` | Remove um agendamento | `204 No Content` |

### Erros

| Situação | Resposta |
|---|---|
| Campos inválidos ou ausentes, ou horário no passado | `400 Bad Request` com os campos com erro |
| Profissional já ocupado no horário | `409 Conflict` |
| Agendamento inexistente (PUT ou DELETE) | `404 Not Found` |

### Exemplo

```http
POST /agendamentos
Content-Type: application/json

{
  "servico": "Corte de cabelo",
  "profissional": "Ana",
  "dataHoraAgendamento": "2026-12-01T10:00:00",
  "duracaoMinutos": 30,
  "cliente": "Maria Souza",
  "telefoneCliente": "31999999999"
}
```

```http
GET /agendamentos?data=2026-12-01
```

## Modelo de dados

Tabela `agendamento`:

| Campo | Tipo |
|---|---|
| id | Long (gerado automaticamente) |
| servico | String |
| profissional | String |
| dataHoraAgendamento | LocalDateTime |
| duracaoMinutos | Integer (30 ou 60; opcional, padrão 60) |
| cliente | String |
| telefoneCliente | String |
| dataInsercao | LocalDateTime (preenchida na criação) |

## Regra de negócio

O atendimento dura **30 minutos ou 1 hora** (`duracaoMinutos`, padrão 60). Um profissional não pode ter dois agendamentos que se sobreponham: o novo intervalo (início + duração) não pode cruzar o de nenhum outro agendamento do mesmo profissional.

- Os limites são exclusivos: 10:00–10:30 e 10:30–11:00 **não** conflitam
- 10:00 (1h) e 10:30 conflitam
- Qualquer outro valor de `duracaoMinutos` retorna `400`
- Na alteração (`PUT`), o próprio agendamento é ignorado na checagem
- Profissionais diferentes podem atender no mesmo horário

## Estrutura

```
src/main/java/com/maspoxajoao/agendador_horarios/
├── AgendadorHorariosApplication.java
├── controller/AgendamentoController.java
├── dto/                      (AgendamentoRequest e AgendamentoResponse)
├── exception/                (exceções de domínio e handler global)
├── services/AgendamentoService.java
└── infrastructure/
    ├── entity/AgendamentoEntity.java
    └── repository/AgendamentoRepository.java
```

## Testes

```bash
./mvnw test
```

Os testes do serviço (`AgendamentoServiceTest`) cobrem criação, conflito de horário (inclusive com durações diferentes), alteração ignorando o próprio id, ids inexistentes e o intervalo de busca do dia.

## Limitações conhecidas e próximos passos

- [x] DTOs de entrada e saída
- [x] Validação dos campos (Bean Validation)
- [x] Tratamento global de erros (`400`, `404` e `409`)
- [x] Regra de conflito por profissional, com limites exclusivos e alteração ignorando o próprio id
- [x] Testes unitários do serviço
- [ ] Testes de integração dos endpoints (MockMvc)
- [x] Duração de 30 minutos ou 1 hora
- [ ] Duração configurável por serviço
- [ ] Persistência em PostgreSQL com Docker Compose
- [ ] Documentação OpenAPI/Swagger

## Autor

**João Victor** · [LinkedIn](https://www.linkedin.com/in/jvbraga-dev/)
