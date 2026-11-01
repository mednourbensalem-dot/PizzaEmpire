package com.pizzaempire.game;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Table extends Entity {
    private final float x0, y0;
    private final int firstSeat;

    public Table(World w, float x0, float y0, int firstSeat) {
        super(w, x0 + 0.6f, y0 + 0.6f);
        this.x0 = x0; this.y0 = y0; this.firstSeat = firstSeat;
    }

    @Override
    public void draw(Canvas c, Paint p, Iso iso) {
        for (int k = 0; k < 4; k++) {
            float sx = World.SEATS[firstSeat + k][0], sy = World.SEATS[firstSeat + k][1];
            iso.box(c, p, sx - 0.22f, sy - 0.22f, sx + 0.22f, sy + 0.22f, 0.35f,
                    0xFFAB47BC, 0xFF8E24AA, 0xFF7B1FA2);
        }
        iso.box(c, p, x0, y0, x0 + 1.2f, y0 + 1.2f, 0.5f, 0xFFFFB300, 0xFFFB8C00, 0xFFEF6C00);
    }
}
