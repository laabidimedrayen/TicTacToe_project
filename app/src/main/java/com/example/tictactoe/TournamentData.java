package com.example.tictactoe;

import java.io.Serializable;

public class TournamentData implements Serializable {
    private int scoreX;
    private int scoreO;
    private int draws;
    private int totalGames;
    private String winner;

    public TournamentData(int scoreX, int scoreO, int draws, int totalGames, String winner) {
        this.scoreX = scoreX;
        this.scoreO = scoreO;
        this.draws = draws;
        this.totalGames = totalGames;
        this.winner = winner;
    }

    public int getScoreX() { return scoreX; }
    public int getScoreO() { return scoreO; }
    public int getDraws() { return draws; }
    public int getTotalGames() { return totalGames; }
    public String getWinner() { return winner; }
}
