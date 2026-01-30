package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.album.AlbumCollection;
import br.dev.allantoledo.psc.dto.album.AlbumCreationForm;
import br.dev.allantoledo.psc.dto.album.AlbumInformationWithAuthors;
import br.dev.allantoledo.psc.dto.album.AlbumUpdateForm;
import br.dev.allantoledo.psc.entity.Album;
import br.dev.allantoledo.psc.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.dev.allantoledo.psc.util.StreamUtility.mapToList;

@RestController
@RequiredArgsConstructor
@Tag(name="Álbuns")
public class AlbumController {

    final AlbumService albumService;

    @Operation(
            summary = "Cadastra um novo álbum.",
            description = "Requer token de administrador."
    )
    @PostMapping("/albums")
    public AlbumInformationWithAuthors createAlbum(@RequestBody AlbumCreationForm albumCreationForm) {
        return AlbumInformationWithAuthors.fromAlbum(albumService.createAlbum(albumCreationForm));
    }

    @Operation(
            summary = "Atualiza um novo álbum.",
            description = "Requer token de administrador."
    )
    @PutMapping("/albums/{id}")
    public AlbumInformationWithAuthors updateAlbum(
            @PathVariable UUID id,
            @RequestBody AlbumUpdateForm albumUpdateForm
    ) {
        return AlbumInformationWithAuthors.fromAlbum(albumService.updateAlbum(id, albumUpdateForm));
    }

    @Operation(
            summary = "Lista os artistas.",
            description = "Requer token de usuário ou administrador.",
            parameters = {
                    @Parameter(name = "artistNameLike",
                            schema = @Schema(type = "string")),
                    @Parameter(name = "albumNameLike",
                            schema = @Schema(type = "string")),
                    @Parameter(name = "albumYearEqual",
                            schema = @Schema(
                                    type = "integer",
                                    format = "int32",
                                    minimum = "0",
                                    maximum = "3000"
                            )),
                    @Parameter(name = "albumYearBefore",
                            schema = @Schema(
                                    type = "integer",
                                    format = "int32",
                                    maximum = "3000"
                            )),
                    @Parameter(name = "albumYearAfter",
                            schema = @Schema(
                                    type = "integer",
                                    format = "int32",
                                    minimum = "0"
                            )),
                    @Parameter(name = "offset",
                            schema = @Schema(
                                    type = "integer",
                                    format = "int32",
                                    minimum = "0",
                                    maximum = "2147483647",
                                    defaultValue = "0"
                            )),
                    @Parameter(name = "limit",
                            schema = @Schema(
                                    type = "integer",
                                    format = "int32",
                                    maximum = "100",
                                    minimum = "0",
                                    defaultValue = "100"
                            ))
            }
    )
    @GetMapping("/albums")
    public AlbumCollection getAlbumCollection(
            @RequestParam @Parameter(hidden = true) Map<String, String> params
    ) {
        List<Album> albums = albumService.getAlbumCollection(params);

        AlbumCollection albumCollection = new AlbumCollection();
        albumCollection.setAlbums(mapToList(albums, AlbumInformationWithAuthors::fromAlbum));

        return albumCollection;
    }

    @Operation(
            summary = "Busca um álbum por identificador.",
            description = "Requer token de usuário ou administrador."
    )
    @GetMapping("/albums/{id}")
    public AlbumInformationWithAuthors getAlbum(@PathVariable UUID id) {
        return AlbumInformationWithAuthors.fromAlbum(albumService.getAlbum(id));
    }

}
