package com.example.demouser.scarnes_dice;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class LoseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose);

        ((Button) findViewById(R.id.reset_button)).setOnClickListener(new View
                .OnClickListener() {


            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
