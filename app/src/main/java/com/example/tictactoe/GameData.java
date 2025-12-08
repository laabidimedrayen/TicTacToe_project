package com.example.tictactoe;

import java.io.Serializable;
import java.util.Date;

public class GameData implements Serializable {
    private String winner; // "X", "O", ou "Draw"
    private int gameNumber;
    private boolean isOnlineGame;
    private String gameId; // For online games
    private String gameType; // "local" or "online"
    private long timestamp; // When the game was played
    private String playerSymbol; // Player's symbol in this game

    public GameData(String winner, int gameNumber) {
        this.winner = winner;
        this.gameNumber = gameNumber;
        this.isOnlineGame = false;
        this.gameType = "local";
        this.timestamp = System.currentTimeMillis();
        this.gameId = "";
        this.playerSymbol = "";
    }
    
    public GameData(String winner, String gameId, String playerSymbol) {
        this.winner = winner;
        this.gameId = gameId;
        this.playerSymbol = playerSymbol;
        this.isOnlineGame = true;
        this.gameType = "online";
        this.gameNumber = 0; // Online games are single games
        this.timestamp = System.currentTimeMillis();
    }

    public String getWinner() { return winner; }
    public int getGameNumber() { return gameNumber; }
    public boolean isOnlineGame() { return isOnlineGame; }
    public String getGameId() { return gameId; }
    public String getGameType() { return gameType; }
    public long getTimestamp() { return timestamp; }
    public String getPlayerSymbol() { return playerSymbol; }
    
    public String getFormattedDate() {
        Date date = new Date(timestamp);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        return sdf.format(date);
    }
}
