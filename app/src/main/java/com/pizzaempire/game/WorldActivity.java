package com.pizzaempire.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/** Hosts the GameView (the world) and the XML HUD that sits on top of it. */
public class WorldActivity extends Activity {

    private GameState state;
    private World world;
    private GameView view;
    private TextView tvMoney, tvGems;
    private long lastMoney = -1;
    private int ticks;
    private boolean tutorialShowing;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable hud = new Runnable() {
        @Override public void run() {
            refreshHud();
            if (++ticks % 60 == 0) GameHolder.save(WorldActivity.this);   // autosave about every 9 s
            handler.postDelayed(this, 150);
        }
    };

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_world);
        SoundManager.init(this);

        state = GameHolder.state(this);
        world = GameHolder.world(this);

        view = findViewById(R.id.gameView);
        view.setWorld(world);
        tvMoney = findViewById(R.id.tvMoney);
        tvGems = findViewById(R.id.tvGems);

        findViewById(R.id.btnShop).setOnClickListener(
            v -> startActivity(new Intent(this, ShopActivity.class)));
        findViewById(R.id.btnSettings).setOnClickListener(
            v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoundManager.init(this);
        state = GameHolder.state(this);
        world = GameHolder.world(this);     // a new world exists after "New game" in settings
        view.setWorld(world);
        world.sync();                       // spawn anything bought in the shop
        lastMoney = state.money;
        handler.post(hud);

        if (!tutorialShowing && !SaveManager.isTutorialSeen(this)) showTutorial();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(hud);
        GameHolder.save(this);
    }

    private void refreshHud() {
        tvMoney.setText("💵 " + state.money);
        tvGems.setText("💎 " + state.gems);
        if (lastMoney >= 0 && state.money > lastMoney) {
            tvMoney.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pop));
        }
        lastMoney = state.money;
    }

    private void showTutorial() {
        tutorialShowing = true;
        new AlertDialog.Builder(this)
            .setTitle("Welcome to Pizza Empire! 🍕")
            .setMessage(getString(R.string.how_to_play))
            .setCancelable(false)
            .setPositiveButton("Let's go!", (d, w) -> {
                SaveManager.setTutorialSeen(this, true);
                tutorialShowing = false;
            })
            .show();
    }
}
