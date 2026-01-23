package br.dev.allantoledo.psc.dto;

import br.dev.allantoledo.psc.entity.Artist;
import lombok.Data;

import java.util.UUID;

@Data
public class ArtistInformation {
    private UUID id;
    private String name;

    public static ArtistInformation fromArtist(Artist artist) {
        ArtistInformation artistInformation = new ArtistInformation();
        artistInformation.setId(artist.getId());
        artistInformation.setName(artist.getName());
        return artistInformation;
    }
}
