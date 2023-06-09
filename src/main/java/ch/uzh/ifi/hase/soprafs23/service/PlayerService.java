package ch.uzh.ifi.hase.soprafs23.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Team;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;

@Service
@Transactional
public class PlayerService {
    private final Logger log = LoggerFactory.getLogger(PlayerService.class);
    private Random randomizer = new Random();
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private final PlayerRepository playerRepository;


    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    
    }

    public Player createPlayer(Player newPlayer, Lobby lobby) {
       
        // lobby = entityManager.find(Lobby.class, lobby.getId());
        newPlayer.setLobby(lobby);
        playerRepository.save(newPlayer);
        
        return newPlayer;
    }

    public Player getPlayer(Long playerId){
        Player player = playerRepository.findById(playerId). 
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The player with the given Id does not exist!"));
        return player;
    }

    public List<Player> getMinigamePlayers(Team team, int amountOfPlayers){
        List<Player> minigamePlayers = new ArrayList<Player>();
        if (team.getPlayers().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This team has no players");
        }
        int lowestAmountPlayed = lowestRoundsPlayed(team, minigamePlayers.size());
        int optIndex;
        int playersAdded = 0;
        while (playersAdded < amountOfPlayers){
            optIndex = randomizer.nextInt(team.getPlayers().size());
            Player player = team.getPlayers().get(optIndex);
            if (player.getRoundsPlayed() > lowestAmountPlayed){
                continue;
            }
            else{
                if (minigamePlayers.contains(player)){
                    continue;
                }
                else{
                    minigamePlayers.add(player);
                    playersAdded++;
                    lowestAmountPlayed = lowestRoundsPlayed(team, minigamePlayers.size());
                }
            }
        }   
        return minigamePlayers;
    }

    private int lowestRoundsPlayed(Team team, int playersAdded) {
        int lowestAmountPlayed = -1;
        int amountWithLowAmntPl = 0;
        for (Player p : team.getPlayers()){
            if (lowestAmountPlayed == -1){
                lowestAmountPlayed = p.getRoundsPlayed();
                amountWithLowAmntPl++;
            }
            else if (p.getRoundsPlayed() < lowestAmountPlayed){
                lowestAmountPlayed = p.getRoundsPlayed();
                amountWithLowAmntPl = 1;
            }
            else if (p.getRoundsPlayed() == lowestAmountPlayed){
                amountWithLowAmntPl++;
            }
        }
        if (playersAdded >= amountWithLowAmntPl){
            lowestAmountPlayed++;
        }
        return lowestAmountPlayed;
    }

    public void updatePlayers(List<Player> minigamePlayers){
        for (Player p : minigamePlayers){
            Player player = getPlayer(p.getId());
            player.setRoundsPlayed(player.getRoundsPlayed() + 1);
            playerRepository.save(player);
            playerRepository.flush();
        }
    }

    public Player setCurrentSessionId(long playerId, String sessionId){
        Player player = getPlayer(playerId);
        player.setCurrentSessionId(sessionId);
        player.setConnected(true);
        return playerRepository.saveAndFlush(player);
    }



    public Player getPlayerBySession(String sessionId){
        return playerRepository.findByCurrentSessionId(sessionId);
    }

    public void disconnect(Player player){
        player.setCurrentSessionId(null);
        player.setConnected(false);
    }

    public Player connect(Player player, String sessionId){
        player.setCurrentSessionId(sessionId);
        player.setConnected(true);
        return playerRepository.saveAndFlush(player);
    }
}
