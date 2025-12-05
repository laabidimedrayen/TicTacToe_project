package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class ResultActivity extends AppCompatActivity {

    private int scoreX, scoreO, draws, totalGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        scoreX = getIntent().getIntExtra("SCORE_X", 0);
        scoreO = getIntent().getIntExtra("SCORE_O", 0);
        draws = getIntent().getIntExtra("DRAWS", 0);
        totalGames = getIntent().getIntExtra("TOTAL_GAMES", 0);

        TextView resultText = findViewById(R.id.resultText);
        TextView detailsText = findViewById(R.id.detailsText);
        Button saveButton = findViewById(R.id.saveButton);
        Button homeButton = findViewById(R.id.homeButton);

        String winner;
        if (scoreX > scoreO) {
            winner = "Victoire du joueur X!";
        } else if (scoreO > scoreX) {
            winner = "Victoire du joueur O!";
        } else {
            winner = "Égalité!";
        }

        resultText.setText(winner);
        detailsText.setText("Score X: " + scoreX + "\nScore O: " + scoreO + "\nParties nulles: " + draws);

        saveButton.setOnClickListener(v -> saveTournament(winner));

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void saveTournament(String winner) {
        try {
            TournamentData data = new TournamentData(scoreX, scoreO, draws, totalGames, winner);
            FileOutputStream fos = openFileOutput("tournament_data.ser", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
            Toast.makeText(this, "Tournoi sauvegardé!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur de sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }
}
