## Projeto Desenvolvedor Back End
Processo Seletivo Nº 001/2026 SEPLAG

Autor: Allan Vitor Walecheski Toledo
Projeto: Backend

### Como executar o projeto
Para executar o projeto, é necessário ter o Docker e o Docker Compose instalado.  
Na pasta raiz do projeto execute o comando:  
**Mac OS**
```bash
docker compose up -d --build
```
**Linux**
```bash
sudo docker-compose up -d --build
```
Isso causará a compilação do projeto e a orquestração dos demais containers para funcionar em conjunto.

A porta 8080 ficará exposta para requisições da API.
É possível também utilizar a ferramenta [Bruno](https://www.usebruno.com/) para testar os endpoints.
As configurações de requisição estão na pasta `./dev/bruno`.

### Padrões de projeto
* Todo o código será escrito em inglês.
* Documentação será escrita em português.

### Objetivo do projeto
O requisito do projeto não define seu propósito. Eu assumo que seja para criar uma biblioteca 
pública de informações sobre artistas e bandas. Toda decisão feita, será com base nesse objetivo.

### Arquitetura
O projeto utiliza arquitetura em camadas seguindo a estrutura de um projeto Spring.
Na camada mais próxima ao banco estão as classes de repositório, são responsáveis por realizar consultas e o mapeamento 
entre as tabelas e as classes em java. A camada de serviços aplica as regras essênciais para o funcionamento da 
aplicação. Na camada de controle é onde recebe a requisição e constrói a resposta à solicitação.

#### Estrutura das pastas
```
|_ dev ....: configuração do ambiente para desenvolvimento.  
|_ doc ....: arquivos para documentação extra.  
|_ src   
    |_ main.java.br.dev.allantoledo.psc  
        |_ components .......: Classes que definem Beans de uso geral.  
        |_ configuration ....: Classes que aplicam configurações à aplicação.  
        |_ controller .......: Classes de controle, recebem e respondem a requisições.  
        |_ dto ..............: Classes de transferência de dados.  
        |_ entity ...........: Classes que mapeiam o banco em objetos.  
        |_ repository .......: Classes que realizam consultas no banco.  
        |_ service ..........: Classes da camada de serviço.  
        |_ util .............: Classes utilitárias de uso geral.  
    |_ main.resources .......: Recursos da aplicação  
        |_ db.migration.postgres ...: Arquivos SQL de migração do banco de dados.  
    |_ test.java.br.dev.allantoledo.psc ..: Classes de testes unitários.  
```

Os requisitos do projeto especificam que o sistema deverá disponibilizar dados sobre artistas e álbuns com 
relacionamento N:N. Portanto, para satisfazer o requerimento, foi elaborado o Modelo Entidade-Relacionamento abaixo.

![Modelo Entidade Relacionamento](/doc/modelo_entidade_relacionamento.png)

A partir desse modelo, foi criado o seguinte diagrama lógico:

![Diagrama Lógico](/doc/diagrama_logico.png)

Para satisfazer o requisito de segurança, foi elaborado o seguinte diagrama lógico:

![Diagrama Lógico Segurança](/doc/diagrama_logico_seguranca.png)

_O nome 'app_user' foi usado, pois no Postgres 'user' é uma palavra reservada._

### Rotas

As rotas seguem boas práticas definidas pela arquitetura REST e pela comunidade.
As rotas estão documentadas através do Swagger no endereço: http://localhost:8080/api/docs/

### Paginação
A paginação é composta por dois parâmetros especiais nas rotas de coleções:
* `offset` (padrão: 0)
* `limit` (padrão: 100)

_Paginação é obrigatória._

### Segurança
O requisito do projeto não detalhada as regras de acesso. Portanto, definirei que:
* Usuários comuns tem permissão de GET
* Administradores tem permissão para GET, POST e PUT
* Administradores podem alterar qualquer Artista/Álbum
* Qualquer usuário pode se cadastrar e obter um token JWT
* Administradores alterar as permissões de usuários

Portanto, a aplicação possui 3 escopos de acesso.
* `ACCESS_COLLECTION` -> Permite realizar consultas em coleções de dados.
* `EDIT_COLLECTION` -> Permite editar coleções de dados (Criar, Atualizar, Deletar).
* `MANAGER_USERS` -> Permite modificações e acesso a outros usuários.

Ao gerar o token de acesso, a aplicação inclui no token os escopos conforme o perfil do usuário.

Para gerar o primeiro token, é possível através do cabeçalho de autenticação usando o esquema Basic.
Para usar o token bastar usar o mesmo cabeçalho, porém trocando para o esquema Bearer.

### Websockets
A aplicação fornece comunicação bidirecional por websockets utilizando protocolo STOMP.
Para iniciar a comunicação, basta conectar-se através do endpoint `/api/ws`. E a inscrição para notificações de novos álbuns ocorre por meio do canal `/notifications/albums`.

### Observações sobre a arquitetura desenvolvida
Nenhuma operação de segurança é realizada após a validação do acesso do usuário ao recurso.
Desta forma, o cache pode ser feita de maneira simples e eficiente: se um determinado 
token possuí acesso ao recurso, qualquer resposta já armazenada em cache é segura de ser compartilhada.\
Claro, com exceção de rotas de uso pessoal como, por exemplo, "/users/me".


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
- [x] e) Endpoint de regionais (https://integrador-argus-api.geia.vip/v1/regionais):
- - [x] i) Importar a lista para tabela interna;
- - [x] ii) Adicionar atributo “ativo” (regional (id integer, nome varchar(200), ativo boolean));
- - [x] iii) Sincronizar com menor complexidade:
- - - [x] 1\) Novo no endpoint → inserir;
- - - [x] 2\) Ausente no endpoint → inativar;
- - - [x] 3\) Atributo alterado → inativar antigo e criar novo registro.
