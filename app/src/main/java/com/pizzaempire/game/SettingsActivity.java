package com.pizzaempire.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_settings);

        Switch sound = findViewById(R.id.swSound);
        sound.setChecked(SaveManager.isSoundOn(this));
        sound.setOnCheckedChangeListener((btn, on) -> {
            SaveManager.setSoundOn(this, on);
            SoundManager.setEnabled(on);
            if (on) SoundManager.play(SoundManager.BUY);
        });

        findViewById(R.id.btnReplay).setOnClickListener(v -> {
            SaveManager.setTutorialSeen(this, false);
            Toast.makeText(this, "The tutorial will show next time you open the shop floor",
                    Toast.LENGTH_LONG).show();
        });

        findViewById(R.id.btnReset).setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("Reset progress?")
            .setMessage("All money, gems and upgrades of this player will be erased.")
            .setPositiveButton("Reset", (d, w) -> {
                GameHolder.newGame(this);
                Toast.makeText(this, "Progress reset", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show());

        findViewById(R.id.btnDelete).setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("Delete your account?")
            .setMessage("Your account and all its progress will be erased for good.")
            .setPositiveButton("Delete", (d, w) -> {
                PlayerDb.get(this).deletePlayer(Session.id(this));
                GameHolder.clear();
                Session.clear(this);
                startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            })
            .setNegativeButton("Cancel", null)
            .show());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
