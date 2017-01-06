package com.example.demouser.scarnes_dice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean player1Turn;
    private int player1Score;
    private int player2Score;
    private int turnScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.rollButton)).setOnClickListener(new View
                .OnClickListener() {


            @Override
            public void onClick(View v) {
                roll();
            }
        });

        ((Button) findViewById(R.id.holdButton)).setOnClickListener(new View
                .OnClickListener() {


            @Override
            public void onClick(View v) {
                hold();
            }
        });

        ((Button) findViewById(R.id.resetButton)).setOnClickListener(new View
                .OnClickListener() {


            @Override
            public void onClick(View v) {
                reset();
            }
        });

        startGame();
    }

    private void startGame() {
        player1Turn = true;
        player1Score = 0;
        player2Score = 0;
        turnScore = 0;
    }

    private void roll() {
        int roll = (int) (Math.random() * 6) + 1;
        ((TextView) findViewById(R.id.dieRoll)).setText(roll + "");

        if (roll == 1) {
            player1Turn = !player1Turn;
            ((TextView) findViewById(R.id.infoText)).setText("Rolled a 1, now" +
                    " Player " + (player1Turn ? 1 : 2) + "'s turn.");
            turnScore = 0;
        } else {
            turnScore += roll;
            ((TextView) findViewById(R.id.infoText)).setText("Still Player "
                    + (player1Turn ? 1 : 2) + "'s turn.");
        }

        ((TextView) findViewById(R.id.turnScore)).setText(turnScore + "");
    }

    private void hold() {
        if (player1Turn) {
            player1Score += turnScore;
            ((TextView) findViewById(R.id.player1Score)).setText(player1Score + "");
        } else {
            player2Score += turnScore;
            ((TextView) findViewById(R.id.player2Score)).setText(player2Score + "");
        }
        player1Turn = !player1Turn;
        turnScore = 0;

        ((TextView) findViewById(R.id.turnScore)).setText(turnScore + "");
        ((TextView) findViewById(R.id.infoText)).setText("Held, now Player "
                + (player1Turn ? 1 : 2) + "'s turn.");
    }

    private void reset() {
        startGame();
        ((TextView) findViewById(R.id.turnScore)).setText(turnScore + "");
        ((TextView) findViewById(R.id.player1Score)).setText(player1Score + "");
        ((TextView) findViewById(R.id.player2Score)).setText(player2Score + "");
        ((TextView) findViewById(R.id.dieRoll)).setText("0");
        ((TextView) findViewById(R.id.infoText)).setText("Player 1's Turn");
    }
}
