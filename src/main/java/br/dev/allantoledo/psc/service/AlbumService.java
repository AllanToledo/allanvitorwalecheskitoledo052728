package br.dev.allantoledo.psc.service;

import br.dev.allantoledo.psc.dto.album.AlbumCreationForm;
import br.dev.allantoledo.psc.dto.album.AlbumUpdateForm;
import br.dev.allantoledo.psc.dto.artist.ArtistInformation;
import br.dev.allantoledo.psc.entity.Album;
import br.dev.allantoledo.psc.entity.Artist;
import br.dev.allantoledo.psc.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static br.dev.allantoledo.psc.util.StreamUtility.mapToSet;
import static br.dev.allantoledo.psc.util.StringUtility.fromString;
import static java.util.Objects.requireNonNullElse;

@Service
@RequiredArgsConstructor
public class AlbumService {
    final AlbumRepository albumRepository;

    public Album createAlbum(AlbumCreationForm albumCreationForm) {
        Album album = new Album();
        album.setName(albumCreationForm.getName());
        Set<Artist> artists = mapToSet(albumCreationForm.getAuthors(), ArtistInformationWithAlbums::toArtist);
        album.setAuthors(artists);

        return albumRepository.save(album);
    }

    public List<Album> getAlbumCollection(Map<String, String> params) {
        List<UUID> ids = albumRepository.findAllAlbumsByParams(
                fromString(String.class, params.get("artistNameLike")),
                fromString(UUID.class, params.get("artistIdEqual")),
                fromString(String.class, params.get("albumNameLike")),
                fromString(Integer.class, params.get("albumYearEqual")),
                fromString(Integer.class, params.get("albumYearBefore")),
                fromString(Integer.class, params.get("albumYearAfter")),
                requireNonNullElse(fromString(Integer.class, params.get("offset")), 0),
                requireNonNullElse(fromString(Integer.class, params.get("limit")), 100)
        );

        return albumRepository.findAllByIdsAndFetchAuthors(ids);
    }

    public Album updateAlbum(UUID id, AlbumUpdateForm albumUpdateForm) {
        Album album = albumRepository.findAlbumByIdAndFetchAuthors(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (albumUpdateForm.getName() != null) {
            album.setName(albumUpdateForm.getName().orElse(null));
        }

        if (albumUpdateForm.getYear() != null) {
            album.setYear(albumUpdateForm.getYear().orElse(null));
        }

        if (albumUpdateForm.getAuthors() != null) {
            if (albumUpdateForm.getAuthors().isPresent()) {
                Set<ArtistInformation> artists = albumUpdateForm.getAuthors().get();
                Set<Artist> authors = mapToSet(artists, ArtistInformation::toArtist);
                album.setAuthors(authors);
            } else {
                album.setAuthors(null);
            }
        }

        return albumRepository.save(album);
    }

    public Album getAlbum(UUID id) {
        return albumRepository.findAlbumByIdAndFetchAuthors(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
