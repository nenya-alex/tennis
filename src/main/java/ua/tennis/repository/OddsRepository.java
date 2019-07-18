package ua.tennis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tennis.domain.Odds;


/**
 * Spring Data  repository for the Odds entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OddsRepository extends JpaRepository<Odds, Long> {

    Odds findTopByMatchIdOrderByCheckDateDesc(Long id);

    Odds findByMatchId(Long id);

}
