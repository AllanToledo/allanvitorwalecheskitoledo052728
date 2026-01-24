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

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {
    final AlbumRepository albumRepository;

    public Album createAlbum(AlbumCreationForm albumCreationForm) {
        Album album = new Album();
        album.setName(albumCreationForm.getName());
        Set<Artist> artists = albumCreationForm.getAuthors()
                .stream().map(ArtistInformation::toArtist).collect(Collectors.toSet());
        album.setAuthors(artists);

        return albumRepository.save(album);
    }

    public List<Album> getAlbumCollection() {
        return albumRepository.findAllAndFetchAuthors();
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
                Set<Artist> authors = albumUpdateForm.getAuthors().get().stream()
                        .map(ArtistInformation::toArtist).collect(Collectors.toSet());
                album.setAuthors(authors);
            } else {
                album.setName(null);
            }
        }

        return albumRepository.save(album);
    }

    public Album getAlbum(UUID id) {
        return albumRepository.findAlbumByIdAndFetchAuthors(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
