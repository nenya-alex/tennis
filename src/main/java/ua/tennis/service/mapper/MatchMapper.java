package ua.tennis.service.mapper;

import ua.tennis.domain.*;
import ua.tennis.service.dto.MatchDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Match and its DTO MatchDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface MatchMapper extends EntityMapper<MatchDTO, Match> {


    @Mapping(target = "setts", ignore = true)
    Match toEntity(MatchDTO matchDTO);

    default Match fromId(Long id) {
        if (id == null) {
            return null;
        }
        Match match = new Match();
        match.setId(id);
        return match;
    }
}
