package com.example.tictactoe;

import java.util.HashMap;
import java.util.Map;

public class GameRoom {
    private String gameId;
    private String player1Symbol;
    private String player2Symbol;
    private String status; // "waiting", "ready", "playing", "finished"
    private String currentTurn;
    private Map<String, String> board;
    private String winner;
    private int totalGames; // Number of games in tournament
    private int currentGame; // Current game number
    private int scoreX;
    private int scoreO;
    private int draws;
    
    public GameRoom() {
        // Default constructor for Firebase
    }
    
    public GameRoom(String gameId, String player1Symbol, String status) {
        this.gameId = gameId;
        this.player1Symbol = player1Symbol;
        this.status = status;
        this.currentTurn = "X";
        this.board = new HashMap<>();
        this.winner = "";
        this.totalGames = 1;
        this.currentGame = 1;
        this.scoreX = 0;
        this.scoreO = 0;
        this.draws = 0;
    }
    
    public GameRoom(String gameId, String player1Symbol, String status, int totalGames) {
        this.gameId = gameId;
        this.player1Symbol = player1Symbol;
        this.status = status;
        this.currentTurn = "X";
        this.board = new HashMap<>();
        this.winner = "";
        this.totalGames = totalGames;
        this.currentGame = 1;
        this.scoreX = 0;
        this.scoreO = 0;
        this.draws = 0;
    }
    
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    
    public String getPlayer1Symbol() { return player1Symbol; }
    public void setPlayer1Symbol(String player1Symbol) { this.player1Symbol = player1Symbol; }
    
    public String getPlayer2Symbol() { return player2Symbol; }
    public void setPlayer2Symbol(String player2Symbol) { this.player2Symbol = player2Symbol; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCurrentTurn() { return currentTurn; }
    public void setCurrentTurn(String currentTurn) { this.currentTurn = currentTurn; }
    
    public Map<String, String> getBoard() { return board; }
    public void setBoard(Map<String, String> board) { this.board = board; }
    
    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }
    
    public int getTotalGames() { return totalGames; }
    public void setTotalGames(int totalGames) { this.totalGames = totalGames; }
    
    public int getCurrentGame() { return currentGame; }
    public void setCurrentGame(int currentGame) { this.currentGame = currentGame; }
    
    public int getScoreX() { return scoreX; }
    public void setScoreX(int scoreX) { this.scoreX = scoreX; }
    
    public int getScoreO() { return scoreO; }
    public void setScoreO(int scoreO) { this.scoreO = scoreO; }
    
    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }
}


