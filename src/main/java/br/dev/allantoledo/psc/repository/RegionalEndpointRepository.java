package br.dev.allantoledo.psc.repository;

import br.dev.allantoledo.psc.entity.RegionalEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegionalEndpointRepository extends JpaRepository<RegionalEndpoint, UUID> {

    @Query("""
            SELECT r FROM RegionalEndpoint r
            WHERE (:idEqual is null or r.idExtern = :idEqual)
                AND (:namePattern is null or r.name ilike :namePattern)
                AND (:activeEqual is null or r.active = :activeEqual)
        """)
    List<RegionalEndpoint> findByParams(
            Long idEqual,
            String namePattern,
            Boolean activeEqual
    );


    @Transactional
    @Modifying
    @Query("""
        UPDATE RegionalEndpoint r SET r.active = false WHERE r.idExtern = :id
    """)
    void deactivateAllByIdExtern(Long id);
}
