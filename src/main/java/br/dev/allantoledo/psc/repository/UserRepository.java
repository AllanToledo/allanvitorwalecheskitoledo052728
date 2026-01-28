package br.dev.allantoledo.psc.repository;

import br.dev.allantoledo.psc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findUserByEmail(String email);

    @Query(value = """
            SELECT * FROM app_user
            WHERE
                (:nameLike  is null or name  ILIKE :nameLike) AND
                (:emailLike is null or email ILIKE :emailLike) AND
                (:isAdmin   is null or is_admin = :isAdmin)
            ORDER BY name
            LIMIT :limit
            OFFSET :offset
        """, nativeQuery = true)
    List<User> findAllUsersByParams(
            String nameLike,
            String emailLike,
            Boolean isAdmin,
            int offset,
            int limit
    );
}
