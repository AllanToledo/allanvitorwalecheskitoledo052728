package br.dev.allantoledo.psc.service;

import br.dev.allantoledo.psc.dto.ArtistCreationForm;
import br.dev.allantoledo.psc.dto.ArtistUpdateForm;
import br.dev.allantoledo.psc.entity.Artist;
import br.dev.allantoledo.psc.repository.ArtistRepository;
import br.dev.allantoledo.psc.util.EntityUpdater;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistService {
    final ArtistRepository artistRepository;
    final Validator validator;

    public Artist createArtist(ArtistCreationForm artistCreationForm) {
        Artist artist = new Artist();
        artist.setName(artistCreationForm.getName());

        return artistRepository.save(artist);
    }


    public Artist updateArtist(UUID id, ArtistUpdateForm artistUpdateForm) {
        Artist artist = artistRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        EntityUpdater.apply(artistUpdateForm, artist);

        return artistRepository.save(artist);
    }

    public List<Artist> getArtistCollection() {
        return artistRepository.findAll();
    }

    public Artist getArtist(UUID id) {
        return artistRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
