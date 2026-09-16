# FIAP SOAT Vehicle Resale API

API REST para cadastro, atualização, consulta e venda de veículos usados, desenvolvida como projeto da pós-graduação SOAT (FIAP).

## O que o projeto faz

O sistema modela um pátio de revenda de veículos:

- **Cadastro de veículos** (`ADMIN`): marca, modelo, cor, ano, descrição e preço. Todo veículo nasce com status `AVAILABLE`.
- **Atualização de veículos** (`ADMIN`): edição dos dados de um veículo existente, desde que ele ainda não tenha sido vendido.
- **Listagem de veículos** (público): lista veículos filtrando por status (`AVAILABLE` ou `SOLD`); sem filtro, retorna apenas os `AVAILABLE`.
- **Compra de veículo** (`CUSTOMER`): efetua a venda de um veículo disponível, criando um registro de venda (`sales`) e marcando o veículo como `SOLD`. O comprador é identificado pelo `subject` do token JWT autenticado.

Regras de negócio relevantes:

- Um veículo com ano muito no futuro (mais de 1 ano à frente do ano atual) é rejeitado na criação (`InvalidVehicleException`).
- Não é possível atualizar nem comprar um veículo já vendido (`VehicleAlreadySoldException`, HTTP 409).
- Atualização e compra usam **lock pessimista** (`SELECT ... FOR UPDATE`) na leitura do veículo, para evitar que um `PUT` concorrente e uma compra alterem o mesmo veículo entre a checagem de status e a escrita.

## Como foi implementado

### Arquitetura

O projeto segue **arquitetura hexagonal (ports & adapters)**:

```
src/main/java/com/soat/vehicle_resale/
├── core/
│   ├── domain/
│   │   ├── models/         # Vehicle, Sale, VehicleStatus (records imutáveis, validam invariantes no construtor)
│   │   └── exceptions/     # Exceções de negócio (InvalidVehicleException, VehicleNotFoundException, VehicleAlreadySoldException)
│   └── application/
│       ├── ports/
│       │   ├── inbound/usecase/   # Casos de uso expostos ao mundo externo (interfaces)
│       │   └── outbound/          # Portas de persistência (interfaces, ex: VehicleRepositoryPort)
│       └── services/               # Implementação dos casos de uso (VehicleService, SaleService)
│
└── infrastructure/
    ├── adapters/
    │   ├── inbound/web/            # Controllers REST, DTOs e handler global de exceções
    │   └── outbound/database/      # Entidades JPA, mappers (MapStruct), repositórios Spring Data e implementação das outbound ports
    └── configuration/              # Segurança (Spring Security + OAuth2/JWT), OpenAPI
```

O `core` não depende de Spring, JPA ou qualquer framework: apenas Java puro. A `infrastructure` implementa as portas definidas pelo `core` e é o único lugar que conhece frameworks e tecnologia.

### Stack técnica

- **Java 25** + **Spring Boot 4.1.1** (Web MVC, Data JPA, Validation, Actuator, Security)
- **PostgreSQL 17** como banco de dados, com **Flyway** para versionamento de schema (`src/main/resources/db/migration`)
- **Spring Security + OAuth2 Resource Server (JWT)**: autorização baseada em roles extraídas do `realm_access.roles` de um token JWT emitido por um Keycloak (ou qualquer provedor OAuth2 compatível)
- **MapStruct** para mapeamento entre modelos de domínio e entidades JPA/DTOs
- **springdoc-openapi** para documentação Swagger/OpenAPI
- **JUnit 5 + Testcontainers** para testes, com **JaCoCo** para cobertura (mínimo de 80% de instruções sobre `core` e `infrastructure/adapters`, verificado no `./gradlew check`)
- **Docker** (multi-stage build) e **docker-compose** para execução local
- **Terraform** (pasta `IaC/`) para provisionamento de EC2 e RDS na AWS, e um workflow de deploy no GitHub Actions (`.github/workflows/deploy.yml`), além de um workflow de CI que roda `./gradlew build` em cada PR (`.github/workflows/ci.yml`)

