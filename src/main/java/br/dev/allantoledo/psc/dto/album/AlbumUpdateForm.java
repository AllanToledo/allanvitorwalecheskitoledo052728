package br.dev.allantoledo.psc.dto.album;

import br.dev.allantoledo.psc.dto.artist.ArtistInformation;
import lombok.Data;

import java.util.Optional;
import java.util.Set;


@Data
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class AlbumUpdateForm {
    private Optional<String> name;
    private Optional<Integer> year;
    private Optional<Set<ArtistInformation>> authors;
}
