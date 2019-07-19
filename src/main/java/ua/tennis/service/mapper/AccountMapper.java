package ua.tennis.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.tennis.domain.Account;
import ua.tennis.service.dto.AccountDTO;

@Mapper(componentModel = "spring", uses = {BetMapper.class})
public interface AccountMapper extends EntityMapper<AccountDTO, Account> {

    @Mapping(source = "bet.id", target = "betId")
    AccountDTO toDto(Account account);

    @Mapping(source = "betId", target = "bet")
    Account toEntity(AccountDTO accountDTO);

    default Account fromId(Long id) {
        if (id == null) {
            return null;
        }
        Account account = new Account();
        account.setId(id);
        return account;
    }
}
