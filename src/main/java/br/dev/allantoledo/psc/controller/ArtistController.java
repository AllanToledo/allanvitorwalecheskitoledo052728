package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.artist.*;
import br.dev.allantoledo.psc.entity.Artist;
import br.dev.allantoledo.psc.service.ArtistService;
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
@Tag(name="Artistas")
public class ArtistController {

    private final ArtistService artistService;

    @Operation(
            summary = "Cadastra um novo artista.",
            description = "Requer token de administrador."
    )
    @PostMapping("/v1/artists")
    public ArtistInformationWithAlbums createArtist(
            @RequestBody ArtistCreationForm artistCreationForm
    ) {
        return ArtistInformationWithAlbums.fromArtist(artistService.createArtist(artistCreationForm));
    }

    @Operation(
            summary = "Atualiza um artista.",
            description = "Requer token de administrador."
    )
    @PutMapping("/v1/artists/{id}")
    public ArtistInformationWithAlbums updateArtist(
            @PathVariable UUID id,
            @RequestBody ArtistUpdateForm artistUpdateForm
    ) {
        return ArtistInformationWithAlbums.fromArtist(artistService.updateArtist(id, artistUpdateForm));
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
    @GetMapping("/v1/artists")
    public ArtistCollection getArtistCollection(
            @RequestParam @Parameter(hidden = true) Map<String, String> params
    ) {
        List<Artist> artists = artistService.getArtistCollection(params);

        ArtistCollection artistCollection = new ArtistCollection();
        artistCollection.setArtists(mapToList(artists, ArtistInformationWithAlbums::fromArtist));

        return artistCollection;
    }

    @Operation(
            summary = "Busca um artista por identificador.",
            description = "Requer token de usuário ou administrador."
    )
    @GetMapping("/v1/artists/{id}")
    public ArtistInformationWithAlbums getArtistInformation(@PathVariable UUID id) {
        return ArtistInformationWithAlbums.fromArtist(artistService.getArtist(id));
    }
}
