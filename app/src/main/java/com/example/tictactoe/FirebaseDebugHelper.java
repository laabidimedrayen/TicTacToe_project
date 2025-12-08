package com.example.tictactoe;

import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDebugHelper {
    private static final String TAG = "FirebaseDebug";
    
    public static void testConnection() {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("test");
            
            Log.d(TAG, "Firebase instance created");
            Log.d(TAG, "Database URL: " + database.getReference().toString());
            
            // Test write
            ref.setValue("connection_test", new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    if (error != null) {
                        Log.e(TAG, "Firebase write failed: " + error.getMessage());
                        Log.e(TAG, "Error code: " + error.getCode());
                        Log.e(TAG, "Error details: " + error.getDetails());
                    } else {
                        Log.d(TAG, "Firebase write successful - connection works!");
                    }
                }
            });
            
            // Test read
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.d(TAG, "Firebase read successful - connection works!");
                    Log.d(TAG, "Test value: " + snapshot.getValue());
                }
                
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e(TAG, "Firebase read cancelled: " + error.getMessage());
                    Log.e(TAG, "Error code: " + error.getCode());
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void checkDatabaseRules() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("games");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG, "Database rules check: Can read games node");
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Database rules check FAILED: " + error.getMessage());
                Log.e(TAG, "This might be a permissions issue. Check Firebase console rules.");
            }
        });
    }
}

