package com.pizzaempire.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShopActivity extends Activity {

    private GameState state;
    private LinearLayout list;
    private TextView tvMoney, tvGems;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_shop);
        SoundManager.init(this);
        state = GameHolder.state(this);
        list = findViewById(R.id.upgradeList);
        tvMoney = findViewById(R.id.tvMoney);
        tvGems = findViewById(R.id.tvGems);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        render();
    }

    private void render() {
        tvMoney.setText("💵 " + state.money);
        tvGems.setText("💎 " + state.gems);
        list.removeAllViews();
        LayoutInflater inf = LayoutInflater.from(this);

        for (final Upgrade u : Upgrade.catalog(state)) {
            View row = inf.inflate(R.layout.item_upgrade, list, false);
            ((TextView) row.findViewById(R.id.upTitle)).setText(u.title);
            ((TextView) row.findViewById(R.id.upDesc)).setText(u.desc);
            ((TextView) row.findViewById(R.id.upLevel)).setText(u.level);
            Button buy = row.findViewById(R.id.upBuy);

            if (u.maxed) {
                buy.setText("MAX");
                buy.setEnabled(false);
            } else {
                buy.setText(u.usesGems ? u.cost + " 💎" : "$" + u.cost);
                buy.setEnabled(u.canAfford(state));
                buy.setOnClickListener(v -> {
                    if (!u.canAfford(state)) { SoundManager.play(SoundManager.ERROR); return; }
                    u.pay(state);
                    u.apply.run();
                    GameHolder.save(this);
                    SoundManager.play(SoundManager.BUY);
                    render();
                });
            }
            list.addView(row);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        GameHolder.save(this);
    }
}
