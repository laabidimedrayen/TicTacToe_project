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
import java.io.FileInputStream;
import java.io.ObjectInputStream;

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

    // REMPLACEZ cette méthode loadAndDisplayScores()
    private void loadAndDisplayScores() {
        TournamentData tournamentData = getSavedTournamentData();
        TournamentHistory gamesHistory = getGamesHistory();

        if (tournamentData != null) {
            StringBuilder message = new StringBuilder();
            message.append("=== RÉSULTAT FINAL ===\n");
            message.append("Score X: ").append(tournamentData.getScoreX()).append("\n");
            message.append("Score O: ").append(tournamentData.getScoreO()).append("\n");
            message.append("Parties nulles: ").append(tournamentData.getDraws()).append("\n");
            message.append("Total parties: ").append(tournamentData.getTotalGames()).append("\n");
            message.append("Vainqueur: ").append(tournamentData.getWinner()).append("\n\n");

            if (gamesHistory != null) {
                message.append("=== DÉTAILS DES PARTIES ===\n");
                for (GameData game : gamesHistory.getGames()) {
                    message.append("Partie ").append(game.getGameNumber()).append(": ");
                    if (game.getWinner().equals("Draw")) {
                        message.append("Match nul\n");
                    } else {
                        message.append("Victoire de ").append(game.getWinner()).append("\n");
                    }
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Scores du dernier tournoi");
            builder.setMessage(message.toString());
            builder.setPositiveButton("OK", null);
            builder.show();
        } else {
            Toast.makeText(this, "Aucun tournoi sauvegardé", Toast.LENGTH_SHORT).show();
        }
    }

    // AJOUTEZ cette méthode GET pour récupérer les données du tournoi
    private TournamentData getSavedTournamentData() {
        try {
            FileInputStream fis = openFileInput("tournament_data.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            TournamentData data = (TournamentData) ois.readObject();
            ois.close();
            fis.close();
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    // AJOUTEZ cette méthode GET pour récupérer l'historique de toutes les parties
    private TournamentHistory getGamesHistory() {
        try {
            FileInputStream fis = openFileInput("games_history.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            TournamentHistory history = (TournamentHistory) ois.readObject();
            ois.close();
            fis.close();
            return history;
        } catch (Exception e) {
            return null;
        }
    }
}
