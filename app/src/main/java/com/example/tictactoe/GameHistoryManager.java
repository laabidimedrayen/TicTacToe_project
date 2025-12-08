package com.example.tictactoe;

import android.content.Context;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GameHistoryManager {
    private static final String HISTORY_FILE = "all_games_history.ser";
    private static final String TOURNAMENT_FILE = "tournament_data.ser";
    private static final String LOCAL_HISTORY_FILE = "games_history.ser";
    
    private Context context;
    
    public GameHistoryManager(Context context) {
        this.context = context;
    }
    
    // Save a single game (local or online)
    public void saveGame(GameData game) {
        try {
            // Load existing history
            AllGamesHistory history = loadAllGamesHistory();
            if (history == null) {
                history = new AllGamesHistory();
            }
            
            // Add new game
            history.addGame(game);
            
            // Save back to file
            FileOutputStream fos = context.openFileOutput(HISTORY_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(history);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Save tournament data (for local tournaments)
    public void saveTournament(TournamentData tournamentData) {
        try {
            FileOutputStream fos = context.openFileOutput(TOURNAMENT_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tournamentData);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Save local tournament history (for backward compatibility)
    public void saveLocalTournamentHistory(TournamentHistory history) {
        try {
            FileOutputStream fos = context.openFileOutput(LOCAL_HISTORY_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(history);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Load all games history (local + online)
    public AllGamesHistory loadAllGamesHistory() {
        try {
            FileInputStream fis = context.openFileInput(HISTORY_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            AllGamesHistory history = (AllGamesHistory) ois.readObject();
            ois.close();
            fis.close();
            return history;
        } catch (Exception e) {
            return new AllGamesHistory();
        }
    }
    
    // Load tournament data
    public TournamentData loadTournamentData() {
        try {
            FileInputStream fis = context.openFileInput(TOURNAMENT_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            TournamentData data = (TournamentData) ois.readObject();
            ois.close();
            fis.close();
            return data;
        } catch (Exception e) {
            return null;
        }
    }
    
    // Load local tournament history (for backward compatibility)
    public TournamentHistory loadLocalTournamentHistory() {
        try {
            FileInputStream fis = context.openFileInput(LOCAL_HISTORY_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            TournamentHistory history = (TournamentHistory) ois.readObject();
            ois.close();
            fis.close();
            return history;
        } catch (Exception e) {
            return null;
        }
    }
    
    // Get statistics
    public GameStatistics getStatistics() {
        AllGamesHistory history = loadAllGamesHistory();
        if (history == null || history.getGames().isEmpty()) {
            return new GameStatistics();
        }
        
        int totalGames = history.getGames().size();
        int localGames = 0;
        int onlineGames = 0;
        int winsX = 0;
        int winsO = 0;
        int draws = 0;
        int onlineWins = 0;
        int onlineLosses = 0;
        int onlineDraws = 0;
        
        for (GameData game : history.getGames()) {
            if (game.isOnlineGame()) {
                onlineGames++;
                if (game.getWinner().equals("Draw")) {
                    onlineDraws++;
                } else if (game.getWinner().equals(game.getPlayerSymbol())) {
                    onlineWins++;
                } else {
                    onlineLosses++;
                }
            } else {
                localGames++;
                if (game.getWinner().equals("X")) {
                    winsX++;
                } else if (game.getWinner().equals("O")) {
                    winsO++;
                } else {
                    draws++;
                }
            }
        }
        
        return new GameStatistics(totalGames, localGames, onlineGames, winsX, winsO, draws, 
                                 onlineWins, onlineLosses, onlineDraws);
    }
    
    // Clear all history
    public void clearAllHistory() {
        try {
            context.deleteFile(HISTORY_FILE);
            context.deleteFile(TOURNAMENT_FILE);
            context.deleteFile(LOCAL_HISTORY_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

