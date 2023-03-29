package ch.uzh.ifi.hase.soprafs23.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.ifi.hase.soprafs23.constant.MinigameType;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Minigame;

@Service
@Transactional
public class LobbyManagement {
    
    public Lobby createLobby(Lobby lobbyInput){
        Lobby wip = lobbyInput;
        return wip;
    }

    public Minigame getMinigame(Long lobbyId){
        Minigame wip = new Minigame(MinigameType.TIMING_GAME, 500);
        return wip;
    }

}