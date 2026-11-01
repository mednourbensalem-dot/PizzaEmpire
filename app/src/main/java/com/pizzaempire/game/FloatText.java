package com.pizzaempire.game;

import android.graphics.Canvas;
import android.graphics.Paint;

/** Text that drifts upward and fades out ("+$30", "+1 gem"). */
public class FloatText extends Entity {
    private static final float LIFE = 1.2f;
    private final String text;
    private final int color;
    private float age;

    public FloatText(World w, float x, float y, String text, int color) {
        super(w, x, y);
        this.text = text; this.color = color;
    }

    @Override public float depth() { return 1000f; }   // always on top

    @Override
    public void update(float dt) {
        age += dt;
        if (age > LIFE) dead = true;
    }

    @Override
    public void draw(Canvas c, Paint p, Iso iso) {
        float u = iso.u();
        int alpha = (int) (255 * Math.min(1f, (LIFE - age) * 2.5f));
        int col = (alpha << 24) | (color & 0x00FFFFFF);
        iso.text(c, p, text, iso.sx(x, y), iso.sy(x, y) - u * (1.7f + age * 0.9f), 0.5f * u, col, true);
    }
}
