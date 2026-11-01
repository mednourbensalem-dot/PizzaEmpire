package com.pizzaempire.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        String name = Session.isLoggedIn(this) ? PlayerDb.get(this).usernameOf(Session.id(this)) : null;
        if (name == null) {                       // nobody logged in (or the account was deleted)
            Session.clear(this);
            goLogin();
            return;
        }

        setContentView(R.layout.activity_main);
        SoundManager.init(this);
        ((TextView) findViewById(R.id.tvWelcome)).setText("Welcome, " + name + "!");

        findViewById(R.id.btnPlay).setOnClickListener(
            v -> startActivity(new Intent(this, WorldActivity.class)));

        findViewById(R.id.btnNew).setOnClickListener(v -> {
            if (!SaveManager.hasSave(this)) {
                GameHolder.newGame(this);
                startActivity(new Intent(this, WorldActivity.class));
                return;
            }
            new AlertDialog.Builder(this)
                .setTitle("Start a new game?")
                .setMessage("Your current pizzeria will be erased.")
                .setPositiveButton("Yes, restart", (d, w) -> {
                    GameHolder.newGame(this);
                    startActivity(new Intent(this, WorldActivity.class));
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        findViewById(R.id.btnBoard).setOnClickListener(
            v -> startActivity(new Intent(this, LeaderboardActivity.class)));

        findViewById(R.id.btnHow).setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("How to play")
            .setMessage(getString(R.string.how_to_play))
            .setPositiveButton("Got it", null)
            .show());

        findViewById(R.id.btnSettings).setOnClickListener(
            v -> startActivity(new Intent(this, SettingsActivity.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            GameHolder.save(this);
            GameHolder.clear();
            Session.clear(this);
            goLogin();
        });
    }

    private void goLogin() {
        startActivity(new Intent(this, LoginActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
