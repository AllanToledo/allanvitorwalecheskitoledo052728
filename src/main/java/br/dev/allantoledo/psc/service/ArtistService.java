package br.dev.allantoledo.psc.service;

import br.dev.allantoledo.psc.dto.artist.ArtistCreationForm;
import br.dev.allantoledo.psc.dto.artist.ArtistUpdateForm;
import br.dev.allantoledo.psc.entity.Artist;
import br.dev.allantoledo.psc.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.dev.allantoledo.psc.util.PaginationUtility.getValidLimit;
import static br.dev.allantoledo.psc.util.PaginationUtility.getValidOffset;
import static br.dev.allantoledo.psc.util.StringUtility.fromString;

@Service
@RequiredArgsConstructor
public class ArtistService {
    final ArtistRepository artistRepository;

    public Artist createArtist(ArtistCreationForm artistCreationForm) {
        Artist artist = new Artist();
        artist.setName(artistCreationForm.getName());

        return artistRepository.save(artist);
    }


    public Artist updateArtist(UUID id, ArtistUpdateForm artistUpdateForm) {
        Artist artist = artistRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (artistUpdateForm.getName() != null) {
            artist.setName(artistUpdateForm.getName().orElse(null));
        }

        return artistRepository.save(artist);
    }

    public List<Artist> getArtistCollection(Map<String, String> params) {
        List<UUID> ids = artistRepository.findAllArtistsByParams(
                fromString(String.class, params.get("artistNameLike")),
                fromString(String.class, params.get("albumNameLike")),
                fromString(Integer.class, params.get("albumYearEqual")),
                fromString(Integer.class, params.get("albumYearBefore")),
                fromString(Integer.class, params.get("albumYearAfter")),
                getValidOffset(fromString(Integer.class, params.get("offset"))),
                getValidLimit(fromString(Integer.class, params.get("limit")))
        );

        return artistRepository.findAllByIdsAndFetchAlbums(ids);
    }

    public Artist getArtist(UUID id) {
        return artistRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
