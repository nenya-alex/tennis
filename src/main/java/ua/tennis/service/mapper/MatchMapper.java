package ua.tennis.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.tennis.domain.Match;
import ua.tennis.service.dto.MatchDTO;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OddsMapper.class})
public interface MatchMapper extends EntityMapper<MatchDTO, Match> {


    @Mapping(target = "setts", ignore = true)
    @Mapping(target = "homeScore", ignore = true)
    @Mapping(target = "awayScore", ignore = true)
    Match toEntity(MatchDTO matchDTO);

    default Match fromId(Long id) {
        if (id == null) {
            return null;
        }
        Match match = new Match();
        match.setId(id);
        return match;
    }

    List<Match> matchDtosToEntity(List<MatchDTO> matchDTOs);
}
