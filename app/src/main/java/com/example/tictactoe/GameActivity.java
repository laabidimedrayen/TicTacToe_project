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
import java.util.Map;

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
    
    // Online mode variables
    private boolean isOnlineMode = false;
    private String gameId;
    private String mySymbol;
    private String opponentSymbol;
    private boolean isMyTurn = false;
    private OnlineGameService gameService;
    
    // History manager
    private GameHistoryManager historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize history manager
        historyManager = new GameHistoryManager(this);
        
        isOnlineMode = getIntent().getBooleanExtra("ONLINE_MODE", false);
        
        if (isOnlineMode) {
            setupOnlineGame();
        } else {
            setupLocalGame();
        }

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
    
    private void setupLocalGame() {
        player1Symbol = getIntent().getStringExtra("PLAYER_SYMBOL");
        totalGames = getIntent().getIntExtra("NUMBER_OF_GAMES", 15);
        player2Symbol = player1Symbol.equals("X") ? "O" : "X";
        tournamentHistory = new TournamentHistory();
    }
    
    private void setupOnlineGame() {
        gameService = OnlineGameService.getInstance();
        gameId = getIntent().getStringExtra("GAME_ID");
        mySymbol = getIntent().getStringExtra("PLAYER_SYMBOL");
        player1Symbol = getIntent().getStringExtra("PLAYER1_SYMBOL");
        player2Symbol = getIntent().getStringExtra("PLAYER2_SYMBOL");
        opponentSymbol = mySymbol.equals("X") ? "O" : "X";
        totalGames = getIntent().getIntExtra("NUMBER_OF_GAMES", 1);
        currentGame = 1;
        scoreX = 0;
        scoreO = 0;
        draws = 0;
        tournamentHistory = new TournamentHistory();
        
        // Determine who goes first (X always goes first)
        isMyTurn = mySymbol.equals("X");
        
        // Listen for game state changes
        gameService.setupGameListener(gameId, new OnlineGameService.GameStateListener() {
            @Override
            public void onGameReady(GameRoom room) {
                runOnUiThread(() -> {
                    updateBoardFromFirebase(room);
                });
            }
            
            @Override
            public void onGameStateChanged(GameRoom room) {
                runOnUiThread(() -> {
                    updateBoardFromFirebase(room);
                    checkGameStatus(room);
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(GameActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
        
        updateGameNumber();
    }
    
    private void updateBoardFromFirebase(GameRoom room) {
        Map<String, String> board = room.getBoard();
        if (board != null) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    String key = i + "_" + j;
                    String value = board.get(key);
                    if (value != null && !value.isEmpty()) {
                        buttons[i][j].setText(value);
                    } else {
                        buttons[i][j].setText("");
                    }
                }
            }
        }
        
        // Update tournament info if available
        if (room.getTotalGames() > 0) {
            totalGames = room.getTotalGames();
        }
        if (room.getCurrentGame() > 0) {
            currentGame = room.getCurrentGame();
        }
        scoreX = room.getScoreX();
        scoreO = room.getScoreO();
        draws = room.getDraws();
        
        // Update turn
        String currentTurn = room.getCurrentTurn();
        isMyTurn = currentTurn != null && currentTurn.equals(mySymbol);
        
        // Update round count
        roundCount = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!buttons[i][j].getText().toString().equals("")) {
                    roundCount++;
                }
            }
        }
        
        updateGameNumber();
        updateScores();
    }
    
    private void checkGameStatus(GameRoom room) {
        String winner = room.getWinner();
        if (winner != null && !winner.isEmpty()) {
            // Update scores
            if (winner.equals("X")) {
                scoreX++;
            } else if (winner.equals("O")) {
                scoreO++;
            } else {
                draws++;
            }
            
            // Save online game to device storage
            saveOnlineGameToDevice(winner);
            
            // Update Firebase with scores
            gameService.updateTournamentScores(gameId, scoreX, scoreO, draws);
            
            // Check if tournament is complete
            if (currentGame >= totalGames) {
                // Tournament finished
                if (totalGames == 1) {
                    // Single game
                    if (winner.equals("Draw")) {
                        showOnlineGameResult("Match nul!");
                    } else if (winner.equals(mySymbol)) {
                        showOnlineGameResult("Vous avez gagné!");
                    } else {
                        showOnlineGameResult("Vous avez perdu!");
                    }
                } else {
                    // Tournament
                    String tournamentWinner;
                    if (scoreX > scoreO) {
                        tournamentWinner = "X";
                    } else if (scoreO > scoreX) {
                        tournamentWinner = "O";
                    } else {
                        tournamentWinner = "Draw";
                    }
                    showOnlineTournamentResult(tournamentWinner);
                }
            } else {
                // Next game in tournament
                currentGame++;
                gameService.nextGame(gameId, currentGame);
                new Handler().postDelayed(() -> {
                    resetBoard();
                    updateGameNumber();
                }, 2000);
            }
            return;
        }
        
        // Check for win after board update
        if (roundCount >= 5) { // Minimum moves for a win
            String[][] field = new String[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    field[i][j] = buttons[i][j].getText().toString();
                }
            }
            
            String detectedWinner = checkForWinOnline(field);
            if (detectedWinner != null && (room.getWinner() == null || room.getWinner().isEmpty())) {
                gameService.updateWinner(gameId, detectedWinner);
            } else if (roundCount == 9 && detectedWinner == null && (room.getWinner() == null || room.getWinner().isEmpty())) {
                gameService.updateWinner(gameId, "Draw");
            }
        }
    }
    
    private void showOnlineTournamentResult(String winner) {
        String message;
        if (winner.equals("Draw")) {
            message = "Tournoi terminé: Égalité!\n\nScore X: " + scoreX + "\nScore O: " + scoreO + "\nParties nulles: " + draws;
        } else if (winner.equals(mySymbol)) {
            message = "Félicitations! Vous avez gagné le tournoi!\n\nScore X: " + scoreX + "\nScore O: " + scoreO + "\nParties nulles: " + draws;
        } else {
            message = "Tournoi terminé. Vous avez perdu.\n\nScore X: " + scoreX + "\nScore O: " + scoreO + "\nParties nulles: " + draws;
        }
        
        new Handler().postDelayed(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tournoi terminé");
            builder.setMessage(message);
            builder.setPositiveButton("OK", (dialog, which) -> {
                gameService.leaveGame(gameId);
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
            builder.setCancelable(false);
            builder.show();
        }, 1000);
    }
    
    private void saveOnlineGameToDevice(String winner) {
        GameData onlineGame = new GameData(winner, gameId, mySymbol);
        historyManager.saveGame(onlineGame);
    }
    
    private String checkForWinOnline(String[][] field) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                return field[i][0];
            }
        }
        
        // Check columns
        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                return field[0][i];
            }
        }
        
        // Check diagonals
        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            return field[0][0];
        }
        
        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            return field[0][2];
        }
        
        return null;
    }

    private void onCellClick(View v) {
        Button button = (Button) v;

        if (!button.getText().toString().equals("")) {
            return;
        }
        
        if (isOnlineMode) {
            handleOnlineMove(button);
        } else {
            handleLocalMove(button);
        }
    }
    
    private void handleOnlineMove(Button button) {
        if (!isMyTurn) {
            Toast.makeText(this, "Attendez votre tour", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get button position
        int row = -1, col = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j] == button) {
                    row = i;
                    col = j;
                    break;
                }
            }
        }
        
        if (row == -1 || col == -1) return;
        
        // Make move on Firebase
        button.setText(mySymbol);
        gameService.makeMove(gameId, row, col, mySymbol);
        isMyTurn = false;
        
        roundCount++;
        
        // Win checking will be handled by checkGameStatus when Firebase updates
    }
    
    private void handleLocalMove(Button button) {
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
    
    private void showOnlineGameResult(String message) {
        new Handler().postDelayed(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Partie terminée");
            builder.setMessage(message);
            builder.setPositiveButton("OK", (dialog, which) -> {
                gameService.leaveGame(gameId);
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
            builder.setCancelable(false);
            builder.show();
        }, 1000);
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
        
        // Save to new unified history system
        historyManager.saveGame(game);
        
        // Also save to old format for backward compatibility
        historyManager.saveLocalTournamentHistory(tournamentHistory);
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
