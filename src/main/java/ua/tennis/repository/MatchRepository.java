package ua.tennis.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ua.tennis.domain.Match;


/**
 * Spring Data JPA repository for the Match entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

}
