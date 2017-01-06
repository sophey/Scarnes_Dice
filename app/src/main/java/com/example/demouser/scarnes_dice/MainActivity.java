package com.example.demouser.scarnes_dice;

import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final int WINNING_SCORE = 50;
    private boolean player1Turn;
    private int player1Score;
    private int player2Score;
    private int turnScore;
    final Handler timerHandler = new Handler();
    private int rollCount;

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
        rollCount = 0;
    }

    private void roll() {
        int roll = (int) (Math.random() * 6) + 1;

        int die;

        switch (roll) {
            case 1:
                die = R.drawable.dice1;
                break;
            case 2:
                die = R.drawable.dice2;
                break;
            case 3:
                die = R.drawable.dice3;
                break;
            case 4:
                die = R.drawable.dice4;
                break;
            case 5:
                die = R.drawable.dice5;
                break;
            case 6:
                die = R.drawable.dice6;
                break;
            default:
                die = R.drawable.dice1;
                break;
        }

        ((ImageView) findViewById(R.id.diceView)).setImageResource(die);

        if (roll == 1) {
            player1Turn = !player1Turn;
            ((TextView) findViewById(R.id.infoText)).setText("Rolled a 1, now" +
                    " Player " + (player1Turn ? 1 : 2) + "'s turn.");
            turnScore = 0;
            rollCount = 0;
            if (!player1Turn)
                computerTurnIn500();
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

        if (!player1Turn && player1Score >= WINNING_SCORE) {
            ((TextView) findViewById(R.id.infoText)).setText("Player 1 Won!");
            ((Button) findViewById(R.id.rollButton)).setEnabled(false);
            ((Button) findViewById(R.id.holdButton)).setEnabled(false);
        } else if (player1Turn && player2Score >= WINNING_SCORE) {
            ((TextView) findViewById(R.id.infoText)).setText("Player 2 Won!");
            ((Button) findViewById(R.id.rollButton)).setEnabled(false);
            ((Button) findViewById(R.id.holdButton)).setEnabled(false);
        } else {
            ((TextView) findViewById(R.id.infoText)).setText("Held, now Player "
                    + (player1Turn ? 1 : 2) + "'s turn.");
            if (!player1Turn) {
                rollCount = 0;
                computerTurnIn500();
            }
        }
    }

    private void computerTurn() {
        if (player2Score + turnScore >= WINNING_SCORE)
            hold();
        else if (WINNING_SCORE - player1Score < 10 && player1Score - player2Score > 5) {
            roll();
        } else if (WINNING_SCORE - (player2Score + turnScore) < 4) {
            roll();
        }
        else if (rollCount >= 4 || turnScore >= 12) {
            hold();
        } else {
            roll();
        }
    }

    private void reset() {
        startGame();
        ((Button) findViewById(R.id.rollButton)).setEnabled(true);
        ((Button) findViewById(R.id.holdButton)).setEnabled(true);
        ((TextView) findViewById(R.id.turnScore)).setText(turnScore + "");
        ((TextView) findViewById(R.id.player1Score)).setText(player1Score + "");
        ((TextView) findViewById(R.id.player2Score)).setText(player2Score + "");
        ((ImageView) findViewById(R.id.diceView)).setImageResource(R.drawable.dice1);
        ((TextView) findViewById(R.id.infoText)).setText("Player 1's Turn");
    }

    private void computerTurnIn500() {
        timerHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                computerTurn();
                if (!player1Turn) {
                    computerTurnIn500();
                }
            }
        }, 500);
    }
}
