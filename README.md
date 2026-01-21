## Projeto Desenvolvedor Back End
Processo Seletivo Nº 001/2026 SEPLAG

Autor: Allan Vitor Walecheski Toledo

### Padrões de projeto
* Todo o código será escrito em inglês.
* Documentação será escrita em português.

### Objetivo do projeto
O requisito do projeto não define seu propósito. Eu assumo que seja para criar uma biblioteca pública de informações sobre artistas 
e bandas. Toda decisão feita, será com base nesse objetivo.

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

_O nome 'system_user' foi usado, pois no Postgres 'user' é uma palavra reservada._

