package com.pizzaempire.game;

import android.graphics.Canvas;
import android.graphics.Paint;

/** A person who can walk and carry pizzas: the player and the helpers. */
public abstract class Carrier extends Entity {
    public int carried;
    public float xfer;                 // cooldown between item transfers
    protected float tx, ty;            // where we are walking to
    protected boolean moving;
    private float walked;
    private final int body, hat;

    protected Carrier(World w, float x, float y, int body, int hat) {
        super(w, x, y);
        this.tx = x; this.ty = y; this.body = body; this.hat = hat;
    }

    public abstract int capacity();
    protected abstract float speed();

    public void setTarget(float x, float y) {
        tx = Math.max(0.4f, Math.min(World.GW - 0.4f, x));
        ty = Math.max(0.4f, Math.min(World.GH - 0.4f, y));
        moving = true;
    }

    @Override
    public void update(float dt) {
        if (!moving) return;
        float dx = tx - x, dy = ty - y;
        float d = (float) Math.hypot(dx, dy);
        if (d < 0.06f) { moving = false; return; }
        float s = Math.min(d, speed() * dt);
        float nx = x + dx / d * s, ny = y + dy / d * s;
        // move one axis at a time so we slide along obstacles instead of sticking
        if (!w.blocked(nx, y)) x = nx;
        if (!w.blocked(x, ny)) y = ny;
        walked += s;
    }

    @Override
    public void draw(Canvas c, Paint p, Iso iso) {
        float bob = moving ? (float) Math.abs(Math.sin(walked * 6f)) * 0.05f : 0f;
        iso.person(c, p, x, y, body, hat, carried, bob);
    }
}
