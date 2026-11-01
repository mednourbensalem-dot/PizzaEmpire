package com.pizzaempire.game;

import android.graphics.Canvas;
import android.graphics.Paint;

/** Anything that lives in the world: it updates every frame and draws itself. */
public abstract class Entity {
    public final World w;
    public float x, y;           // tile coordinates
    public boolean dead;         // true = the world removes it

    protected Entity(World w, float x, float y) { this.w = w; this.x = x; this.y = y; }

    /** Drawing order: things further down the screen are drawn later (in front). */
    public float depth() { return x + y; }

    public void update(float dt) {}

    public abstract void draw(Canvas c, Paint p, Iso iso);
}
