package br.dev.allantoledo.psc.repository;

import br.dev.allantoledo.psc.entity.Album;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID> {


    @Query("SELECT a FROM Album a JOIN FETCH a.authors WHERE a.id = :id")
    Optional<Album> findAlbumByIdAndFetchAuthors(UUID id);

    @Query(value = """
        SELECT subselect.id FROM (
            SELECT DISTINCT album.id, album.year FROM album
            INNER JOIN author ON album.id = author.id_album
            INNER JOIN artist ON artist.id = author.id_artist
            WHERE (:artistName      IS NULL OR artist.name ILIKE :artistName)
            AND (:artistId          IS NULL OR artist.id = :artistId)
            AND (:albumName         IS NULL OR album.name ILIKE :albumName)
            AND (:albumYear         IS NULL OR album.year = :albumYear)
            AND (:albumYearBefore   IS NULL OR album.year < :albumYearBefore)
            AND (:albumYearAfter    IS NULL OR album.year > :albumYearAfter)
            ORDER BY album.year
            OFFSET :offset LIMIT :limit
        ) subselect
    """, nativeQuery = true)
    List<UUID> findAllAlbumsByParams(
            String artistName,
            UUID artistId,
            String albumName,
            Integer albumYear,
            Integer albumYearBefore,
            Integer albumYearAfter,
            int offset,
            int limit
    );

    //Usando JOIN FETCH resolve o problema de N+1 consultas
    @Query("SELECT a FROM Album a LEFT JOIN FETCH a.authors WHERE a.id IN :albumsIds ORDER BY a.year")
    List<Album> findAllByIdsAndFetchAuthors(List<UUID> albumsIds);
}
