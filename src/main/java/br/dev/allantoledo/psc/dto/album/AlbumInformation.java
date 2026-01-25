package br.dev.allantoledo.psc.dto.album;

import br.dev.allantoledo.psc.entity.Album;
import lombok.Data;

import java.util.UUID;

@Data
public class AlbumInformation {
    private UUID id;
    private String name;
    private Integer year;

    public static AlbumInformation fromAlbum(Album album) {
        AlbumInformation albumInformation = new AlbumInformation();
        albumInformation.setId(album.getId());
        albumInformation.setName(album.getName());
        albumInformation.setYear(album.getYear());

        return albumInformation;
    }

    public static Album toAlbum(AlbumInformation albumInformation) {
        Album album = new Album();
        album.setId(albumInformation.getId());
        album.setName(albumInformation.getName());
        album.setYear(albumInformation.getYear());

        return album;
    }
}
