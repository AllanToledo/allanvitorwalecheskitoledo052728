package br.dev.allantoledo.psc.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Biblioteca online de Artistas e Álbuns")
                        .summary("Desenvolvido por Allan Vitor W. Toledo")
                        .description("""
                                A API fornece dados de artistas e seus álbuns.
                                Cada artista pode ter vários álbuns, e cada álbum pode possui vários autores (artistas).
                                
                                
                                Para consultar os dados, basta criar uma conta na aplicação e solicitar um token.
                                Os tokens possuem validade de 5 minutos e podem ser usados gerar novos tokens.
                                
                                
                                Os tokens de usuários administradores permitem criar e atualizar álbuns, como
                                também modificar outros usuários.
                                
                                
                                Todas as rotas que apontam para coleções são obrigatóriamente paginadas.
                                O limite por página é no máximo 100 itens.
                                
                                
                                As rotas de PUT funcionam no formato PATCH, portanto, propriedades com valor 'undefined'
                                são ignoradas.
                                """)
                        .version("v1.0")
                );
    }
}
