package br.dev.allantoledo.psc.dto.album;

import br.dev.allantoledo.psc.dto.artist.ArtistInformation;
import lombok.Data;

import java.util.List;

@Data
public class AlbumCreationForm {
    private String name;
    private Integer year;
    private List<ArtistInformation> authors;
}
