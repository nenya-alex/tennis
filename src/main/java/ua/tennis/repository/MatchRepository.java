package ua.tennis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tennis.domain.Match;
import ua.tennis.domain.enumeration.MatchStatus;

import java.time.Instant;
import java.util.List;

@SuppressWarnings("unused")
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByStatusAndUpdatedDateBefore(MatchStatus status, Instant date);

    List<Match> findByStatus(MatchStatus status);

}
