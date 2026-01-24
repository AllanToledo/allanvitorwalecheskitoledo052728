package br.dev.allantoledo.psc.repository;

import br.dev.allantoledo.psc.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID> {

    //Usando JOIN FETCH resolve o problema de N+1 consultas
    @Query("SELECT a FROM Album a JOIN FETCH a.authors")
    List<Album> findAllAndFetchAuthors();

    @Query("SELECT a FROM Album a JOIN FETCH a.authors WHERE a.id = :id")
    Optional<Album> findAlbumByIdAndFetchAuthors(UUID id);
}
