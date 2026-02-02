## Projeto Desenvolvedor Back End
Processo Seletivo Nº 001/2026 SEPLAG

Autor: Allan Vitor Walecheski Toledo

### Padrões de projeto
* Todo o código será escrito em inglês.
* Documentação será escrita em português.

### Objetivo do projeto
O requisito do projeto não define seu propósito. Eu assumo que seja para criar uma biblioteca 
pública de informações sobre artistas e bandas. Toda decisão feita, será com base nesse objetivo.

### Arquitetura
Os requisitos do projeto especificam que o sistema deverá disponibilizar dados sobre artistas e álbuns com 
relacionamento N:N. Portanto, para satisfazer o requerimento, foi elaborado o Modelo Entidade-Relacionamento abaixo.

![Modelo Entidade Relacionamento](/doc/modelo_entidade_relacionamento.png)

A partir desse modelo, foi criado o seguinte diagrama lógico:

![Diagrama Lógico](/doc/diagrama_logico.png)

O requisito do projeto não detalhada as regras de acesso. Portanto, definirei que:
* Usuários comuns tem permissão de GET
* Administradores tem permissão para GET, POST e PUT
* Administradores podem alterar qualquer Artista/Álbum
* Qualquer usuário pode se cadastrar e obter um token JWT
* Administradores podem elevar ou reduzir permissões de usuários

Para satisfazer o requisito de segurança, foi elaborado o seguinte diagrama lógico:

![Diagrama Lógico Segurança](/doc/diagrama_logico_seguranca.png)

_O nome 'app_user' foi usado, pois no Postgres 'user' é uma palavra reservada._

### Rotas

As rotas seguem boas práticas definidas pela arquitetura REST e pela comunidade.

`POST /users` -> Cria um novo usuário \
`GET  /recovery?email=` -> Solicita um link de recuperação que será enviado ao email \
`PUT  /recovery?token=` -> Atualiza a senha por link autoassinado \
`GET  /token` -> Solicita um token novo \
`PUT  /users/{id}` -> Atualiza (parcial ou integralmente) um usuário existente \
`GET  /users/{id}` -> Acessa um usuário \
`GET  /users/me` -> Acessa o próprio usuário

`POST /artists` -> Cria um novo artista \
`PUT  /artists/{id}` -> Atualiza (parcial ou integralmente) um artista existente \
`GET  /artists` -> Retorna uma coleção paginada de artistas \
`GET  /artists/{id}` -> Retorna um artista

`POST /albums` -> Cria um novo álbum \
`PUT  /albums/{id}` -> Atualiza (parcial ou integralmente) um álbum \
`GET  /albums` -> Retorna uma coleção paginada de álbuns \
`GET  /albuns/{id}` -> Retorna um álbum

### Parâmetros nas rotas

Os parâmetros são opcionais e se usados são aplicados através do conectivo lógico conjunção (AND)

`GET /albums` Aceita os seguintes parametros:
* `artistNameLike`
* `artistIdEqual`
* `albumNameLike`
* `albumYearEqual`
* `albumYearBefore`
* `albumYearAfter`

`GET /artists` Aceita os seguintes parametros:
* `artistNameLike`
* `albumNameLike`
* `albumYearEqual`
* `albumYearBefore`
* `albumYearAfter`

`GET /users` Aceita os seguintes parametros:
* `nameLike`
* `emailLike`
* `isAdminEqual`

### Paginação
A paginação é composta por dois parâmetros especiais nas rotas de coleções:
* `offset` (padrão: 0)
* `limit` (padrão: 100)

_Paginação é obrigatória._


### Segurança
A aplicação possui 3 escopos de acesso.
* `ACCESS_COLLECTION` -> Permite realizar consultas em coleções de dados.
* `EDIT_COLLECTION` -> Permite editar coleções de dados (Criar, Atualizar, Deletar).
* `MANAGER_USERS` -> Permite modificações e acesso a outros usuários.

Ao gerar o token de acesso, a aplicação inclui no token os escopos conforme o perfil do usuário.

Para gerar o primeiro token, é possível através do cabeçalho de autenticação usando o esquema Basic.
Para usar o token bastar usar o mesmo cabeçalho, porém trocando para o esquema Bearer.

### Observações sobre a arquitetura desenvolvida
Nenhuma operação de segurança é realizada após a validação do acesso do usuário ao recurso.
Desta forma, o cache pode ser feita de maneira simples e eficiente: se um determinado 
token possuí acesso ao recurso, qualquer resposta já armazenada em cache é segura de ser compartilhada.\
Claro, com exceção da rota "/users/me", que **não deve ser armazenada na cache.**


### Checklist dos requisitos

#### Pré-requisitos:
- [x] a) Leia todo o documento antes de iniciar.
- [x] b) Java (Spring Boot ou Quarkus).
#### Requisitos Gerais:
- [x] a) Segurança: bloquear acesso ao endpoint a partir de domínios fora do domínio do serviço.
- [x] b) Autenticação JWT com expiração a cada 5 minutos e possibilidade de renovação.
- [x] c) Implementar POST, PUT, GET.
- [x] d) Paginação na consulta dos álbuns.
- [x] e) Expor quais álbuns são/tem cantores e/ou bandas (consultas parametrizadas).
- [x] f) Consultas por nome do artista com ordenação alfabética (asc/desc).
- [x] g) Upload de uma ou mais imagens de capa do álbum.
- [x] h) Armazenamento das imagens no MinIO (API S3).
- [x] i) Recuperação por links pré-assinados com expiração de 30 minutos.
- [x] j) Versionar endpoints.
- [x] k) Flyway Migrations para criar e popular tabelas.
- [x] l) Documentar endpoints com OpenAPI/Swagger.
#### Requisitos apenas para Sênior:
- [x] a) Health Checks e Liveness/Readiness.
- [x] b) Testes unitários.
- [x] c) WebSocket para notificar o front a cada novo álbum cadastrado.
- [x] d) Rate limit: até 10 requisições por minuto por usuário.
- [ ] e) Endpoint de regionais (https://integrador-argus-api.geia.vip/v1/regionais):
- - [ ] i) Importar a lista para tabela interna;
- - [ ] ii) Adicionar atributo “ativo” (regional (id integer, nome varchar(200), ativo boolean));
- - [ ] iii) Sincronizar com menor complexidade:
- - - [ ] 1\) Novo no endpoint → inserir;
- - - [ ] 2\) Ausente no endpoint → inativar;
- - - [ ] 3\) Atributo alterado → inativar antigo e criar novo registro.