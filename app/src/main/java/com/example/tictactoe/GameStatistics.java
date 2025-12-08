package com.example.tictactoe;

public class GameStatistics {
    private int totalGames;
    private int localGames;
    private int onlineGames;
    private int winsX;
    private int winsO;
    private int draws;
    private int onlineWins;
    private int onlineLosses;
    private int onlineDraws;
    
    public GameStatistics() {
        this.totalGames = 0;
        this.localGames = 0;
        this.onlineGames = 0;
        this.winsX = 0;
        this.winsO = 0;
        this.draws = 0;
        this.onlineWins = 0;
        this.onlineLosses = 0;
        this.onlineDraws = 0;
    }
    
    public GameStatistics(int totalGames, int localGames, int onlineGames, 
                         int winsX, int winsO, int draws,
                         int onlineWins, int onlineLosses, int onlineDraws) {
        this.totalGames = totalGames;
        this.localGames = localGames;
        this.onlineGames = onlineGames;
        this.winsX = winsX;
        this.winsO = winsO;
        this.draws = draws;
        this.onlineWins = onlineWins;
        this.onlineLosses = onlineLosses;
        this.onlineDraws = onlineDraws;
    }
    
    // Getters
    public int getTotalGames() { return totalGames; }
    public int getLocalGames() { return localGames; }
    public int getOnlineGames() { return onlineGames; }
    public int getWinsX() { return winsX; }
    public int getWinsO() { return winsO; }
    public int getDraws() { return draws; }
    public int getOnlineWins() { return onlineWins; }
    public int getOnlineLosses() { return onlineLosses; }
    public int getOnlineDraws() { return onlineDraws; }
}

