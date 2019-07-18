package ua.tennis.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.tennis.domain.Game;
import ua.tennis.service.dto.GameDTO;

/**
 * Mapper for the entity Game and its DTO GameDTO.
 */
@Mapper(componentModel = "spring", uses = {SettMapper.class})
public interface GameMapper extends EntityMapper<GameDTO, Game> {

    @Mapping(source = "sett.id", target = "settId")
    GameDTO toDto(Game game);

    @Mapping(source = "settId", target = "sett")
    Game toEntity(GameDTO gameDTO);

    default Game fromId(Long id) {
        if (id == null) {
            return null;
        }
        Game game = new Game();
        game.setId(id);
        return game;
    }
}
