package com.example.tictactoe;

import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class OnlineGameService {
    private static final String TAG = "OnlineGameService";
    private DatabaseReference database;
    private static OnlineGameService instance;
    private ValueEventListener currentGameListener;

    private OnlineGameService() {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            if (firebaseDatabase == null) {
                Log.e(TAG, "FirebaseDatabase.getInstance() returned null!");
                return;
            }
            database = firebaseDatabase.getReference();
            if (database == null) {
                Log.e(TAG, "firebaseDatabase.getReference() returned null!");
                return;
            }
            Log.d(TAG, "Firebase database initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase: " + e.getMessage(), e);
        }
    }

    public static synchronized OnlineGameService getInstance() {
        if (instance == null) {
            instance = new OnlineGameService();
        }
        return instance;
    }

    public String createGame(String playerSymbol, int numberOfGames, GameStateListener listener) {
        if (database == null) {
            Log.e(TAG, "Database is null, cannot create game");
            if (listener != null) {
                listener.onError("Firebase non initialisé");
            }
            return null;
        }

        String gameId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Log.d(TAG, "Creating game with ID: " + gameId);

        GameRoom gameRoom = new GameRoom(gameId, playerSymbol, "waiting", numberOfGames);

        DatabaseReference gameRef = database.child("games").child(gameId);

        gameRef.setValue(gameRoom, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {
                    Log.e(TAG, "Error creating game: " + error.getMessage());
                    if (listener != null) {
                        listener.onError("Erreur Firebase: " + error.getMessage());
                    }
                } else {
                    Log.d(TAG, "Game created successfully in Firebase");
                    // Setup listener after successful creation
                    setupGameCreatorListener(gameId, listener);
                }
            }
        });

        return gameId;
    }

    private void setupGameCreatorListener(String gameId, GameStateListener listener) {
        database.child("games").child(gameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GameRoom room = snapshot.getValue(GameRoom.class);
                    if (room != null) {
                        if ("ready".equals(room.getStatus())) {
                            listener.onGameReady(room);
                        }
                        listener.onGameStateChanged(room);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Game listener cancelled: " + error.getMessage());
                listener.onError(error.getMessage());
            }
        });
    }

    public void joinGame(String gameId, String playerSymbol, GameStateListener listener) {
        try {
            Log.d(TAG, "=== joinGame() ENTRY POINT ===");
            Log.d(TAG, "Game ID: " + gameId);
            Log.d(TAG, "Player Symbol: " + playerSymbol);

            if (database == null) {
                Log.e(TAG, "CRITICAL ERROR: database is null! Firebase not initialized.");
                if (listener != null) {
                    listener.onError("Erreur: Firebase non initialisé. Redémarrez l'app.");
                }
                return;
            }

            if (gameId == null || gameId.trim().isEmpty()) {
                Log.e(TAG, "Invalid game ID");
                if (listener != null) {
                    listener.onError("ID de partie invalide");
                }
                return;
            }

            Log.d(TAG, "Attempting to join game: " + gameId);
            DatabaseReference gameRef = database.child("games").child(gameId.trim().toUpperCase());

            gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "=== onDataChange called ===");
                    Log.d(TAG, "Snapshot exists: " + snapshot.exists());

                    if (!snapshot.exists()) {
                        Log.e(TAG, "Game not found: " + gameId);
                        listener.onError("Partie non trouvée. Vérifiez l'ID.");
                        return;
                    }

                    GameRoom room = snapshot.getValue(GameRoom.class);
                    if (room == null) {
                        Log.e(TAG, "Failed to parse GameRoom from snapshot");
                        listener.onError("Données de partie invalides");
                        return;
                    }

                    Log.d(TAG, "Game room found. Status: " + room.getStatus());

                    if ("ready".equals(room.getStatus()) || "playing".equals(room.getStatus())) {
                        Log.e(TAG, "Game already started");
                        listener.onError("La partie a déjà commencé");
                        return;
                    }

                    // Set second player and update status
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("player2Symbol", playerSymbol);
                    updates.put("status", "ready");

                    Log.d(TAG, "Updating game room with player 2");

                    gameRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                Log.e(TAG, "Error updating game room: " + error.getMessage());
                                listener.onError("Erreur lors de la connexion: " + error.getMessage());
                            } else {
                                Log.d(TAG, "Game room updated successfully");
                                room.setPlayer2Symbol(playerSymbol);
                                room.setStatus("ready");
                                listener.onGameReady(room);
                                setupGameListener(gameId, listener);
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "=== onCancelled called ===");
                    Log.e(TAG, "Error: " + error.getMessage());
                    if (listener != null) {
                        listener.onError("Erreur Firebase: " + error.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in joinGame: " + e.getMessage(), e);
            if (listener != null) {
                listener.onError("Erreur: " + e.getMessage());
            }
        }
    }

    public void setupGameListener(String gameId, GameStateListener listener) {
        if (database == null || gameId == null) {
            return;
        }

        currentGameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GameRoom room = snapshot.getValue(GameRoom.class);
                    if (room != null) {
                        listener.onGameStateChanged(room);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Game listener cancelled: " + error.getMessage());
                listener.onError(error.getMessage());
            }
        };

        database.child("games").child(gameId).addValueEventListener(currentGameListener);
    }

    public void makeMove(String gameId, int row, int col, String symbol) {
        if (database == null || gameId == null) {
            return;
        }

        DatabaseReference gameRef = database.child("games").child(gameId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("board/" + row + "_" + col, symbol);
        updates.put("currentTurn", symbol.equals("X") ? "O" : "X");
        updates.put("status", "playing");

        gameRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {
                    Log.e(TAG, "Error making move: " + error.getMessage());
                }
            }
        });
    }

    public void updateGameStatus(String gameId, String status) {
        if (database != null && gameId != null) {
            database.child("games").child(gameId).child("status").setValue(status);
        }
    }

    public void updateWinner(String gameId, String winner) {
        if (database != null && gameId != null) {
            database.child("games").child(gameId).child("winner").setValue(winner);
        }
    }

    public void resetGame(String gameId) {
        if (database == null || gameId == null) {
            return;
        }

        DatabaseReference gameRef = database.child("games").child(gameId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("board", null);
        updates.put("currentTurn", "X");
        updates.put("winner", "");

        gameRef.updateChildren(updates);
    }

    public void nextGame(String gameId, int currentGame) {
        if (database == null || gameId == null) {
            return;
        }

        DatabaseReference gameRef = database.child("games").child(gameId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("board", null);
        updates.put("currentTurn", "X");
        updates.put("winner", "");
        updates.put("currentGame", currentGame);
        updates.put("status", "ready");

        gameRef.updateChildren(updates);
    }

    public void updateTournamentScores(String gameId, int scoreX, int scoreO, int draws) {
        if (database == null || gameId == null) {
            return;
        }

        DatabaseReference gameRef = database.child("games").child(gameId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("scoreX", scoreX);
        updates.put("scoreO", scoreO);
        updates.put("draws", draws);

        gameRef.updateChildren(updates);
    }

    public void removeGameListener(String gameId) {
        if (database != null && currentGameListener != null && gameId != null) {
            database.child("games").child(gameId).removeEventListener(currentGameListener);
            currentGameListener = null;
        }
    }

    public void leaveGame(String gameId) {
        if (database != null && gameId != null) {
            removeGameListener(gameId);
            database.child("games").child(gameId).removeValue();
        }
    }

    public interface GameStateListener {
        void onGameReady(GameRoom room);
        void onGameStateChanged(GameRoom room);
        void onError(String error);
    }
}
