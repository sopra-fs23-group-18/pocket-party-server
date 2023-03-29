package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.MinigameType;

public class MinigameGetDTO {
    private MinigameType type;
    private int scoreToGain;
    private String team1Player;
    private String team2Player;

    public String getTeam1Player() {
        return team1Player;
    }

    public void setTeam1Player(String team1Player) {
        this.team1Player = team1Player;
    } 

    public String getTeam2Player() {
        return team2Player;
    }

    public void setTeam2Player(String team2Player) {
        this.team2Player = team2Player;
    }

    public int getScoreToGain() {
        return scoreToGain;
    }

    public void setScoreToGain(int scoreToGain) {
        this.scoreToGain = scoreToGain;
    }

    public MinigameType getType() {
        return type;
    }

    public void setType(MinigameType type) {
        this.type = type;
    }
    
}
