package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.album.AlbumCollection;
import br.dev.allantoledo.psc.dto.album.AlbumCreationForm;
import br.dev.allantoledo.psc.dto.album.FullAlbumInformation;
import br.dev.allantoledo.psc.dto.album.AlbumUpdateForm;
import br.dev.allantoledo.psc.entity.Album;
import br.dev.allantoledo.psc.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/v1/albums")
    public FullAlbumInformation createAlbum(@RequestBody AlbumCreationForm albumCreationForm) {
        return FullAlbumInformation.fromAlbum(albumService.createAlbum(albumCreationForm));
    }

    @Operation(
            summary = "Atualiza um novo álbum.",
            description = "Requer token de administrador."
    )
    @PutMapping("/v1/albums/{id}")
    public FullAlbumInformation updateAlbum(
            @PathVariable UUID id,
            @RequestBody AlbumUpdateForm albumUpdateForm
    ) {
        return FullAlbumInformation.fromAlbum(albumService.updateAlbum(id, albumUpdateForm));
    }

    @Operation(
            summary = "Cadastra uma nova capa ao álbum.",
            description = "Requer token de administrador."
    )
    @PostMapping("/v1/albums/{id}/covers")
    public FullAlbumInformation createAlbum(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file
    ) {
        return FullAlbumInformation.fromAlbum(albumService.createCover(id, file));
    }

    @Operation(
            summary = "Deleta a capa especificada.",
            description = "Requer token de administrador."
    )
    @DeleteMapping("/v1/covers/{id}")
    public FullAlbumInformation createAlbum(
            @PathVariable UUID id
    ) {
        return FullAlbumInformation.fromAlbum(albumService.deleteCover(id));
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
    @GetMapping("/v1/albums")
    public AlbumCollection getAlbumCollection(
            @RequestParam @Parameter(hidden = true) Map<String, String> params
    ) {
        List<Album> albums = albumService.getAlbumCollection(params);

        AlbumCollection albumCollection = new AlbumCollection();
        albumCollection.setAlbums(mapToList(albums, FullAlbumInformation::fromAlbum));

        return albumCollection;
    }

    @Operation(
            summary = "Busca um álbum por identificador.",
            description = "Requer token de usuário ou administrador."
    )
    @GetMapping("/v1/albums/{id}")
    public FullAlbumInformation getAlbum(@PathVariable UUID id) {
        return FullAlbumInformation.fromAlbum(albumService.getAlbum(id));
    }

}
