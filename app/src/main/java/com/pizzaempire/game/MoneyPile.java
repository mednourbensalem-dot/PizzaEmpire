package com.pizzaempire.game;

import android.graphics.Canvas;
import android.graphics.Paint;

/** Cash left by customers. The player collects it by walking over it. */
public class MoneyPile extends Entity {
    public static final float CX = 1.5f, CY = 7.5f;

    public MoneyPile(World w) { super(w, CX, CY); }

    @Override
    public void draw(Canvas c, Paint p, Iso iso) {
        int amount = w.state.pile;
        float u = iso.u();
        if (amount <= 0) {
            iso.quad(c, p, x - 0.5f, y - 0.5f, x + 0.5f, y + 0.5f, 0f, 0x5543A047);
            return;
        }
        int layers = Math.min(8, 1 + amount / 25);
        float h = layers * 0.13f;
        iso.box(c, p, x - 0.5f, y - 0.5f, x + 0.5f, y + 0.5f, h, 0xFF76FF03, 0xFF64DD17, 0xFF558B2F);
        iso.text(c, p, "$" + amount, iso.sx(x, y), iso.sy(x, y) - (h + 0.35f) * u, 0.45f * u, 0xFFFFFFFF, true);
    }
}
