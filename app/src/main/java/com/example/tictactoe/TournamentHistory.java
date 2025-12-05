package com.example.tictactoe;

import java.io.Serializable;
import java.util.ArrayList;

public class TournamentHistory implements Serializable {
    private ArrayList<GameData> games;

    public TournamentHistory() {
        this.games = new ArrayList<>();
    }

    public void addGame(GameData game) {
        games.add(game);
    }

    public ArrayList<GameData> getGames() {
        return games;
    }

    public int getTotalGames() {
        return games.size();
    }
}
