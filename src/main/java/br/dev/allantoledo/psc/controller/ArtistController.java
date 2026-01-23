package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.ArtistCollection;
import br.dev.allantoledo.psc.dto.ArtistInformation;
import br.dev.allantoledo.psc.dto.ArtistCreationForm;
import br.dev.allantoledo.psc.dto.ArtistUpdateForm;
import br.dev.allantoledo.psc.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
        List<ArtistInformation> artists = artistService.getArtistCollection()
                .stream().map(ArtistInformation::fromArtist).toList();

        ArtistCollection artistCollection = new ArtistCollection();
        artistCollection.setArtists(artists);

        return artistCollection;
    }

    @GetMapping("/artists/{id}")
    public ArtistInformation getArtistInformation(@PathVariable UUID id) {
        return ArtistInformation.fromArtist(artistService.getArtist(id));
    }
}
