package ua.tennis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tennis.domain.Account;
import ua.tennis.domain.enumeration.AccountStatus;
import ua.tennis.domain.enumeration.AccountType;

import java.util.List;

@SuppressWarnings("unused")
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByType(AccountType accountType);

    List<Account> findAllByType(AccountType accountType);

    Account findFirstByTypeAndStatus(AccountType accountType, AccountStatus accountStatus);

}
