package br.dev.allantoledo.psc.dto.album;

import br.dev.allantoledo.psc.dto.artist.ArtistInformation;
import br.dev.allantoledo.psc.entity.Album;
import lombok.Data;

import java.util.List;
import java.util.UUID;

import static br.dev.allantoledo.psc.util.StreamUtility.mapToList;

@Data
public class AlbumInformationWithAuthors {
    private UUID id;
    private String name;
    private Integer year;
    private List<ArtistInformation> authors;

    public static AlbumInformationWithAuthors fromAlbum(Album album) {
        AlbumInformationWithAuthors albumInformationWithAuthors = new AlbumInformationWithAuthors();
        albumInformationWithAuthors.setId(album.getId());
        albumInformationWithAuthors.setName(album.getName());
        albumInformationWithAuthors.setYear(album.getYear());
        albumInformationWithAuthors.setAuthors(mapToList(album.getAuthors(), ArtistInformation::fromArtist));

        return albumInformationWithAuthors;
    }
}
