package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.artist.*;
import br.dev.allantoledo.psc.entity.Artist;
import br.dev.allantoledo.psc.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.dev.allantoledo.psc.util.StreamUtility.mapToList;

@RestController
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @PostMapping("/artists")
    public ArtistInformationWithAlbums createArtist(
            @RequestBody ArtistCreationForm artistCreationForm
    ) {
        return ArtistInformationWithAlbums.fromArtist(artistService.createArtist(artistCreationForm));
    }

    @PutMapping("/artists/{id}")
    public ArtistInformationWithAlbums updateArtist(
            @PathVariable UUID id,
            @RequestBody ArtistUpdateForm artistUpdateForm
    ) {
        return ArtistInformationWithAlbums.fromArtist(artistService.updateArtist(id, artistUpdateForm));
    }

    @GetMapping("/artists")
    public ArtistCollection getArtistCollection(
            @RequestParam Map<String, String> params
    ) {
        List<Artist> artists = artistService.getArtistCollection(params);

        ArtistCollection artistCollection = new ArtistCollection();
        artistCollection.setArtists(mapToList(artists, ArtistInformationWithAlbums::fromArtist));

        return artistCollection;
    }

    @GetMapping("/artists/{id}")
    public ArtistInformationWithAlbums getArtistInformation(@PathVariable UUID id) {
        return ArtistInformationWithAlbums.fromArtist(artistService.getArtist(id));
    }
}
