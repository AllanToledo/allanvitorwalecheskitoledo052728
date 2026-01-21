package br.dev.allantoledo.psc.service;

import br.dev.allantoledo.psc.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumService {
    final AlbumRepository albumRepository;
}
