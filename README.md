# Sales Monolith

Reconstrução do [Sales-Microservices](https://github.com/Renaneliziario/Sales-Microservices)
como monólito modular em Java 17, preservando os contratos HTTP e as regras de
negócio do original.

O objetivo não é entregar outro CRUD, e sim responder uma pergunta de engenharia:
o que de fato se ganha e o que se perde ao escolher microsserviços num sistema
deste tamanho. Para a resposta valer, as duas versões precisam expor os mesmos
endpoints e rodar no mesmo ambiente, com o mesmo Dockerfile, sob a mesma carga.
O projeto original permanece intacto, porque é a referência da comparação.

## Arquitetura

Organização por domínio, não por camada técnica.

```
br.com.renan.sales
├── shared/exception        ApiError e RestExceptionHandler, comuns aos módulos
├── clientes                controller, dto, entity, repository, service
├── produtos                controller, dto, entity, repository, service
└── vendas                  controller, dto, entity, repository, service
```

Clientes e Produtos não dependem de ninguém. Vendas depende dos dois, por
contratos internos explícitos (`BuscaCliente`, `BuscaProduto`, `GestaoEstoque`).
As fronteiras são as mesmas que existiam entre os microsserviços, sem a rede no
meio.

Responsabilidade das camadas:

| Camada | Responsabilidade |
| --- | --- |
| Controller | borda HTTP: rota, formato de entrada e saída, código de status |
| Service | caso de uso, orquestração e limite da transação |
| Entidade | regras que valem sempre para o próprio dado |
| Repository | acesso ao banco, via Spring Data JPA |

## Stack

Java 17 com Records para DTOs, Spring Boot 3.3.4, Spring Web, Spring Data JPA,
Bean Validation, PostgreSQL 16 em Docker, Maven, JUnit 5, Mockito e MockMvc.

Duas escolhas deliberadas que divergem do original: não uso Lombok, os
construtores e getters são escritos à mão para controlar quais campos ficam
acessíveis; e não uso Flyway, o schema é SQL manual em `database/schema.sql`.

## Preparar o ambiente

1. Crie o `.env` a partir de `.env.example` e preencha as senhas. O `.env` é
   local e não vai para o Git.
2. Suba PostgreSQL e pgAdmin com `docker compose up -d`. O banco fica em
   `localhost:5433` e o pgAdmin em `http://localhost:5050`.
3. Crie o banco de teste, uma vez só, conectado ao `sales_db`:

```sql
CREATE DATABASE sales_test_db OWNER sales_user;
```

4. Aplique `database/schema.sql` nos dois bancos, `sales_db` e `sales_test_db`.
   São quatro tabelas: `clientes`, `produtos`, `vendas` e `itens_venda`.

Pelo terminal, sem pgAdmin:

```bash
for db in sales_db sales_test_db; do
  docker exec -i sales-monolith-db psql -U sales_user -d $db < database/schema.sql
done
```

Como o `ddl-auto` é `validate` em todos os perfis, o Hibernate nunca cria
tabela: ele apenas confere se o mapeamento bate com o schema. Isso transforma o
teste de carregamento do contexto numa verificação real de consistência entre
código e banco.

## Perfis

O perfil ativo vem da variável `APP_PROFILE`, e **o padrão é `test`**:

```properties
spring.profiles.active=${APP_PROFILE:test}
```

| Perfil | Banco | ddl-auto |
| --- | --- | --- |
| `dev` | `sales_db` em `localhost:5433` | validate |
| `test` | `sales_test_db` em `localhost:5433` | validate |
| `prod` | variáveis de ambiente | none |

Subir a aplicação sem definir `APP_PROFILE` aponta para o banco de teste. Para
desenvolvimento, use `APP_PROFILE=dev`.

## Executar

```bash
set -a
source .env
set +a

./mvnw clean test
APP_PROFILE=dev ./mvnw spring-boot:run
```

Os testes de integração usam o perfil `test` e revertem as alterações ao final
de cada teste.

## Executar em container

```bash
docker build -t sales-monolith:1.0 .

docker run --rm --name sales-app --network host \
  --memory=1g --memory-swap=1g \
  --cpuset-cpus="0,1" \
  -e APP_PROFILE=dev \
  -e DB_PASSWORD="$DB_PASSWORD" \
  -e JAVA_OPTS="-XX:+UseSerialGC -Xms512m -Xmx512m" \
  sales-monolith:1.0
```

O `Dockerfile` é multi-stage: compila com Maven e entrega só o JRE e o jar.
`JAVA_OPTS` permite ajustar heap e coletor de lixo nos ensaios de carga sem
reconstruir a imagem.

Os limites existem para o ensaio de desempenho. `--memory-swap` igual a
`--memory` desliga o swap do container, sem o que a medição refletiria disco em
vez de arquitetura. `--cpuset-cpus` prende o processo aos mesmos núcleos
físicos que a versão em microsserviços vai usar.

## API

Os caminhos são herdados do original, inclusive as inconsistências: `/clientes`
no plural contra `/produto` e `/venda` no singular, `is-cadastrado` em
kebab-case contra `isCadastrado` em camelCase. Corrigir isso impediria reutilizar
os mesmos scripts de carga nas duas arquiteturas, então ficaram documentadas e
não corrigidas.

### Clientes

| Método | Rota | Resultado |
| --- | --- | --- |
| `GET` | `/clientes` | Lista paginada |
| `GET` | `/clientes/{id}` | Cliente por ID |
| `GET` | `/clientes/is-cadastrado/{id}` | `true` ou `false` |
| `POST` | `/clientes` | Cadastro, HTTP 201 |
| `PUT` | `/clientes/{id}` | Atualização, HTTP 200 |
| `DELETE` | `/clientes/{id}` | Exclusão, HTTP 204 |

O CPF não muda na atualização.

### Produtos

| Método | Rota | Resultado |
| --- | --- | --- |
| `GET` | `/produto` | Lista paginada |
| `GET` | `/produto/id/{id}` | Produto por ID |
| `GET` | `/produto/{codigo}` | Produto por código |
| `GET` | `/produto/isCadastrado/{id}` | `true` ou `false` |
| `POST` | `/produto` | Cadastro, HTTP 201 |
| `PUT` | `/produto` | Atualização, ID no corpo, HTTP 200 |
| `DELETE` | `/produto/{id}` | Exclusão, HTTP 204 |
| `POST` | `/produto/{codigo}/estoque/baixa` | Baixa de estoque, corpo é um número |
| `POST` | `/produto/{codigo}/estoque/reposicao` | Reposição, corpo é um número |

O estoque só muda pelos métodos da entidade, que rejeitam quantidade nula, zero
ou negativa, e recusam baixa acima do saldo disponível.

### Vendas

| Método | Rota | Resultado |
| --- | --- | --- |
| `GET` | `/venda` | Lista paginada |
| `GET` | `/venda/{id}` | Venda por ID |
| `POST` | `/venda` | Criação, HTTP 200 |
| `PUT` | `/venda/{id}/{codigoProduto}/{quantidade}/addProduto` | Adiciona item |
| `PUT` | `/venda/{id}/finalizar` | Conclui a venda |
| `PUT` | `/venda/{id}/cancelar` | Cancela e devolve o estoque |

O `POST /venda` responde 200, e não 201, igual ao original.

## O fluxo de criação de uma venda

É o caso de uso central e onde está a diferença entre as duas arquiteturas. Tudo
roda dentro de uma transação:

1. valida o formato do pedido, erro gera 400 com os erros por campo
2. confere se o cliente existe, erro gera 404
3. cria a venda com status `INICIADA`, data atual e total zero
4. para cada item: busca o produto por código, grava no item o nome e o preço
   daquele momento, recalcula o total e baixa o estoque
5. grava a venda e os itens juntos, por cascade

Qualquer exceção reverte tudo. Se o terceiro item não tiver estoque, o estoque
dos dois primeiros não é consumido.

No original esse mesmo fluxo é uma saga manual: o estoque é baixado por chamadas
HTTP item a item e, quando algo falha no meio, um bloco de compensação tenta
devolver o que já foi consumido. Se a compensação também falhar, o estoque fica
divergente de forma permanente. No monólito esse código deixou de ser
necessário, e é o ganho concreto da arquitetura.

Nome e preço do produto são copiados para o item, não referenciados. Alterar o
preço do produto depois não altera o histórico da venda.

## Modelo de dados

```
clientes      id, nome, cpf (único), tel, email (único), endereco, numero,
              cidade, estado

produtos      id, codigo (único), nome, descricao, valor NUMERIC(10,2),
              quantidade INTEGER CHECK (>= 0)

vendas        id, codigo (único), cliente_id FK para clientes(id),
              endereco_entrega, valor_total NUMERIC(10,2),
              data_venda TIMESTAMP WITH TIME ZONE, status

itens_venda   id, venda_id FK para vendas(id) ON DELETE CASCADE, produto_id,
              codigo_produto, nome, quantidade, valor_unitario
```

Duas decisões de modelagem. `vendas.cliente_id` tem chave estrangeira real, o
que é impossível na versão em microsserviços porque os dados vivem em bancos
separados. Já `itens_venda.produto_id` é um `BIGINT` sem chave estrangeira de
propósito, para o retrato do produto sobreviver à exclusão dele.

## Tratamento de erros

Um único `RestExceptionHandler` atende os três módulos, com corpo padronizado no
record `ApiError`, que tem `status`, `message`, `timestamp` e um mapa opcional
`errors`.

| Exceção | Status |
| --- | --- |
| `MethodArgumentNotValidException` | 400, com erros por campo |
| `IllegalArgumentException` | 400 |
| `EntityNotFoundException` | 404 |
| `IllegalStateException` | 409 |
| `DataIntegrityViolationException` com SQLState 23505 | 409 |
| `DataIntegrityViolationException`, outros casos | 500 |
| `Exception` | 500, com log |

Na versão em microsserviços cada serviço tem seu próprio handler, escritos em
momentos diferentes, com três formatos de erro distintos e coberturas
diferentes.

## Desvios conscientes em relação ao original

Todos preservam o contrato HTTP de sucesso e alteram apenas comportamento
defeituoso.

| Situação | Original | Monólito |
| --- | --- | --- |
| CPF, email ou código duplicado | 500 | 409 |
| Conflito de estado da venda | 503 | 409 |
| Finalizar venda já concluída | 200, sem validar | 409 |
| Cancelar venda já cancelada | 200, estornava de novo | 409 |
| Falha no estorno ao cancelar | engolida, seguia | propaga e faz rollback |
| Baixa de estoque com valor negativo | aumentava o estoque | 400 |
| Reposição com valor negativo | baixava sem validar saldo | 400 |
| Entidade exposta na API em Produtos e Vendas | sim | DTOs com os mesmos campos |
| Regra de estoque | nas services, com setters abertos | encapsulada na entidade |

Não corrigidos de propósito, porque são objeto de medição na comparação: a
condição de corrida na baixa de estoque, presente nas duas arquiteturas, e as
inconsistências de nomenclatura dos caminhos.

## Testes

```bash
./mvnw test
```

Estado atual: 14 testes, nenhuma falha.

| Teste | Tipo | Quantidade |
| --- | --- | --- |
| `SalesMonolithApplicationTests` | contexto e validação do schema | 1 |
| `CadastroClienteTest` | unitário com Mockito | 1 |
| `ClienteControllerIntegrationTest` | integração com MockMvc | 4 |
| `ProdutoTest` | unitário da entidade, sem Spring | 8 |

Lacunas conhecidas: o módulo Vendas ainda não tem testes próprios, e é o mais
complexo dos três. O que está verificado hoje é o mapeamento das entidades de
venda, pelo `validate` do Hibernate, não o comportamento. Faltam também o teste
de integração de Produtos e o teste do rollback do fluxo de venda, que é o
comportamento mais relevante a provar.

## Comparação entre as arquiteturas

Etapa seguinte do projeto, ainda não executada.

Plano: k6 gerando carga de taxa constante, Actuator e Micrometer expondo
métricas, e comparação de P50, P95, P99, throughput, taxa de erro, CPU, memória,
pausas de GC e comportamento do banco.

Medições já coletadas do monólito, em container limitado a 1 GB e dois núcleos:

| Métrica | Valor |
| --- | --- |
| Processos | 1 |
| RSS em repouso | 315 MB |
| Tempo de inicialização | 2,76 s |

Regras definidas para o ensaio ser justo: mesmos contratos HTTP, mesmo
Dockerfile, mesmo orçamento total de memória somando todos os processos de cada
lado, mesmo coletor de lixo fixado explicitamente, swap desligado, gerador de
carga fora dos núcleos medidos, e um lado por vez.

Um fator a equalizar já identificado: o `ClienteService` original carrega Spring
Security e os três serviços expõem Swagger, o que este monólito não tem. Parte
de qualquer diferença de latência viria daí, e não da arquitetura.
