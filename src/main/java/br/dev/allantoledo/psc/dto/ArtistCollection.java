package br.dev.allantoledo.psc.dto;

import br.dev.allantoledo.psc.entity.Artist;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ArtistCollection {
    private List<ArtistInformation> artists;
}
