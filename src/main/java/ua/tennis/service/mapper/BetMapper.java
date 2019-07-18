package ua.tennis.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.tennis.domain.Bet;
import ua.tennis.service.dto.BetDTO;

/**
 * Mapper for the entity {@link Bet} and its DTO {@link BetDTO}.
 */
@Mapper(componentModel = "spring", uses = {MatchMapper.class})
public interface BetMapper extends EntityMapper<BetDTO, Bet> {

    @Mapping(source = "match.id", target = "matchId")
    BetDTO toDto(Bet bet);

    @Mapping(source = "matchId", target = "match")
    Bet toEntity(BetDTO betDTO);

    default Bet fromId(Long id) {
        if (id == null) {
            return null;
        }
        Bet bet = new Bet();
        bet.setId(id);
        return bet;
    }
}
