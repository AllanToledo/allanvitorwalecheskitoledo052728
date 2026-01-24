package br.dev.allantoledo.psc.dto.album;

import br.dev.allantoledo.psc.dto.artist.ArtistInformation;
import br.dev.allantoledo.psc.entity.Album;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AlbumInformation {
    private UUID id;
    private String name;
    private Integer year;
    private List<ArtistInformation> authors;

    public static AlbumInformation fromAlbum(Album album) {
        AlbumInformation albumInformation = new AlbumInformation();
        albumInformation.setId(album.getId());
        albumInformation.setName(album.getName());
        albumInformation.setYear(album.getYear());
        albumInformation.setAuthors(album.getAuthors().stream().map(ArtistInformation::fromArtist).toList());

        return albumInformation;
    }
}
