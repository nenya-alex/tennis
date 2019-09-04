package ua.tennis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tennis.domain.Bet;
import ua.tennis.domain.enumeration.BetStatus;

import java.util.Set;

@SuppressWarnings("unused")
@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {

    Set<Bet> findByMatchIdAndStatus(Long id, BetStatus status);

    Bet findByMatchIdAndStatus(Long id, BetStatus status);

}
