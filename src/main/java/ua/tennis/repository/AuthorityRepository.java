package ua.tennis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.tennis.domain.Authority;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
