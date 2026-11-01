package com.pizzaempire.game;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Oven extends Entity {
    public static final int CAP = 8;
    public int stock;
    private float timer;

    public Oven(World w, float x, float y) { super(w, x, y); }

    /** Where a carrier must stand to pick pizzas up. */
    public float px() { return x; }
    public float py() { return y + 1.7f; }

    @Override
    public void update(float dt) {
        if (stock >= CAP) return;
        timer += dt;
        if (timer >= w.state.ovenInterval()) { timer = 0; stock++; }
    }

    @Override
    public void draw(Canvas c, Paint p, Iso iso) {
        float u = iso.u();
        // tray in front of the oven holding the finished pizzas
        iso.box(c, p, x - 0.6f, y + 0.75f, x + 0.6f, y + 1.35f, 0.06f, 0xFF37474F, 0xFF263238, 0xFF263238);
        iso.pizzaStack(c, p, iso.sx(x, y + 1.05f), iso.sy(x, y + 1.05f) - 0.06f * u, stock, 1f);

        // base
        iso.box(c, p, x - 0.75f, y - 0.65f, x + 0.75f, y + 0.65f, 0.7f, 0xFF42A5F5, 0xFF1E88E5, 0xFF1565C0);

        // dome, mouth and chimney
        float cx = iso.sx(x, y), cy = iso.sy(x, y) - 0.7f * u;
        p.setStyle(Paint.Style.FILL);
        p.setColor(0xFFE0A030);
        c.drawOval(cx - 0.62f * u, cy - 0.95f * u, cx + 0.62f * u, cy + 0.15f * u, p);
        p.setColor(0xFFC62828);
        c.drawRect(cx - 0.1f * u, cy - 1.3f * u, cx + 0.1f * u, cy - 0.85f * u, p);
        p.setColor(0xFF3E2723);
        c.drawOval(cx - 0.22f * u, cy - 0.2f * u, cx + 0.22f * u, cy + 0.1f * u, p);
        p.setColor(0xFFFF9800);
        c.drawCircle(cx, cy - 0.02f * u, 0.07f * u, p);

        // smoke puffs
        for (int k = 0; k < 3; k++) {
            float t = (iso.time * 0.6f + k / 3f) % 1f;
            int alpha = (int) ((1f - t) * 110);
            p.setColor((alpha << 24) | 0x00757575);
            c.drawCircle(cx + 0.1f * u * (float) Math.sin(t * 6f + k), cy - 1.35f * u - t * 0.9f * u,
                    (0.1f + 0.12f * t) * u, p);
        }
    }
}
