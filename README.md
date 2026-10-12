# Sales Monolith

Aplicação backend em Java 17 e Spring Boot 3.3.4. O módulo Clientes expõe o CRUD em `/clientes` e usa PostgreSQL.

## Preparar o ambiente

1. Crie o arquivo `.env` a partir de `.env.example` e preencha as senhas. O `.env` é local e não deve ser enviado ao Git.
2. Inicie PostgreSQL e pgAdmin com `docker compose up -d`.
3. No pgAdmin, conecte-se ao servidor `postgres`, porta `5432`, usando o usuário e a senha definidos no `.env`. O pgAdmin está disponível em `http://localhost:5050`.
4. Abra o banco `sales_db` e execute o conteúdo de `database/schema.sql` somente se a tabela `clientes` ainda não existir.

O banco de desenvolvimento fica disponível para a aplicação em `localhost:5433`. A estrutura do banco é criada manualmente; o projeto não usa Flyway.

## Preparar o banco de teste

No Query Tool do pgAdmin, conectado ao banco `sales_db`, execute uma vez:

```sql
CREATE DATABASE sales_test_db OWNER sales_user;
```

Depois abra o Query Tool do novo banco `sales_test_db` e execute o conteúdo de `database/schema.sql`. Esse banco é separado de `sales_db` e deve ser usado somente pelos testes. Se o banco ou a tabela já existirem, não repita os comandos de criação.

No `.env`, configure `TEST_DB_URL` para `jdbc:postgresql://localhost:5433/sales_test_db`, `TEST_DB_USER` e `TEST_DB_PASSWORD`. A senha do teste pode ser a mesma de `DB_PASSWORD` quando ambos os bancos usam `sales_user`.

## Iniciar a aplicação e executar os testes

No terminal, na raiz do projeto, carregue as variáveis e execute os comandos:

```bash
set -a
source .env
set +a
./mvnw clean test
./mvnw spring-boot:run
```

Os testes de integração usam o perfil `test` e exigem as variáveis `TEST_DB_URL`, `TEST_DB_USER` e `TEST_DB_PASSWORD`. As alterações realizadas pelos testes são revertidas ao final de cada teste.

## API de Clientes

| Método | Rota | Resultado |
| --- | --- | --- |
| `GET` | `/clientes` | Lista paginada |
| `GET` | `/clientes/{id}` | Cliente por ID |
| `GET` | `/clientes/is-cadastrado/{id}` | `true` ou `false` |
| `POST` | `/clientes` | Cadastro, HTTP 201 |
| `PUT` | `/clientes/{id}` | Atualização, HTTP 200 |
| `DELETE` | `/clientes/{id}` | Exclusão, HTTP 204 |

O CPF não muda na atualização. Requisições inválidas retornam HTTP 400, cliente inexistente retorna HTTP 404 e chave única duplicada retorna HTTP 409.
