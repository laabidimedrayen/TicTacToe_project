package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GameActivity extends AppCompatActivity {

    private Button[][] buttons = new Button[3][3];
    private boolean player1Turn = true;
    private int roundCount = 0;
    private int scoreX = 0;
    private int scoreO = 0;
    private int draws = 0;
    private int currentGame = 1;
    private int totalGames;
    private String player1Symbol;
    private String player2Symbol;
    private TournamentHistory tournamentHistory;

    private TextView gameNumberText;
    private TextView scoreXText;
    private TextView scoreOText;
    private TextView drawsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        player1Symbol = getIntent().getStringExtra("PLAYER_SYMBOL");
        totalGames = getIntent().getIntExtra("NUMBER_OF_GAMES", 15);
        player2Symbol = player1Symbol.equals("X") ? "O" : "X";

        // Initialiser l'historique du tournoi
        tournamentHistory = new TournamentHistory();

        gameNumberText = findViewById(R.id.gameNumberText);
        scoreXText = findViewById(R.id.scoreXText);
        scoreOText = findViewById(R.id.scoreOText);
        drawsText = findViewById(R.id.drawsText);

        updateGameNumber();
        updateScores();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this::onCellClick);
            }
        }
    }

    private void onCellClick(View v) {
        Button button = (Button) v;

        if (!button.getText().toString().equals("")) {
            return;
        }

        if (player1Turn) {
            button.setText(player1Symbol);
        } else {
            button.setText(player2Symbol);
        }

        roundCount++;

        if (checkForWin()) {
            String gameWinner;
            if (player1Turn) {
                if (player1Symbol.equals("X")) {
                    scoreX++;
                    gameWinner = "X";
                } else {
                    scoreO++;
                    gameWinner = "O";
                }
            } else {
                if (player2Symbol.equals("X")) {
                    scoreX++;
                    gameWinner = "X";
                } else {
                    scoreO++;
                    gameWinner = "O";
                }
            }

            // Sauvegarder la partie dans le fichier
            saveGameToFile(gameWinner);

            updateScores();

            new Handler().postDelayed(() -> {
                if (currentGame < totalGames) {
                    currentGame++;
                    resetBoard();
                    updateGameNumber();
                } else {
                    showTournamentResult();
                }
            }, 1500);

        } else if (roundCount == 9) {
            draws++;

            // Sauvegarder la partie nulle dans le fichier
            saveGameToFile("Draw");

            updateScores();

            new Handler().postDelayed(() -> {
                if (currentGame < totalGames) {
                    currentGame++;
                    resetBoard();
                    updateGameNumber();
                } else {
                    showTournamentResult();
                }
            }, 1500);

        } else {
            player1Turn = !player1Turn;
        }
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        // Lignes
        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                return true;
            }
        }

        // Colonnes
        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                return true;
            }
        }

        // Diagonales
        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            return true;
        }

        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            return true;
        }

        return false;
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
        roundCount = 0;
        player1Turn = true;
    }

    private void updateGameNumber() {
        gameNumberText.setText("Partie " + currentGame + " / " + totalGames);
    }

    private void updateScores() {
        scoreXText.setText("Score X: " + scoreX);
        scoreOText.setText("Score O: " + scoreO);
        drawsText.setText("Parties nulles: " + draws);
    }

    // Méthode pour sauvegarder chaque partie dans le fichier
    private void saveGameToFile(String winner) {
        GameData game = new GameData(winner, currentGame);
        tournamentHistory.addGame(game);

        try {
            FileOutputStream fos = openFileOutput("games_history.ser", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tournamentHistory);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTournamentResult() {
        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
        intent.putExtra("SCORE_X", scoreX);
        intent.putExtra("SCORE_O", scoreO);
        intent.putExtra("DRAWS", draws);
        intent.putExtra("TOTAL_GAMES", totalGames);
        startActivity(intent);
        finish();
    }
}
