package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.MinigameType;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.Lob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import ch.uzh.ifi.hase.soprafs23.entity.Minigame;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Team;


@Service
@Transactional
public class LobbyManagement {
    
    private final Logger log = LoggerFactory.getLogger(LobbyManagement.class);

    @Autowired
    private final LobbyRepository lobbyRepository;

    private final MinigameService minigameService;
    private final TeamService teamService;



    private Random randomizer = new Random();
    

    public LobbyManagement(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository, MinigameService minigameService, TeamService teamService) {
        this.lobbyRepository = lobbyRepository;
        this.minigameService = minigameService;
        this.teamService = teamService;
      }   

    public Lobby createLobby(Lobby newLobby) {
        int inviteCode = new Random().nextInt(900000) + 100000;
        while (lobbyRepository.findByInviteCode(inviteCode) != null){
          inviteCode = new Random().nextInt(900000) + 100000;
        }
        newLobby.setInviteCode(inviteCode);

        List<MinigameType> minigames = minigameService.chosenMinigames();
        newLobby.setMinigamesChoice(minigames);
        List<Team> teams = new ArrayList<Team>();
        teams.add(teamService.createTeam());
        teams.add(teamService.createTeam());
        newLobby.setTeams(teams);

        List<Player> unassignedPlayers = new ArrayList<Player>();
        newLobby.setUnassignedPlayers(unassignedPlayers);
        
        newLobby = lobbyRepository.save(newLobby);
        lobbyRepository.flush();
    
        log.debug("Created Information for User: {}", newLobby);

        //ResponseStatusException missing -> TODO: add 

        return newLobby;
      }
    
      public Lobby getLobby(Long lobbyId) {
        Lobby lobby = lobbyRepository.findById(lobbyId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The lobby with the given Id does not exist!"));
        return lobby;
      }

      public Lobby getLobby(int inviteCode) {
        Lobby lobby = lobbyRepository.findByInviteCode(inviteCode);
        if (lobby == null){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lobby with the given Invite Code does not exist!");
        }
        return lobby;
      }

      public Minigame getMinigame(Long lobbyId){
        Lobby lobby = getLobby(lobbyId);
        Minigame minigame = lobby.getUpcomingMinigame();
        if (minigame == null){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No upcomming Minigame was found!");
        }
        return minigame;
      }

      public MinigameType getNextMinigameType(Long lobbyId){
        Lobby lobby = getLobby(lobbyId);
        List<MinigameType> minigamesChoice = lobby.getMinigamesChoice();
        List<Minigame> minigamesPlayed = lobby.getMinigamesPlayed();

        int index = randomizer.nextInt(minigamesChoice.size());
        MinigameType nextMinigameType = minigamesChoice.get(index);
        if (minigamesPlayed.size() != 0){
        while (nextMinigameType.equals(minigamesPlayed.get(minigamesPlayed.size()-1).getType())){
          nextMinigameType = minigamesChoice.get(randomizer.nextInt(minigamesChoice.size()));
        }}
        return nextMinigameType;
      }

      public void addUpcommingMinigame(Long lobbyId, MinigameType type){
        if (type == null){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No upcoming Minigame!");
        }

        //random player choice

        Minigame nextMinigame = minigameService.createMinigame(type);
        Lobby lobby = getLobby(lobbyId);
        lobby.setUpcomingMinigame(nextMinigame);
        lobbyRepository.save(lobby);
        lobbyRepository.flush();
      }

      private void addMinigameToPlayedList(Long lobbyId, Minigame startedMinigame){
        Lobby lobby = getLobby(lobbyId);
        lobby.addToMinigamesPlayed(startedMinigame);
      }

      public void addToUnassignedPlayers(Long lobbyId, Player newPlayer){
        Lobby lobby = getLobby(lobbyId);
        if (lobby == null || newPlayer == null){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby or Player is empty!");
        }
        lobby.addToUnassignedPlayers(newPlayer);
        lobbyRepository.save(lobby);
        lobbyRepository.flush();
      }

      public void removeFromUnassignedPlayers(Long lobbyId, Player remPlayer){
        Lobby lobby = getLobby(lobbyId);
        if (lobby == null || remPlayer == null){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby or Player is empty!");
        }
        List<Player> players = lobby.getUnassignedPlayers();
        players.remove(remPlayer);
        lobbyRepository.save(lobby);
        lobbyRepository.flush();
      }

      public boolean ableToJoin(int inviteCode, Player playerToCreate){
        Lobby lobby = getLobby(inviteCode);
        int cnt = 0;
        for (Player p : lobby.getUnassignedPlayers()){
          if (p.getNickname().equals(playerToCreate.getNickname())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Player with this Nickname already exists in this lobby!");
          }
          cnt += 1;
        }
        for (Team t : lobby.getTeams()){
          for (Player p : t.getPlayers()){
            if (p.getNickname().equals(playerToCreate.getNickname())){
              throw new ResponseStatusException(HttpStatus.CONFLICT, "Player with this Nickname already exists in this lobby!");
            }
            cnt += 1;
          }
        }
        if (cnt == 8){
          throw new ResponseStatusException(HttpStatus.LOCKED, "Player limit for lobby was reached!");
        }

        return true;
      }

      

}
