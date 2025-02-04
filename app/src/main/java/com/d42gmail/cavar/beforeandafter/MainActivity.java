package com.d42gmail.cavar.beforeandafter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.d42gmail.cavar.beforeandafter.custom_view.BeforeAndAfterView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BeforeAndAfterView beforeAndAfterView = findViewById(R.id.beforeAndAfterView);
        beforeAndAfterView.loadImagesByUrl("https://wallspics.nyc3.digitaloceanspaces.com/transforms/styles/53515025c7434ec8b75acc05482d48b2.png",
                "https://wallspics.nyc3.digitaloceanspaces.com/transforms/styles/1460fe20097383782f6e4b9959ff41e9.jpg");
        beforeAndAfterView.setRoundCorners(true);
    }
}