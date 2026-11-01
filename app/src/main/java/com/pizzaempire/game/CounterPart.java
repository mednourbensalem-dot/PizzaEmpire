package com.pizzaempire.game;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * The long counter is drawn as 4 slices. Each slice has its own depth, so a person standing
 * behind one end of the counter is correctly hidden, even though the counter is long.
 */
public class CounterPart extends Entity {
    private final Counter counter;
    private final int index;

    public CounterPart(World w, Counter counter, int index) {
        super(w, Counter.X0 + index + 0.5f, (Counter.Y0 + Counter.Y1) / 2f);
        this.counter = counter;
        this.index = index;
    }

    @Override
    public void draw(Canvas c, Paint p, Iso iso) {
        float u = iso.u();
        float x0 = Counter.X0 + index, x1 = x0 + 1f;
        iso.box(c, p, x0, Counter.Y0, x1, Counter.Y1, 0.9f, 0xFFBA68C8, 0xFFF5F5F5, 0xFFE0E0E0);

        if (index == 1 && counter.stock > 0) {
            float px = iso.sx(x - 0.5f, y), py = iso.sy(x - 0.5f, y) - 0.9f * u;
            iso.pizzaStack(c, p, px, py, counter.stock, 1f);
            iso.text(c, p, "x" + counter.stock, px, py - (Math.min(counter.stock, 14) * 0.09f + 0.4f) * u,
                    0.4f * u, 0xFFFFFFFF, true);
        }
        if (index == 3) {   // cash register
            iso.boxZ(c, p, 5.8f, 7.25f, 6.3f, 7.75f, 0.9f, 0.35f, 0xFF455A64, 0xFF37474F, 0xFF263238);
        }
    }
}
