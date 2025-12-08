package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private RadioGroup symbolGroup;
    private RadioButton selectedSymbol;
    private RadioGroup gamesGroup;
    private String playerSymbol = "X";
    private int numberOfGames = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        symbolGroup = findViewById(R.id.symbolGroup);
        gamesGroup = findViewById(R.id.gamesGroup);
        Button playButton = findViewById(R.id.playButton);
        Button principleButton = findViewById(R.id.principleButton);
        Button scoresButton = findViewById(R.id.scoresButton);
        Button onlineButton = findViewById(R.id.onlineButton);

        // Par défaut X est sélectionné
        symbolGroup.check(R.id.radioX);
        gamesGroup.check(R.id.radio15);

        symbolGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedSymbol = findViewById(checkedId);
            playerSymbol = selectedSymbol.getText().toString();
        });

        gamesGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedGames = findViewById(checkedId);
            numberOfGames = Integer.parseInt(selectedGames.getText().toString());
        });

        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("PLAYER_SYMBOL", playerSymbol);
            intent.putExtra("NUMBER_OF_GAMES", numberOfGames);
            startActivity(intent);
        });

        principleButton.setOnClickListener(v -> showPrinciple());

        scoresButton.setOnClickListener(v -> loadAndDisplayScores());
        
        if (onlineButton != null) {
            onlineButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, OnlineGameActivity.class);
                startActivity(intent);
            });
        }
    }

    private void showPrinciple() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Principe du jeu");
        builder.setMessage("Le jeu X-O se joue sur une grille de 3 × 3 cases, où deux joueurs s'affrontent : " +
                "l'un choisit le symbole X et l'autre le symbole O.\n\n" +
                "À tour de rôle, chaque joueur place son symbole dans une case vide.\n\n" +
                "Une partie se termine lorsqu'un joueur aligne trois symboles identiques sur une ligne, " +
                "une colonne ou une diagonale, ou lorsque toutes les cases sont remplies sans qu'aucun joueur n'ait gagné.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    // Enhanced method to display all game history
    private void loadAndDisplayScores() {
        GameHistoryManager historyManager = new GameHistoryManager(this);
        AllGamesHistory allHistory = historyManager.loadAllGamesHistory();
        TournamentData tournamentData = historyManager.loadTournamentData();
        GameStatistics stats = historyManager.getStatistics();

        if (allHistory == null || allHistory.getTotalGames() == 0) {
            Toast.makeText(this, "Aucune partie sauvegardée", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder message = new StringBuilder();
        
        // Statistics section
        message.append("=== STATISTIQUES GÉNÉRALES ===\n");
        message.append("Total parties: ").append(stats.getTotalGames()).append("\n");
        message.append("Parties locales: ").append(stats.getLocalGames()).append("\n");
        message.append("Parties en ligne: ").append(stats.getOnlineGames()).append("\n\n");
        
        // Local tournament results
        if (tournamentData != null) {
            message.append("=== DERNIER TOURNOI LOCAL ===\n");
            message.append("Score X: ").append(tournamentData.getScoreX()).append("\n");
            message.append("Score O: ").append(tournamentData.getScoreO()).append("\n");
            message.append("Parties nulles: ").append(tournamentData.getDraws()).append("\n");
            message.append("Vainqueur: ").append(tournamentData.getWinner()).append("\n\n");
        }
        
        // Online games statistics
        if (stats.getOnlineGames() > 0) {
            message.append("=== STATISTIQUES EN LIGNE ===\n");
            message.append("Victoires: ").append(stats.getOnlineWins()).append("\n");
            message.append("Défaites: ").append(stats.getOnlineLosses()).append("\n");
            message.append("Matchs nuls: ").append(stats.getOnlineDraws()).append("\n\n");
        }
        
        // Recent games (last 10)
        message.append("=== DERNIÈRES PARTIES ===\n");
        int count = 0;
        for (GameData game : allHistory.getGames()) {
            if (count >= 10) break;
            
            if (game.isOnlineGame()) {
                message.append("🌐 En ligne - ID: ").append(game.getGameId()).append("\n");
                message.append("   ").append(game.getFormattedDate()).append("\n");
                if (game.getWinner().equals("Draw")) {
                    message.append("   Match nul");
                } else if (game.getWinner().equals(game.getPlayerSymbol())) {
                    message.append("   ✅ Victoire (").append(game.getPlayerSymbol()).append(")");
                } else {
                    message.append("   ❌ Défaite (vous: ").append(game.getPlayerSymbol()).append(")");
                }
            } else {
                message.append("📱 Partie ").append(game.getGameNumber());
                if (game.getWinner().equals("Draw")) {
                    message.append(": Match nul");
                } else {
                    message.append(": Victoire de ").append(game.getWinner());
                }
            }
            message.append("\n\n");
            count++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Historique des parties");
        builder.setMessage(message.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }

}
