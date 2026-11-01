package com.pizzaempire.game;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

public class Player extends Carrier {
    private DashPathEffect dash;
    private float dashU;

    public Player(World w) { super(w, 4.5f, 4.6f, 0xFF1E88E5, 0xFFE53935); }

    @Override public int capacity() { return w.state.capacity(); }
    @Override protected float speed() { return w.state.walkSpeed(); }

    @Override
    public void draw(Canvas c, Paint p, Iso iso) {
        float u = iso.u();
        if (dash == null || dashU != u) {
            dash = new DashPathEffect(new float[]{u * 0.12f, u * 0.1f}, 0f);
            dashU = u;
        }
        float px = iso.sx(x, y), py = iso.sy(x, y);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(Math.max(2f, u * 0.05f));
        p.setColor(0xFF43A047);
        p.setPathEffect(dash);
        c.drawOval(px - 0.55f * u, py - 0.25f * u, px + 0.55f * u, py + 0.25f * u, p);
        p.setPathEffect(null);
        p.setStyle(Paint.Style.FILL);
        super.draw(c, p, iso);
    }
}
