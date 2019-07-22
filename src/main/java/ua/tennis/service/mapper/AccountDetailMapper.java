package ua.tennis.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.tennis.domain.AccountDetail;
import ua.tennis.service.dto.AccountDetailDTO;

@Mapper(componentModel = "spring", uses = {BetMapper.class, AccountMapper.class})
public interface AccountDetailMapper extends EntityMapper<AccountDetailDTO, AccountDetail> {

    @Mapping(source = "bet.id", target = "betId")
    @Mapping(source = "account.id", target = "accountId")
    AccountDetailDTO toDto(AccountDetail accountDetail);

    @Mapping(source = "betId", target = "bet")
    @Mapping(source = "accountId", target = "account")
    AccountDetail toEntity(AccountDetailDTO accountDetailDTO);

    default AccountDetail fromId(Long id) {
        if (id == null) {
            return null;
        }
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setId(id);
        return accountDetail;
    }
}
