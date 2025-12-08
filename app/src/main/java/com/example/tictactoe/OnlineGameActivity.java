package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OnlineGameActivity extends AppCompatActivity {

    private RadioGroup symbolGroup;
    private RadioGroup gamesGroup;
    private String playerSymbol = "X";
    private int numberOfGames = 1;
    private EditText gameIdEditText;
    private TextView gameIdDisplay;
    private Button createGameButton;
    private Button joinGameButton;
    private OnlineGameService gameService;
    private String currentGameId;

    private static final String TAG = "OnlineGameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game);

        // Test Firebase connection
        Log.d(TAG, "Testing Firebase connection...");
        try {
            FirebaseDebugHelper.testConnection();
            FirebaseDebugHelper.checkDatabaseRules();
            Log.d(TAG, "Firebase debug tests completed");
        } catch (Exception e) {
            Log.e(TAG, "Error testing Firebase: " + e.getMessage(), e);
        }

        // Initialize game service
        Log.d(TAG, "Initializing OnlineGameService...");
        try {
            gameService = OnlineGameService.getInstance();
            if (gameService != null) {
                Log.d(TAG, "OnlineGameService initialized: SUCCESS");
                boolean ready = gameService.ensureInitialized();
                if (!ready) {
                    Toast.makeText(this, "Erreur: Service non disponible", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e(TAG, "OnlineGameService initialized: FAILED - service is null");
                Toast.makeText(this, "Erreur: Impossible d'initialiser le service", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing OnlineGameService: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur d'initialisation: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        symbolGroup = findViewById(R.id.onlineSymbolGroup);
        gamesGroup = findViewById(R.id.onlineGamesGroup);
        gameIdEditText = findViewById(R.id.gameIdEditText);
        gameIdDisplay = findViewById(R.id.gameIdDisplay);
        createGameButton = findViewById(R.id.createGameButton);
        joinGameButton = findViewById(R.id.joinGameButton);

        symbolGroup.check(R.id.onlineRadioX);
        if (gamesGroup != null) {
            gamesGroup.check(R.id.onlineRadio1);
        }

        symbolGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = findViewById(checkedId);
            if (selected != null) {
                playerSymbol = selected.getText().toString();
            }
        });

        if (gamesGroup != null) {
            gamesGroup.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton selected = findViewById(checkedId);
                if (selected != null) {
                    try {
                        numberOfGames = Integer.parseInt(selected.getText().toString());
                    } catch (NumberFormatException e) {
                        numberOfGames = 1;
                    }
                }
            });
        }

        createGameButton.setOnClickListener(v -> {
            Log.d(TAG, "Create game button clicked");
            createGame();
        });

        joinGameButton.setOnClickListener(v -> {
            Log.d(TAG, "Join game button clicked");
            joinGame();
        });
    }

    private void createGame() {
        Log.d(TAG, "createGame() called");

        if (gameService == null) {
            Log.e(TAG, "gameService is null in createGame()");
            Toast.makeText(this, "Erreur: Service non disponible", Toast.LENGTH_LONG).show();
            return;
        }

        if (!gameService.isInitialized() && !gameService.ensureInitialized()) {
            Toast.makeText(this, "Erreur: Service non disponible", Toast.LENGTH_LONG).show();
            return;
        }

        createGameButton.setEnabled(false);
        joinGameButton.setEnabled(false);

        Toast.makeText(this, "Création de la partie...", Toast.LENGTH_SHORT).show();

        String gameId = gameService.createGame(playerSymbol, numberOfGames, new OnlineGameService.GameStateListener() {
            @Override
            public void onGameReady(GameRoom room) {
                Log.d(TAG, "onGameReady called - Game ID: " + (room != null ? room.getGameId() : "null"));
                runOnUiThread(() -> {
                    if (room != null) {
                        currentGameId = room.getGameId();
                        startOnlineGame(room, true);
                    } else {
                        Log.e(TAG, "GameRoom is null in onGameReady");
                        Toast.makeText(OnlineGameActivity.this, "Erreur: Données de partie invalides", Toast.LENGTH_LONG).show();
                        createGameButton.setEnabled(true);
                        joinGameButton.setEnabled(true);
                    }
                });
            }

            @Override
            public void onGameStateChanged(GameRoom room) {
                // Not used in create flow
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error in createGame: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(OnlineGameActivity.this, "Erreur: " + error, Toast.LENGTH_LONG).show();
                    createGameButton.setEnabled(true);
                    joinGameButton.setEnabled(true);
                });
            }
        });

        if (gameId != null && !gameId.isEmpty()) {
            currentGameId = gameId;
            gameIdDisplay.setText("Game ID: " + gameId);
            gameIdDisplay.setVisibility(View.VISIBLE);
            Toast.makeText(this, "En attente d'un autre joueur...", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Erreur lors de la création de la partie", Toast.LENGTH_SHORT).show();
            createGameButton.setEnabled(true);
            joinGameButton.setEnabled(true);
        }
    }

    private void joinGame() {
        Log.d(TAG, "joinGame() called");

        String gameId = gameIdEditText.getText().toString().trim().toUpperCase();

        Log.d(TAG, "Game ID from EditText: '" + gameId + "'");

        if (gameId.isEmpty()) {
            Log.w(TAG, "Game ID is empty");
            Toast.makeText(this, "Veuillez entrer un ID de partie", Toast.LENGTH_SHORT).show();
            return;
        }

        if (gameId.length() != 8) {
            Log.w(TAG, "Game ID length is " + gameId.length() + ", expected 8");
            Toast.makeText(this, "L'ID doit contenir 8 caractères", Toast.LENGTH_SHORT).show();
            return;
        }

        if (gameService == null) {
            Log.e(TAG, "gameService is null in joinGame()");
            Toast.makeText(this, "Erreur: Service non disponible", Toast.LENGTH_LONG).show();
            return;
        }

        if (!gameService.isInitialized() && !gameService.ensureInitialized()) {
            Toast.makeText(this, "Erreur: Service non disponible", Toast.LENGTH_LONG).show();
            return;
        }

        createGameButton.setEnabled(false);
        joinGameButton.setEnabled(false);

        Toast.makeText(this, "Connexion à la partie...", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "About to call gameService.joinGame() with ID: " + gameId);
        Log.d(TAG, "gameService class: " + gameService.getClass().getName());
        Log.d(TAG, "playerSymbol: " + playerSymbol);

        try {
            Log.d(TAG, "Invoking joinGame method NOW...");
            gameService.joinGame(gameId, playerSymbol, new OnlineGameService.GameStateListener() {
                @Override
                public void onGameReady(GameRoom room) {
                    Log.d(TAG, "onGameReady called for join - Game ID: " + (room != null ? room.getGameId() : "null"));
                    runOnUiThread(() -> {
                        if (room != null) {
                            currentGameId = room.getGameId();
                            numberOfGames = room.getTotalGames();
                            Log.d(TAG, "Successfully joined game. Starting game activity...");
                            startOnlineGame(room, false);
                        } else {
                            Log.e(TAG, "GameRoom is null in onGameReady (join)");
                            Toast.makeText(OnlineGameActivity.this, "Erreur: Données de partie invalides", Toast.LENGTH_LONG).show();
                            createGameButton.setEnabled(true);
                            joinGameButton.setEnabled(true);
                        }
                    });
                }

                @Override
                public void onGameStateChanged(GameRoom room) {
                    // Not used in join flow
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error in joinGame: " + error);
                    runOnUiThread(() -> {
                        Toast.makeText(OnlineGameActivity.this, "Erreur: " + error, Toast.LENGTH_LONG).show();
                        createGameButton.setEnabled(true);
                        joinGameButton.setEnabled(true);
                    });
                }
            });
            Log.d(TAG, "joinGame method call completed");
        } catch (Exception e) {
            Log.e(TAG, "EXCEPTION calling joinGame: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
            createGameButton.setEnabled(true);
            joinGameButton.setEnabled(true);
        }
    }

    private void startOnlineGame(GameRoom room, boolean isHost) {
        Intent intent = new Intent(OnlineGameActivity.this, GameActivity.class);
        intent.putExtra("ONLINE_MODE", true);
        intent.putExtra("GAME_ID", currentGameId);
        intent.putExtra("PLAYER_SYMBOL", playerSymbol);
        intent.putExtra("NUMBER_OF_GAMES", numberOfGames);
        intent.putExtra("IS_HOST", isHost);
        intent.putExtra("PLAYER1_SYMBOL", room.getPlayer1Symbol());
        intent.putExtra("PLAYER2_SYMBOL", room.getPlayer2Symbol());
        startActivity(intent);
        finish();
    }
}
