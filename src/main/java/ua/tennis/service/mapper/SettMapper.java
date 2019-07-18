package ua.tennis.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.tennis.domain.Sett;
import ua.tennis.service.dto.SettDTO;

/**
 * Mapper for the entity Sett and its DTO SettDTO.
 */
@Mapper(componentModel = "spring", uses = {MatchMapper.class})
public interface SettMapper extends EntityMapper<SettDTO, Sett> {

    @Mapping(source = "match.id", target = "matchId")
    SettDTO toDto(Sett sett);

    @Mapping(target = "games", ignore = true)
    @Mapping(source = "matchId", target = "match")
    Sett toEntity(SettDTO settDTO);

    default Sett fromId(Long id) {
        if (id == null) {
            return null;
        }
        Sett sett = new Sett();
        sett.setId(id);
        return sett;
    }
}