### Autorização por endpoint

| Método | Endpoint | Acesso |
|---|---|---|
| `POST` | `/vehicles` | `ADMIN` |
| `PUT` | `/vehicles/{id}` | `ADMIN` |
| `GET` | `/vehicles` | público |
| `POST` | `/vehicles/{id}/purchase` | `CUSTOMER` |
| `GET` | `/actuator/health` | público |
| `GET` | `/swagger`, `/swagger-ui/**`, `/v3/api-docs/**` | público |

> **Pendência conhecida**: a API espera um JWT emitido por um Keycloak com issuer configurável via `OAUTH_ISSUER_URI` (padrão `http://localhost:8081/realms/vehicle-resale`) e roles `ADMIN`/`CUSTOMER` no `realm_access.roles`. O repositório **não** inclui um serviço Keycloak no `docker-compose.yml` nem um realm exportado, então subir e configurar esse provedor (client, roles, usuários) ainda é uma etapa manual, fora do escopo automatizado do projeto.

## Como usar localmente

### Pré-requisitos

- Docker e Docker Compose
- JDK 25 (opcional, apenas se for rodar fora de container; o `gradlew` baixa o Gradle automaticamente)
- Um provedor OAuth2/JWT (ex.: Keycloak) configurado à parte, para testar os endpoints protegidos (ver pendência acima)

### Subindo com Docker Compose

```bash
docker compose up --build
```

Isso sobe dois serviços:

- `db`: PostgreSQL 17, exposto em `localhost:5432` (`vehicledb` / `vehicleuser` / `vehiclepass`)
- `app`: a API, exposta em `localhost:8080`

O Flyway aplica as migrations automaticamente na subida (`SPRING_JPA_HIBERNATE_DDL_AUTO=validate`, ou seja, o schema é gerenciado só pelo Flyway).

### Documentação da API

Com a aplicação no ar:

- Swagger UI: http://localhost:8080/swagger
- OpenAPI JSON: http://localhost:8080/v3/api-docs

### Chamando a API sem token (endpoints públicos)

```bash
curl http://localhost:8080/vehicles
```

### Chamando endpoints protegidos

Endpoints com `ADMIN`/`CUSTOMER` exigem um header `Authorization: Bearer <token>` com um JWT válido emitido pelo issuer configurado (`OAUTH_ISSUER_URI`), contendo a role correspondente:

```bash
curl -X POST http://localhost:8080/vehicles \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "brand": "Toyota",
    "model": "Corolla",
    "color": "Prata",
    "year": 2022,
    "description": "Único dono, revisões em dia",
    "price": 95000.00
  }'
```

### Rodando fora do Docker Compose

```bash
./gradlew bootRun
```

Nesse caso, aponte as variáveis de ambiente para um Postgres acessível (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`) e, se necessário, `OAUTH_ISSUER_URI`. No Windows, use `gradlew.bat bootRun`.

## Como testar

Os testes usam **Testcontainers** (sobem um Postgres real em container para os testes de integração), então é preciso ter o Docker rodando.

Rodar toda a suíte de testes:

```bash
./gradlew test
```

Rodar testes + verificação de cobertura (o que o CI executa em cada PR):

```bash
./gradlew build
```

No Windows: `gradlew.bat test` / `gradlew.bat build`.

O relatório de cobertura (JaCoCo) é gerado em `build/reports/jacoco/test/html/index.html`.

### Estrutura de testes

- `core/domain` e `core/application/services`: testes unitários (sem I/O), cobrindo regras de negócio e casos de uso.
- `infrastructure/adapters/inbound/web`: testes de controller (`@WebMvcTest`-style), cobrindo contrato HTTP, validação e mapeamento de exceções.
- `infrastructure/adapters/outbound/database`: testes de integração com Testcontainers, cobrindo os repositórios contra um Postgres real.
