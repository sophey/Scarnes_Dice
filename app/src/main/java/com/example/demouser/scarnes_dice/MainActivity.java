package com.example.demouser.scarnes_dice;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public static final String USER_SCORE = "com.example.demouser.scarnes_dice";
    final Handler timerHandler = new Handler();
    private final int WINNING_SCORE = 50;
    private boolean player1Turn;
    private int player1Score;
    private int player2Score;
    private int turnScore;
    private int rollCount;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUserEmail = mFirebaseUser.getEmail();
        }

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
        ((ImageView) findViewById(R.id.diceView)).setContentDescription
                (String.format(getString(R.string.dice), roll));

        if (roll == 1) {
            player1Turn = !player1Turn;
            ((TextView) findViewById(R.id.infoText)).setText(String.format
                    (getString(R.string.change_players), player1Turn ? 1 : 2));
            turnScore = 0;
            rollCount = 0;
            if (!player1Turn)
                computerTurnIn500();
        } else {
            turnScore += roll;
            ((TextView) findViewById(R.id.infoText)).setText(String.format
                    (getString(R.string.keep_player), player1Turn ? 1 : 2));
        }

        ((TextView) findViewById(R.id.turnScore)).setText(turnScore + "");
    }

    private void hold() {
        if (player1Turn) {
            player1Score += turnScore;
            ((TextView) findViewById(R.id.player1Score)).setText(player1Score
                    + "");
        } else {
            player2Score += turnScore;
            ((TextView) findViewById(R.id.player2Score)).setText(player2Score
                    + "");
        }
        player1Turn = !player1Turn;
        turnScore = 0;

        ((TextView) findViewById(R.id.turnScore)).setText(turnScore + "");

        if (!player1Turn && player1Score >= WINNING_SCORE) {
            Intent intent = new Intent(this, WinActivity.class);
            intent.putExtra(USER_SCORE, String.valueOf(player1Score));
            startActivity(intent);
            reset();
//            ((TextView) findViewById(R.id.infoText)).setText(R.string
//                    .player1_win);
//            ((Button) findViewById(R.id.rollButton)).setEnabled(false);
//            ((Button) findViewById(R.id.holdButton)).setEnabled(false);
        } else if (player1Turn && player2Score >= WINNING_SCORE) {
            Intent intent = new Intent(this, LoseActivity.class);
            startActivity(intent);
            reset();
//            ((TextView) findViewById(R.id.infoText)).setText(R.string
//                    .player2_win);
//            ((Button) findViewById(R.id.rollButton)).setEnabled(false);
//            ((Button) findViewById(R.id.holdButton)).setEnabled(false);
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
        else if (WINNING_SCORE - player1Score < 10 && player1Score -
                player2Score > 5) {
            roll();
        } else if (WINNING_SCORE - (player2Score + turnScore) < 4) {
            roll();
        } else if (rollCount >= 4 || turnScore >= 12) {
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
        ((ImageView) findViewById(R.id.diceView)).setImageResource(R.drawable
                .dice1);
        ((ImageView) findViewById(R.id.diceView)).setContentDescription
                (String.format(getString(R.string.dice), 1));
        ((TextView) findViewById(R.id.infoText)).setText(R.string.player_turn);
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
