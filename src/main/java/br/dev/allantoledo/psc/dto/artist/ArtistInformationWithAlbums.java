package br.dev.allantoledo.psc.dto.artist;

import br.dev.allantoledo.psc.dto.album.AlbumInformation;
import br.dev.allantoledo.psc.entity.Artist;
import lombok.Data;

import java.util.List;
import java.util.UUID;

import static br.dev.allantoledo.psc.util.StreamUtility.mapToList;
import static br.dev.allantoledo.psc.util.StreamUtility.mapToSet;

@Data
public class ArtistInformationWithAlbums {
    private UUID id;
    private String name;
    private List<AlbumInformation> albums;

    public static ArtistInformationWithAlbums fromArtist(Artist artist) {
        ArtistInformationWithAlbums artistInformation = new ArtistInformationWithAlbums();
        artistInformation.setId(artist.getId());
        artistInformation.setName(artist.getName());
        artistInformation.setAlbums(mapToList(artist.getAlbums(), AlbumInformation::fromAlbum));
        return artistInformation;
    }

    public static Artist toArtist(ArtistInformationWithAlbums artistInformation) {
        Artist artist = new Artist();
        artist.setId(artistInformation.getId());
        artist.setName(artistInformation.getName());
        artist.setAlbums(mapToSet(artistInformation.getAlbums(), AlbumInformation::toAlbum));
        return artist;
    }
}
