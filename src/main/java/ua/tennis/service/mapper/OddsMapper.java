package ua.tennis.service.mapper;

import ua.tennis.domain.*;
import ua.tennis.service.dto.OddsDTO;

import org.mapstruct.*;

import java.util.Set;

/**
 * Mapper for the entity {@link Odds} and its DTO {@link OddsDTO}.
 */
@Mapper(componentModel = "spring", uses = {MatchMapper.class})
public interface OddsMapper extends EntityMapper<OddsDTO, Odds> {

    @Mapping(source = "match.id", target = "matchId")
    OddsDTO toDto(Odds odds);

    @Mapping(source = "matchId", target = "match")
    Odds toEntity(OddsDTO oddsDTO);

    default Odds fromId(Long id) {
        if (id == null) {
            return null;
        }
        Odds odds = new Odds();
        odds.setId(id);
        return odds;
    }

    Set<Odds> oddsDtosToEntities(Set<OddsDTO> oddsDTOs);
}
