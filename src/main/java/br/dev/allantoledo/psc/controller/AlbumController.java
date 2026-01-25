package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.album.AlbumCollection;
import br.dev.allantoledo.psc.dto.album.AlbumCreationForm;
import br.dev.allantoledo.psc.dto.album.AlbumInformationWithAuthors;
import br.dev.allantoledo.psc.dto.album.AlbumUpdateForm;
import br.dev.allantoledo.psc.entity.Album;
import br.dev.allantoledo.psc.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.dev.allantoledo.psc.util.StreamUtility.mapToList;

@RestController
@RequiredArgsConstructor
public class AlbumController {

    final AlbumService albumService;

    @PostMapping("/albums")
    public AlbumInformationWithAuthors createAlbum(@RequestBody AlbumCreationForm albumCreationForm) {
        return AlbumInformationWithAuthors.fromAlbum(albumService.createAlbum(albumCreationForm));
    }

    @PutMapping("/albums/{id}")
    public AlbumInformationWithAuthors updateAlbum(
            @PathVariable UUID id,
            @RequestBody AlbumUpdateForm albumUpdateForm
    ) {
        return AlbumInformationWithAuthors.fromAlbum(albumService.updateAlbum(id, albumUpdateForm));
    }

    @GetMapping("/albums")
    public AlbumCollection getAlbumCollection(@RequestParam Map<String, String> params) {
        List<Album> albums = albumService.getAlbumCollection(params);

        AlbumCollection albumCollection = new AlbumCollection();
        albumCollection.setAlbums(mapToList(albums, AlbumInformationWithAuthors::fromAlbum));

        return albumCollection;
    }

    @GetMapping("/albums/{id}")
    public AlbumInformationWithAuthors getAlbum(@PathVariable UUID id) {
        return AlbumInformationWithAuthors.fromAlbum(albumService.getAlbum(id));
    }

}
