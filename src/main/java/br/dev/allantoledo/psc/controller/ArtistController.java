package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.artist.*;
import br.dev.allantoledo.psc.entity.Artist;
import br.dev.allantoledo.psc.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static br.dev.allantoledo.psc.util.StreamUtility.mapToList;

@RestController
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @PostMapping("/artists")
    public ArtistInformation createArtist(
            @RequestBody ArtistCreationForm artistCreationForm
    ) {
        return ArtistInformation.fromArtist(artistService.createArtist(artistCreationForm));
    }

    @PutMapping("/artists/{id}")
    public ArtistInformation updateArtist(
            @PathVariable UUID id,
            @RequestBody ArtistUpdateForm artistUpdateForm
    ) {
        return ArtistInformation.fromArtist(artistService.updateArtist(id, artistUpdateForm));
    }

    @GetMapping("/artists")
    public ArtistCollection getArtistCollection() {
        List<Artist> artists = artistService.getArtistCollection();

        ArtistCollection artistCollection = new ArtistCollection();
        artistCollection.setArtists(mapToList(artists, ArtistInformation::fromArtist));

        return artistCollection;
    }

    @GetMapping("/artists/{id}")
    public ArtistInformation getArtistInformation(@PathVariable UUID id) {
        return ArtistInformation.fromArtist(artistService.getArtist(id));
    }
}
