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
| PUT | `/agendamentos/{id}` | Altera cliente, serviço e horário | `200 OK` |
| DELETE | `/agendamentos/{id}` | Remove um agendamento | `204 No Content` |

### Exemplo

```http
POST /agendamentos
Content-Type: application/json

{
  "servico": "Corte de cabelo",
  "profissional": "Ana",
  "dataHoraAgendamento": "2026-12-01T10:00:00",
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
| cliente | String |
| telefoneCliente | String |
| dataInsercao | LocalDateTime (preenchida na criação) |

## Regra de negócio

Ao criar um agendamento, o serviço consulta se já existe um agendamento do **mesmo serviço** na janela de 1 hora a partir do horário pedido. Se existir, a criação é recusada com a mensagem "Horario ja esta preenchido".

## Estrutura

```
src/main/java/com/maspoxajoao/agendador_horarios/
├── AgendadorHorariosApplication.java
├── controller/AgendamentoController.java
├── services/AgendamentoService.java
└── infrastructure/
    ├── entity/AgendamentoEntity.java
    └── repository/AgendamentoRepository.java
```

## Limitações conhecidas e próximos passos

Este projeto está em evolução. O que ainda falta:

- [ ] Usar DTOs de entrada e saída em vez de expor a entidade
- [ ] Validação dos campos (Bean Validation)
- [ ] Tratamento global de erros: hoje um horário ocupado ou um id inexistente resultam em erro 500. O correto é `409 Conflict` e `404 Not Found`
- [ ] Regra de conflito completa: considerar sobreposição de horários, o profissional e a alteração (PUT)
- [ ] Testes automatizados: existe apenas o teste de carregamento do contexto
- [ ] Persistência em PostgreSQL com Docker Compose
- [ ] Documentação OpenAPI/Swagger

## Autor

**João Victor** · [LinkedIn](https://www.linkedin.com/in/jvbraga-dev/)
