package ua.tennis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tennis.domain.Settings;

@SuppressWarnings("unused")
@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {

    Settings findByKey(String key);

}
