package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Minigame;
import ch.uzh.ifi.hase.soprafs23.entity.Team;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameOverGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.MinigameGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.ScoresGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.TeamGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.WinnerTeamPutDTO;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  UserGetDTO convertEntityToUserGetDTO(User user);

  // @Mapping(source = "winningScore", target = "winningScore")
  // Lobby convertLobbyPostDTOtoEntity(LobbyPostDTO lobbyPostDTO);

  @Mapping(source = "id", target = "id")
  //@Mapping(source = "winningScore", target = "winningScore")
  @Mapping(source = "inviteCode", target = "inviteCode")
  @Mapping(source = "teams", target = "teams")
  @Mapping(source = "unassignedPlayers", target = "unassignedPlayers")
  LobbyGetDTO convertEntityToLobbyGetDTO(Lobby lobby);

  //@Mapping(source = "winningScore", target = "winningScore")
  @Mapping(source = "teams", target = "teams")
  ScoresGetDTO convertEntityToScoresGetDTO(Lobby lobby);
  
  @Mapping(source = "scoreToGain", target = "scoreToGain")
  @Mapping(source = "type", target = "type")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "team1Players", target = "team1Players")
  @Mapping(source = "team2Players", target = "team2Players")
  MinigameGetDTO convertEntityToMinigameGetDTO(Minigame minigame);

  @Mapping(source = "color", target = "color")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "score", target = "score")
  Team convertWinnerTeamPutDTOToEntity(WinnerTeamPutDTO winnerTeamPutDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "score", target = "score")
  @Mapping(source = "color", target = "color")
  TeamGetDTO convertEntityToTeamGetDTO(Team team); 

  @Mapping(source = "isFinished", target = "isFinished")
  GameOverGetDTO convertEntityToGameOverGetDTO(Game game);

  @Mapping(source = "minigamesChoice", target = "minigamesChoice")
  @Mapping(source = "winningScore", target = "winningScore")
  Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

  GameGetDTO convertEntityToGameGetDTO(Game game);
}
