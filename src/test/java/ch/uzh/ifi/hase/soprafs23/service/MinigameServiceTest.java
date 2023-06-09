package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.MinigamePlayers;
import ch.uzh.ifi.hase.soprafs23.constant.MinigameType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.minigame.Minigame;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebAppConfiguration
@SpringBootTest
public class MinigameServiceTest {

    @Autowired
    private MinigameService minigameService;

    @Test
    public void chosenMinigames_success() {
        // given
        List<MinigameType> minigames = Arrays.asList(MinigameType.values());

        // when
        List<MinigameType> chosenMinigames = minigameService.chooseAllMinigames();

        // then
        assertNotNull(chosenMinigames);
        assertEquals(minigames.size(), chosenMinigames.size());
        assertEquals(minigames.get(0), chosenMinigames.get(0));
    }

    @Test
    public void createMinigame_success() {
        // given
        MinigameType minigameType = MinigameType.TIMING_TUMBLE;

        Player player1 = new Player();
        player1.setNickname("test1");

        Player player2 = new Player();
        player2.setNickname("test2");

        List<Player> team1Players = new ArrayList<Player>();
        team1Players.add(player1);
        List<Player> team2Players = new ArrayList<Player>();
        team2Players.add(player2);

        // when
        Minigame createdMinigame = minigameService.createMinigame(minigameType, 2);

        // then
        assertNotNull(createdMinigame);
        assertEquals(minigameType, createdMinigame.getType());
        assertEquals(MinigamePlayers.ONE, createdMinigame.getAmountOfPlayers());
    }

    @Test
    public void getMinigame_validId_success() {
        // given
        MinigameType minigameType = MinigameType.TIMING_TUMBLE;

        Player player1 = new Player();
        player1.setNickname("test1");

        Player player2 = new Player();
        player2.setNickname("test2");

        List<Player> team1Players = new ArrayList<Player>();
        team1Players.add(player1);
        List<Player> team2Players = new ArrayList<Player>();
        team2Players.add(player2);

        Minigame createdMinigame = minigameService.createMinigame(minigameType, 2);

        // when
        Minigame foundMinigame = minigameService.getMinigame(createdMinigame.getId());

        // then
        assertNotNull(foundMinigame);
        assertEquals(createdMinigame.getId(), foundMinigame.getId());
        assertEquals(createdMinigame.getType(), foundMinigame.getType());
        assertEquals(createdMinigame.getDescription(), foundMinigame.getDescription());
        // assertEquals(createdMinigame.getTeam1Players(),
        // foundMinigame.getTeam1Players());
        // assertEquals(createdMinigame.getTeam2Players(),
        // foundMinigame.getTeam2Players());
    }

    @Test
    public void getMinigame_invalidId_throwsException() {
        // given
        MinigameType minigameType = MinigameType.TIMING_TUMBLE;

        Player player1 = new Player();
        player1.setNickname("test1");

        Player player2 = new Player();
        player2.setNickname("test2");

        List<Player> team1Players = new ArrayList<Player>();
        team1Players.add(player1);
        List<Player> team2Players = new ArrayList<Player>();
        team2Players.add(player2);

        // when
        Minigame createdMinigame = minigameService.createMinigame(minigameType, 2);

        // then
        assertThrows(ResponseStatusException.class, () -> {
            minigameService.getMinigame(createdMinigame.getId() + 1);
        });
    }

    @Test
    public void updateMinigame_success() {
        // given
        MinigameType minigameType = MinigameType.TIMING_TUMBLE;

        Player player1 = new Player();
        player1.setNickname("test1");

        Player player2 = new Player();
        player2.setNickname("test2");

        List<Player> team1Players = new ArrayList<Player>();
        team1Players.add(player1);
        List<Player> team2Players = new ArrayList<Player>();
        team2Players.add(player2);

        // when
        Minigame createdMinigame = minigameService.createMinigame(minigameType, 2);
        minigameService.updateMinigame(createdMinigame.getId(), "test");
        Minigame foundMinigame = minigameService.getMinigame(createdMinigame.getId());

        // then
        assertEquals("test", foundMinigame.getWinner());
    }

}
