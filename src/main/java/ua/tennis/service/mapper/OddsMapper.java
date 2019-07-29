package ua.tennis.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.tennis.domain.Odds;
import ua.tennis.service.dto.OddsDTO;

import java.util.Set;

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
