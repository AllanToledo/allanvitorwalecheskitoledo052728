package br.dev.allantoledo.psc;

import br.dev.allantoledo.psc.dto.token.TokenInformation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.client.RestClient;


import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
@SpringBootTest(webEnvironment = DEFINED_PORT)
@TestPropertySource(properties = {
        "server.port=8081",
        "security.allowedOrigins=http://localhost:8081",
        "security.rateLimit=disabled"
})
public class ApplicationTests {

    @Value("${security.allowedOrigins}")
    private String origin;

    private RestTestClient getTestClient() {
        return RestTestClient.bindToServer().baseUrl(origin).build();
    }
    private String getTokenForUser(String username, String password) {
        RestClient client = RestClient.builder().baseUrl(origin).build();
        byte[] bytes = String.format("%s:%s", username, password).getBytes();
        String encoded = Base64.getEncoder().encodeToString(bytes);
        TokenInformation tokenInformation =  client
            .get()
            .uri("/api/v1/token")
            .headers(headers -> {
                headers.add("Authorization", "Basic " + encoded);
            })
            .retrieve()
            .body(TokenInformation.class);

        return tokenInformation.getToken();
    }

    @Test
    void getTokenUsingPassword() {
        RestTestClient client = getTestClient();
        String encoded = Base64.getEncoder().encodeToString("admin@admin.br:123456".getBytes());

        client
            .get()
            .uri("/api/v1/token")
            .headers(headers -> {
                headers.add("Authorization", "Basic " + encoded);
            })
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.user").isEqualTo("admin@admin.br")
                .jsonPath("$.role")
                    .isEqualTo("EDIT_COLLECTION ACCESS_COLLECTION MANAGER_USERS")
                .jsonPath("$.token").isNotEmpty();
    }

