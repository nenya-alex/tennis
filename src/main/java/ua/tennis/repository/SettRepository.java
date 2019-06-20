package ua.tennis.repository;

import ua.tennis.domain.Sett;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Sett entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SettRepository extends JpaRepository<Sett, Long> {

}
