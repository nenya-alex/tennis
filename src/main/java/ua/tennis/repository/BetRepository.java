package ua.tennis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tennis.domain.Bet;


/**
 * Spring Data  repository for the Bet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {

}
