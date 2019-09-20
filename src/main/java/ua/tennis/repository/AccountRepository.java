package ua.tennis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tennis.domain.Account;
import ua.tennis.domain.enumeration.AccountType;

@SuppressWarnings("unused")
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByType(AccountType accountType);

    Account findFirstByTypeAndStatus(AccountType accountType, AccountStatus accountStatus);

}
