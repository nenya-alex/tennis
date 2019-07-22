package ua.tennis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tennis.domain.AccountDetail;

@SuppressWarnings("unused")
@Repository
public interface AccountDetailRepository extends JpaRepository<AccountDetail, Long> {
}
