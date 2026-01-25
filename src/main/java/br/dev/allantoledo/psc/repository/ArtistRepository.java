package br.dev.allantoledo.psc.repository;

import br.dev.allantoledo.psc.entity.Album;
import br.dev.allantoledo.psc.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, UUID> {
    @Query(value = """
        SELECT subselect.id FROM (
            SELECT DISTINCT artist.id, artist.name FROM artist
            LEFT JOIN author ON artist.id = author.id_artist
            LEFT JOIN album ON album.id = author.id_album
            WHERE (:artistName      IS NULL OR artist.name ILIKE :artistName)
            AND (:albumName         IS NULL OR album.name ILIKE :albumName)
            AND (:albumYear         IS NULL OR album.year = :albumYear)
            AND (:albumYearBefore   IS NULL OR album.year < :albumYearBefore)
            AND (:albumYearAfter    IS NULL OR album.year > :albumYearAfter)
            ORDER BY artist.name
            OFFSET :offset LIMIT :limit
        ) subselect
    """, nativeQuery = true)
    List<UUID> findAllArtistsByParams(
            String artistName,
            String albumName,
            Integer albumYear,
            Integer albumYearBefore,
            Integer albumYearAfter,
            int offset,
            int limit
    );

    //Usando JOIN FETCH resolve o problema de N+1 consultas
    @Query("""
        SELECT a FROM Artist a
        JOIN FETCH a.albums b
        WHERE a.id IN :artistsIds ORDER BY a.name
    """)
    List<Artist> findAllByIdsAndFetchAlbums(List<UUID> artistsIds);
}