    @Test
    void getTokenUsingPreviousToken() {
        RestTestClient client = getTestClient();

        String token = getTokenForUser("admin@admin.br", "123456");

        client
            .get()
            .uri("/api/v1/token")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + token);
            })
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.user").isEqualTo("admin@admin.br")
                .jsonPath("$.role")
                    .isEqualTo("EDIT_COLLECTION ACCESS_COLLECTION MANAGER_USERS")
                .jsonPath("$.token").isNotEmpty();
    }

    @Test
    void createNewUser() {
        RestTestClient client = getTestClient();

        String newUserName = "fulano";
        String newUserEmail = String.format("fulano%d@email.com", new Random().nextInt());
        String newUserPassword = "fulano123";
        String body = """
            {
                "name": "%s",
                "email": "%s",
                "password": "%s"
            }
        """.formatted(newUserName, newUserEmail, newUserPassword);

        client
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo(newUserName)
                .jsonPath("$.email").isEqualTo(newUserEmail)
                .jsonPath("$.password").doesNotExist()
                .jsonPath("$.isAdmin").isEqualTo(false);

    }

    @Test
    void getSelfInformation() {
        RestTestClient client = getTestClient();
        String encoded = Base64.getEncoder().encodeToString("admin@admin.br:123456".getBytes());

        client
            .get()
            .uri("/api/v1/users/me")
            .headers(headers -> {
                headers.add("Authorization", "Basic " + encoded);
            })
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.email").isEqualTo("admin@admin.br")
                .jsonPath("$.name").isEqualTo("admin")
                .jsonPath("$.isAdmin").isEqualTo(true);
    }

    @Test
    void updateSelfInformation() {
        RestTestClient client = getTestClient();
        String encoded = Base64.getEncoder().encodeToString("admin@admin.br:123456".getBytes());

        String oldName = "admin";
        String newName = "teste";
        client
            .put()
            .uri("/api/v1/users/me")
            .headers(headers -> {
                headers.add("Authorization", "Basic " + encoded);
            })
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
                {
                    "name": "%s"
                }
            """.formatted(newName))
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.email").isEqualTo("admin@admin.br")
                .jsonPath("$.name").isEqualTo(newName)
                .jsonPath("$.isAdmin").isEqualTo(true);

        client
            .put()
            .uri("/api/v1/users/me")
            .headers(headers -> {
                headers.add("Authorization", "Basic " + encoded);
            })
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
                {
                    "name": "%s"
                }
            """.formatted(oldName))
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.email").isEqualTo("admin@admin.br")
                .jsonPath("$.name").isEqualTo(oldName)
                .jsonPath("$.isAdmin").isEqualTo(true);
    }

    @Test
    void createUserAndUpdateByAdmin() {
        RestTestClient client = getTestClient();
        String adminToken = getTokenForUser("admin@admin.br", "123456");
        String newUserName = "Fulano Admin";
        String newUserEmail = String.format("fulano%d@admin.br", new Random().nextInt());
        String newUserPassword = "fulano123";

        String body = """
            {
                "name": "%s",
                "email": "%s",
                "password": "%s"
            }
        """.formatted(newUserName, newUserEmail, newUserPassword);

        AtomicReference<String> newUserId = new AtomicReference<>();
        client
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.id").value(id -> newUserId.set((String) id))
                .jsonPath("$.name").isEqualTo(newUserName)
                .jsonPath("$.email").isEqualTo(newUserEmail)
                .jsonPath("$.password").doesNotExist()
                .jsonPath("$.isAdmin").isEqualTo(false);

        client
            .put()
            .uri(String.format("/api/v1/users/%s", newUserId.get()))
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + adminToken);
            })
            .contentType(MediaType.APPLICATION_JSON)
            .body("{ \"isAdmin\": true }")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.id").isEqualTo(newUserId.get())
                .jsonPath("$.name").isEqualTo(newUserName)
                .jsonPath("$.email").isEqualTo(newUserEmail)
                .jsonPath("$.isAdmin").isEqualTo(true);
    }

    @Test
    void listUsersWithAdminAccess() {
        RestTestClient client = getTestClient();
        String token = getTokenForUser("admin@admin.br", "123456");
        client
            .get()
            .uri("/api/v1/users")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + token);
            })
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void listUsersWithUserAccess() {
        RestTestClient client = getTestClient();
        String token = getTokenForUser("teste@teste.br", "123456");
        client
            .get()
            .uri("/api/v1/users")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + token);
            })
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    void listArtistsWithAnonymousAccess() {
        RestTestClient client = getTestClient();
        client
            .get()
            .uri("/api/v1/artists")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void listArtistsWithUserAccess() {
        RestTestClient client = getTestClient();
        String token = getTokenForUser("teste@teste.br", "123456");
        client
            .get()
            .uri("/api/v1/artists")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + token);
            })
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void listAlbumsWithAnonymousAccess() {
        RestTestClient client = getTestClient();
        client
            .get()
            .uri("/api/v1/albums")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void listAlbumsWithUserAccess() {
        RestTestClient client = getTestClient();
        String token = getTokenForUser("teste@teste.br", "123456");
        client
            .get()
            .uri("/api/v1/albums")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + token);
            })
            .exchange()
            .expectStatus().isOk();
    }


    @Test
    void getArtistByIdWithUserAccess() {
        RestTestClient client = getTestClient();
        String token = getTokenForUser("teste@teste.br", "123456");

        client
            .get()
            .uri("/api/v1/artists?limit=1")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + token);
            })
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.artists[0].id").value(artistId -> {
                client
                    .get()
                    .uri("/api/v1/artists/" + artistId)
                    .headers(headers -> {
                        headers.add("Authorization", "Bearer " + token);
                    })
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(artistId)
                    .jsonPath("$.name").isNotEmpty()
                    .jsonPath("$.albums").exists();
            });
    }

    @Test
    void createAndUpdateArtistWithAdminAccess() {
        RestTestClient client = getTestClient();
        String adminToken = getTokenForUser("admin@admin.br", "123456");

        String artistName = "Novo Artista Teste";
        String updatedArtistName = "Artista Teste Atualizado";

        AtomicReference<String> artistId = new AtomicReference<>();

        client
            .post()
            .uri("/api/v1/artists")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + adminToken);
            })
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
            {
                "name": "%s"
            }
            """.formatted(artistName))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").value(id -> artistId.set((String) id))
            .jsonPath("$.name").isEqualTo(artistName)
            .jsonPath("$.albums").isArray();

        client
            .put()
            .uri("/api/v1/artists/" + artistId.get())
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + adminToken);
            })
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
            {
                "name": "%s"
            }
            """.formatted(updatedArtistName))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.id").isEqualTo(artistId.get())
                .jsonPath("$.name").isEqualTo(updatedArtistName);
    }

    @Test
    void createAlbumWithMultipleArtists() {
        RestTestClient client = getTestClient();
        String adminToken = getTokenForUser("admin@admin.br", "123456");

        String[] artistIds = new String[2];
        String[] artistNames = {"Artista 1 Album", "Artista 2 Album"};

        for (int i = 0; i < 2; i++) {
            int finalI = i;
            client
                .post()
                .uri("/api/v1/artists")
                .headers(headers -> {
                    headers.add("Authorization", "Bearer " + adminToken);
                })
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                {
                    "name": "%s"
                }
                """.formatted(artistNames[i]))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.id").value(id -> artistIds[finalI] = (String) id);
        }

        String albumName = "Álbum Colaborativo";
        int albumYear = 2024;

        client
            .post()
            .uri("/api/v1/albums")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + adminToken);
            })
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
            {
                "name": "%s",
                "year": %d,
                "authors": [
                    {"id": "%s"},
                    {"id": "%s"}
                ]
            }
            """.formatted(albumName, albumYear, artistIds[0], artistIds[1]))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo(albumName)
                .jsonPath("$.year").isEqualTo(albumYear)
                .jsonPath("$.authors").isArray()
                .jsonPath("$.authors.length()").isEqualTo(2)
                .jsonPath("$.covers").isArray();
    }

    @Test
    void filterArtistsByName() {
        RestTestClient client = getTestClient();
        String token = getTokenForUser("teste@teste.br", "123456");

        String searchTerm = "test";

        client
            .get()
            .uri("/api/v1/artists?artistNameLike=" + searchTerm + "&limit=10")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + token);
            })
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.artists").isArray();
    }

    @Test
    void filterAlbumsByYearRange() {
        RestTestClient client = getTestClient();
        String token = getTokenForUser("teste@teste.br", "123456");

        int yearAfter = 2000;
        int yearBefore = 2010;

        client
            .get()
            .uri("/api/v1/albums?albumYearAfter=" + yearAfter + "&albumYearBefore=" + yearBefore)
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + token);
            })
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.albums").isArray();
    }

    @Test
    void getAlbumByIdWithDetails() {
        RestTestClient client = getTestClient();
        String token = getTokenForUser("teste@teste.br", "123456");

        // Primeiro lista os álbuns para obter um ID válido
        client
            .get()
            .uri("/api/v1/albums?limit=1")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + token);
            })
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.albums[0].id").value(albumId -> {
                client
                .get()
                .uri("/api/v1/albums/" + albumId)
                .headers(headers -> {
                    headers.add("Authorization", "Bearer " + token);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.id").isEqualTo(albumId)
                    .jsonPath("$.name").isNotEmpty()
                    .jsonPath("$.year").isNumber()
                    .jsonPath("$.authors").isArray()
                    .jsonPath("$.covers").isArray();
            });
    }

    @Test
    void updateAlbumInformation() {
        RestTestClient client = getTestClient();
        String adminToken = getTokenForUser("admin@admin.br", "123456");

        AtomicReference<String> artistId = new AtomicReference<>();
        client
            .post()
            .uri("/api/v1/artists")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + adminToken);
            })
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
            {
                "name": "Artista para Album Update"
            }
            """)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.id").value(id -> artistId.set((String) id));

        AtomicReference<String> albumId = new AtomicReference<>();
        String initialAlbumName = "Álbum Inicial";
        int initialYear = 2020;

        client
            .post()
            .uri("/api/v1/albums")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + adminToken);
            })
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
            {
                "name": "%s",
                "year": %d,
                "authors": [{"id": "%s"}]
            }
            """.formatted(initialAlbumName, initialYear, artistId.get()))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.id").value(id -> albumId.set((String) id));

        String updatedAlbumName = "Álbum Atualizado";
        int updatedYear = 2023;

        client
            .put()
            .uri("/api/v1/albums/" + albumId.get())
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + adminToken);
            })
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
            {
                "name": "%s",
                "year": %d
            }
            """.formatted(updatedAlbumName, updatedYear))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.id").isEqualTo(albumId.get())
                .jsonPath("$.name").isEqualTo(updatedAlbumName)
                .jsonPath("$.year").isEqualTo(updatedYear);
    }

    @Test
    void testPaginationLimits() {
        RestTestClient client = getTestClient();
        String token = getTokenForUser("teste@teste.br", "123456");

        client
            .get()
            .uri("/api/v1/artists?limit=150")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + token);
            })
            .exchange()
            .expectStatus().isOk();

        client
            .get()
            .uri("/api/v1/artists?offset=10&limit=20")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + token);
            })
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void getRecoveryLink() {
        RestTestClient client = getTestClient();

        String email = "teste@teste.br";

        client
            .get()
            .uri("/api/v1/recovery?email=" + email)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.link").isNotEmpty()
                .jsonPath("$.attention").isNotEmpty();
    }

    @Test
    void getUserByIdWithAdminAccess() {
        RestTestClient client = getTestClient();
        String adminToken = getTokenForUser("admin@admin.br", "123456");

        client
            .get()
            .uri("/api/v1/users?limit=1")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + adminToken);
            })
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.users[0].id").value(userId -> {
                    client
                        .get()
                        .uri("/api/v1/users/" + userId)
                        .headers(headers -> {
                            headers.add("Authorization", "Bearer " + adminToken);
                        })
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                            .jsonPath("$.id").isEqualTo(userId)
                            .jsonPath("$.name").isNotEmpty()
                            .jsonPath("$.email").isNotEmpty()
                            .jsonPath("$.isAdmin").isBoolean();
                });
    }

    @Test
    void testPartialUpdateWithUndefinedProperties() {
        RestTestClient client = getTestClient();
        String adminToken = getTokenForUser("admin@admin.br", "123456");

        String newUserEmail = String.format( "teste%d@email.com", new Random().nextInt());

        AtomicReference<String> userId = new AtomicReference<>();
        client
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
            {
                "name": "Usuário Patch Test",
                "email": "%s",
                "password": "123456"
            }
            """.formatted(newUserEmail))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.id").value(id -> userId.set((String) id));

        client
            .put()
            .uri("/api/v1/users/" + userId.get())
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + adminToken);
            })
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
            {
                "name": "Nome Atualizado"
            }
            """)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.id").isEqualTo(userId.get())
                .jsonPath("$.name").isEqualTo("Nome Atualizado")
                .jsonPath("$.email").isEqualTo(newUserEmail)
                .jsonPath("$.isAdmin").isEqualTo(false);
    }

    @Test
    void createArtistWithUserAccessShouldFail() {
        RestTestClient client = getTestClient();
        String userToken = getTokenForUser("teste@teste.br", "123456");

        client
            .post()
            .uri("/api/v1/artists")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + userToken);
            })
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
                {
                    "name": "Artista Não Autorizado"
                }
            """)
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    void updateAlbumWithUserAccessShouldFail() {
        RestTestClient client = getTestClient();
        String userToken = getTokenForUser("teste@teste.br", "123456");
        String fakeAlbumId = UUID.randomUUID().toString();

        client
            .put()
            .uri("/api/v1/albums/" + fakeAlbumId)
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + userToken);
            })
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
            {
                "name": "Álbum Não Autorizado"
            }
            """)
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    void searchUsersWithFilters() {
        RestTestClient client = getTestClient();
        String adminToken = getTokenForUser("admin@admin.br", "123456");

        client
            .get()
            .uri("/api/v1/users?isAdminEqual=true")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + adminToken);
            })
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.users").isArray();

        client
            .get()
            .uri("/api/v1/users?nameLike=%25admin%25")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + adminToken);
            })
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.users").isArray();

        client
            .get()
            .uri("/api/v1/users?emailLike=%25admin%25")
            .headers(headers -> {
                headers.add("Authorization", "Bearer " + adminToken);
            })
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.users").isArray();
    }
}
