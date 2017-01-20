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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String USER_SCORE = "com.example.demouser.scarnes_dice";
    final Handler timerHandler = new Handler();
    private final int WINNING_SCORE = 50;
//    private boolean player1Turn;
//    private int player1Score;
//    private int player2Score;
//    private int turnScore;
//    private int rollCount;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserEmail;
    private DatabaseReference mFirebaseDatabase;
    private PlayerState state;
    private ScarnesDiceGame game;


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
            mUserEmail = ScarnesDiceGame.sanitizeEmail(mFirebaseUser.getEmail
                    ());
        }

        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.rollButton)).setOnClickListener(new View
                .OnClickListener() {


            @Override
            public void onClick(View v) {
                roll();
            }
        });

        configureDatabase();

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

    }

    private void configureDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        mFirebaseDatabase.child("players").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PlayerState newState = dataSnapshot.getValue(PlayerState.class);
                // if different players
                if (!newState.getEmail().equals(state.getEmail()) &&
                        newState.getStatus() == PlayerStatus.READY &&
                        state.getStatus() == PlayerStatus.READY) {
//                    state.setStatus(PlayerStatus.IN_GAME);
//                    newState.setStatus(PlayerStatus.IN_GAME);
//                    mFirebaseDatabase.child("players").child(state.getId())
//                            .setValue(state);
//                    mFirebaseDatabase.child("players").child(newState.getId()
//                    ).setValue(newState);

                    startGame(newState);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        state = new PlayerState(mUserEmail);
        mFirebaseDatabase.child("players").child(state.getId()).setValue(state);
    }

    private void startGame(PlayerState newState) {
        Random random = new Random();

        game = new ScarnesDiceGame(state.getEmail(), newState.getEmail(),
                random.nextBoolean());

        mFirebaseDatabase.child("games").child(game.getId()).setValue(game);

        state.setStatus(PlayerStatus.IN_GAME);
        newState.setStatus(PlayerStatus.IN_GAME);

        mFirebaseDatabase.child("players").child(state.getId()).setValue(state);
        mFirebaseDatabase.child("players").child(newState.getId()).setValue
                (newState);

        mFirebaseDatabase.child("games").child(game.getId())
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(ScarnesDiceGame.class) !=
                                null)
                            game = dataSnapshot.getValue(ScarnesDiceGame.class);
                        updateGameView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void updateGameView() {
        int roll = game.getLastRoll();
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

        TextView infoText = ((TextView) findViewById(R.id.infoText));

        if (roll == 1) {
            infoText.setText(String.format(getString(R.string.change_players)
                    , game.isPlayer1Turn() ? 1 : 2));
        } else if (isPlayerTurn()) {
            infoText.setText("It's your turn.");
        } else {
            infoText.setText("It's the other player's turn");
        }

        Class c = null;

        if (game.getPlayer1Score() >= WINNING_SCORE) {
            c = game.getPlayer1().equals(mUserEmail) ?
                    WinActivity.class : LoseActivity.class;
        } else if (game.getPlayer2Score() >= WINNING_SCORE) {
            c = game.getPlayer2().equals(mUserEmail) ?
                    WinActivity.class : LoseActivity.class;
        }

        if (c != null) {
            startActivity(new Intent(this, c));
        }

        ((ImageView) findViewById(R.id.diceView)).setImageResource(die);
        ((ImageView) findViewById(R.id.diceView)).setContentDescription
                (String.format(getString(R.string.dice), roll));
        ((TextView) findViewById(R.id.player1Score)).setText(String.valueOf
                (game.getPlayer1Score()));
        ((TextView) findViewById(R.id.player2Score)).setText(String.valueOf
                (game.getPlayer2Score()));
        ((TextView) findViewById(R.id.turnScore)).setText(String.valueOf(game
                .getTurnScore()));
    }

    private boolean isPlayerTurn() {
        String email = game.isPlayer1Turn() ? game.getPlayer1() : game
                .getPlayer2();
        return email.equals(mUserEmail);
    }


    private void changeTurn() {
        game.setTurnScore(0);
        game.setPlayer1Turn(!game.isPlayer1Turn());
    }

    private void roll() {
        int roll = (int) (Math.random() * 6) + 1;

        game.setLastRoll(roll);

        if (roll == 1) {
            game.setTurnScore(0);
            changeTurn();
        } else {
            game.setTurnScore(game.getTurnScore() + roll);
        }

        mFirebaseDatabase.child("games").child(game.getId()).setValue(game);
    }

    private void hold() {
        if (game.isPlayer1Turn()) {
            game.setPlayer1Score(game.getPlayer1Score() + game.getTurnScore());
        } else {
            game.setPlayer2Score(game.getPlayer2Score() + game.getTurnScore());
        }
        changeTurn();

        mFirebaseDatabase.child("games").child(game.getId()).setValue(game);
    }

    private void reset() {
        game = new ScarnesDiceGame(game.getPlayer1(), game.getPlayer2(), (new
                Random()).nextBoolean());
        mFirebaseDatabase.child("games").child(game.getId()).setValue(game);

        ((Button) findViewById(R.id.rollButton)).setEnabled(true);
        ((Button) findViewById(R.id.holdButton)).setEnabled(true);

        ((ImageView) findViewById(R.id.diceView)).setImageResource(R.drawable
                .dice1);
        ((ImageView) findViewById(R.id.diceView)).setContentDescription
                (String.format(getString(R.string.dice), 1));
        ((TextView) findViewById(R.id.infoText)).setText(R.string.player_turn);
    }

}
