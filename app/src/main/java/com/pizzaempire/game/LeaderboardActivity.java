package com.pizzaempire.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class LeaderboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_leaderboard);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        GameHolder.save(this);                       // make sure your own numbers are up to date
        PlayerDb db = PlayerDb.get(this);
        String me = db.usernameOf(Session.id(this));

        LinearLayout list = findViewById(R.id.rankList);
        LayoutInflater inf = LayoutInflater.from(this);
        List<String[]> rows = db.topPlayers(10);

        for (int i = 0; i < rows.size(); i++) {
            String[] r = rows.get(i);
            View row = inf.inflate(R.layout.item_rank, list, false);
            ((TextView) row.findViewById(R.id.rankNum)).setText("#" + (i + 1));
            ((TextView) row.findViewById(R.id.rankName)).setText(r[0]);
            ((TextView) row.findViewById(R.id.rankStats)).setText("🍕 " + r[1] + "   💵 " + r[2]);
            if (r[0].equalsIgnoreCase(me)) row.setBackgroundResource(R.drawable.bg_card_ready);
            list.addView(row);
        }
    }
}
