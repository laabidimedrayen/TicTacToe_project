package com.example.tictactoe;

import java.io.Serializable;
import java.util.ArrayList;

public class GameData implements Serializable {
    private String winner; // "X", "O", ou "Draw"
    private int gameNumber;

    public GameData(String winner, int gameNumber) {
        this.winner = winner;
        this.gameNumber = gameNumber;
    }

    public String getWinner() { return winner; }
    public int getGameNumber() { return gameNumber; }
}
