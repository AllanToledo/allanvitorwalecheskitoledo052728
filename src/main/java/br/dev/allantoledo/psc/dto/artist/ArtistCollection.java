package br.dev.allantoledo.psc.dto.artist;

import lombok.Data;

import java.util.List;

@Data
public class ArtistCollection {
    private List<ArtistInformationWithAlbums> artists;
}
