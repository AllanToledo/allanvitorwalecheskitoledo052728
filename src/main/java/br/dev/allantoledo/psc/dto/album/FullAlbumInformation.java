package br.dev.allantoledo.psc.dto.album;

import br.dev.allantoledo.psc.dto.artist.ArtistInformation;
import br.dev.allantoledo.psc.dto.file.FileInformation;
import br.dev.allantoledo.psc.entity.Album;
import lombok.Data;

import java.util.List;
import java.util.UUID;

import static br.dev.allantoledo.psc.util.StreamUtility.mapToList;

@Data
public class FullAlbumInformation {
    private UUID id;
    private String name;
    private Integer year;
    private List<ArtistInformation> authors;
    private List<FileInformation> covers;

    public static FullAlbumInformation fromAlbum(Album album) {
        FullAlbumInformation fullAlbumInformation = new FullAlbumInformation();
        fullAlbumInformation.setId(album.getId());
        fullAlbumInformation.setName(album.getName());
        fullAlbumInformation.setYear(album.getYear());
        fullAlbumInformation.setAuthors(mapToList(album.getAuthors(), ArtistInformation::fromArtist));
        fullAlbumInformation.setCovers(mapToList(album.getCovers(), FileInformation::fromCover));

        return fullAlbumInformation;
    }
}
