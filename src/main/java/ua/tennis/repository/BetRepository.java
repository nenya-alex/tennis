package ua.tennis.repository;

import ua.tennis.domain.Bet;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Bet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {

}
