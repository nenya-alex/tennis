package ua.tennis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tennis.domain.Sett;


@SuppressWarnings("unused")
@Repository
public interface SettRepository extends JpaRepository<Sett, Long> {

}
