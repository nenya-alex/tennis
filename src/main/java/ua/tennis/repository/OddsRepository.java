package ua.tennis.repository;

import ua.tennis.domain.Odds;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Odds entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OddsRepository extends JpaRepository<Odds, Long> {

    Odds findTopByMatchIdOrderByCheckDateDesc(Long id);

    Odds findByMatchId(Long id);

}
