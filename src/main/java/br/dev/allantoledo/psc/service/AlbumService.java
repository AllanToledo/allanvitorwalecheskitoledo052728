package br.dev.allantoledo.psc.service;

import br.dev.allantoledo.psc.dto.album.AlbumCreationForm;
import br.dev.allantoledo.psc.dto.album.AlbumInformation;
import br.dev.allantoledo.psc.dto.album.AlbumUpdateForm;
import br.dev.allantoledo.psc.dto.artist.ArtistInformation;
import br.dev.allantoledo.psc.dto.websocket.Payload;
import br.dev.allantoledo.psc.entity.Album;
import br.dev.allantoledo.psc.entity.Artist;
import br.dev.allantoledo.psc.entity.File;
import br.dev.allantoledo.psc.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static br.dev.allantoledo.psc.util.PaginationUtility.getValidLimit;
import static br.dev.allantoledo.psc.util.PaginationUtility.getValidOffset;
import static br.dev.allantoledo.psc.util.StreamUtility.mapToSet;
import static br.dev.allantoledo.psc.util.StringUtility.fromString;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final FileService fileService;
    private final SimpMessagingTemplate template;

    public Album createAlbum(AlbumCreationForm albumCreationForm) {
        Album album = new Album();
        album.setName(albumCreationForm.getName());
        album.setYear(albumCreationForm.getYear());
        Set<Artist> artists = mapToSet(albumCreationForm.getAuthors(), ArtistInformation::toArtist);
        album.setAuthors(artists);

        album =  albumRepository.save(album);
        //notifica os usuários da criação do novo álbum
        Payload payload = Payload.fromData(AlbumInformation.fromAlbum(album));
        template.convertAndSend("/notifications/albums", payload);

        return album;
    }

    public Album updateAlbum(UUID id, AlbumUpdateForm albumUpdateForm) {
        Album album = getAlbum(id);

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

    public List<Album> getAlbumCollection(Map<String, String> params) {
        List<UUID> ids = albumRepository.findAllAlbumsByParams(
                fromString(String.class, params.get("artistNameLike")),
                fromString(UUID.class, params.get("artistIdEqual")),
                fromString(String.class, params.get("albumNameLike")),
                fromString(Integer.class, params.get("albumYearEqual")),
                fromString(Integer.class, params.get("albumYearBefore")),
                fromString(Integer.class, params.get("albumYearAfter")),
                getValidOffset(fromString(Integer.class, params.get("offset"))),
                getValidLimit(fromString(Integer.class, params.get("limit")))
        );

        return albumRepository.findAllByIdsAndFetchAuthors(ids);
    }

    public Album getAlbum(UUID id) {
        return albumRepository.findAlbumByIdAndFetchAll(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Album createCover(UUID id, MultipartFile file) {
        Album album = getAlbum(id);

        if(file == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        File cover = new File();
        cover.setBucket("covers");
        String extension = "bin";
        if (file.getOriginalFilename() != null) {
            int extensionBeginIndex = file.getOriginalFilename().lastIndexOf('.') + 1;
            extension = file.getOriginalFilename().substring(extensionBeginIndex);
        }
        cover.setName(String.format("%s.%s", UUID.randomUUID(), extension));
        cover.setMediaType(file.getContentType());

        try {
            cover = fileService.save(cover, file);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        album.getCovers().add(cover);
        return albumRepository.save(album);
    }

    public Album deleteCover(UUID id) {
        List<Album> albums = albumRepository.findAllByIdsAndFetchAuthors(albumRepository.getAlbumByCoverId(id));
        if (albums.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Album album = albums.getFirst();
        album.setCovers(
            album.getCovers()
            .stream()
            .filter(f -> !f.getId().equals(id))
            .collect(Collectors.toSet())
        );

        fileService.delete(id);

        return albumRepository.save(album);
    }
}
