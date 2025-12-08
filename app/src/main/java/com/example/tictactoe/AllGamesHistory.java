package com.example.tictactoe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllGamesHistory implements Serializable {
    private ArrayList<GameData> games;
    
    public AllGamesHistory() {
        this.games = new ArrayList<>();
    }
    
    public void addGame(GameData game) {
        games.add(game);
        // Sort by timestamp (newest first)
        Collections.sort(games, (g1, g2) -> Long.compare(g2.getTimestamp(), g1.getTimestamp()));
    }
    
    public ArrayList<GameData> getGames() {
        return games;
    }
    
    public int getTotalGames() {
        return games.size();
    }
    
    public ArrayList<GameData> getLocalGames() {
        ArrayList<GameData> localGames = new ArrayList<>();
        for (GameData game : games) {
            if (!game.isOnlineGame()) {
                localGames.add(game);
            }
        }
        return localGames;
    }
    
    public ArrayList<GameData> getOnlineGames() {
        ArrayList<GameData> onlineGames = new ArrayList<>();
        for (GameData game : games) {
            if (game.isOnlineGame()) {
                onlineGames.add(game);
            }
        }
        return onlineGames;
    }
}

