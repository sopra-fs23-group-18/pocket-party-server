package ch.uzh.ifi.hase.soprafs23.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Team;
import ch.uzh.ifi.hase.soprafs23.entity.minigame.Minigame;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameOverGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.MinigameGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.ScoresGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.WinnerTeamGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.MinigameWinnerTeamPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyManagement;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePostDTO;

@RestController
public class GameController {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private final LobbyManagement lobbyManager;

    @Autowired
    private final GameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    GameController(LobbyManagement lobbyManager, GameService gameService) {
        this.lobbyManager = lobbyManager;
        this.gameService = gameService;
    }

    /**
     * @input winningScore, chosenMinigames
     */
    @PostMapping("/lobbies/{lobbyId}/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO createGame(@PathVariable long lobbyId, @RequestBody GamePostDTO gamePostDTO) {
        // convert API user to internal representation
        Game game = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);

        // create user
        Game createdGame = gameService.createGame(game, lobbyId);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(createdGame);
    }

    /**
     * @return game; format: id, winningScore
     */
    @GetMapping("/lobbies/{lobbyId}/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGame(@PathVariable long gameId) {
        Game game = gameService.getGame(gameId);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    /**
     * @return minigame; format: description, scoreToGain, team1Players, team2Players,
     *         type, amountOfPlayers
     */
    @GetMapping("/lobbies/{lobbyId}/games/{gameId}/minigame")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MinigameGetDTO getMinigame(@PathVariable long gameId) {
        Minigame nextMinigame = gameService.getMinigame(gameId);
        return DTOMapper.INSTANCE.convertEntityToMinigameGetDTO(nextMinigame);
    }

    /**
     * @change adds next minigame
     */
    @PostMapping("/lobbies/{lobbyId}/games/{gameId}/minigames")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public MinigameGetDTO addMinigame(@PathVariable long gameId) {
        Minigame nextMinigame = gameService.addUpcomingMinigame(gameId);
        return DTOMapper.INSTANCE.convertEntityToMinigameGetDTO(nextMinigame);
    }

    /**
     * @change add players to Minigame
     */
    @PutMapping("/lobbies/{lobbyId}/games/{gameId}/minigames")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMinigame(@PathVariable long gameId) {
        gameService.updateUpcomingMinigame(gameId);
    }

    /**
     * @input winner team of minigame; format: score, name
     * @change updates score of teams, add winnerName to minigame, update
     *         roundsPlayed of players.
     */
    @PutMapping("/lobbies/{lobbyId}/games/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateScore(@PathVariable long gameId, @RequestBody MinigameWinnerTeamPutDTO winnerTeamPutDTO) {
        Team winnerTeamInput = DTOMapper.INSTANCE.convertMinigameWinnerTeamPutDTOtoEntity(winnerTeamPutDTO);
        gameService.finishedMinigameUpdate(gameId, winnerTeamInput);
    }

    /**
     * @return enum: NOT_FINISHED or DRAW/WINNER if someone achieved winningScore.
     */
    @GetMapping("/lobbies/{lobbyId}/games/{gameId}/gameover")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameOverGetDTO getIsFinished(@PathVariable long gameId) {
        Game game = gameService.getGame(gameId);
        return DTOMapper.INSTANCE.convertEntityToGameOverGetDTO(game);
    }

    /**
     * @return winnerTeam: (id, score, name, players)
     */
    @GetMapping("/lobbies/{lobbyId}/games/{gameId}/winner")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public WinnerTeamGetDTO getWinner(@PathVariable long gameId) {
        Team team = gameService.getWinner(gameId);
        return DTOMapper.INSTANCE.convertEntityToWinnerTeamGetDTO(team);
    }

    /**
     * @return winning score + both teams (id, score, name)
     */
    @GetMapping("/lobbies/{lobbyId}/games/{gameId}/scores")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ScoresGetDTO getScores(@PathVariable long lobbyId, @PathVariable long gameId) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        Game game = gameService.getGame(gameId);
        return DTOMapper.INSTANCE.convertEntitiesToScoresGetDTO(lobby, game);
    }
}
