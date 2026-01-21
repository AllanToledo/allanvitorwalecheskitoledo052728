package br.dev.allantoledo.psc.service;

import br.dev.allantoledo.psc.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtistService {
    final ArtistRepository artistRepository;
}
