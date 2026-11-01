package com.pizzaempire.game;

import android.graphics.Canvas;
import android.graphics.Paint;

/** A customer is always in exactly one state: queueing, walking to a seat, eating or leaving. */
public class Customer extends Entity {
    enum S { QUEUE, TO_SEAT, EATING, LEAVING }

    private static final int[] COLORS = {0xFF90A4AE, 0xFF9FA8DA, 0xFF80CBC4, 0xFFB0BEC5, 0xFFA5D6A7, 0xFFFFCC80};
    private static final float SPEED = 2.0f;

    S s = S.QUEUE;
    float patience = 45f;
    int seat = -1;
    boolean angry, holding;
    private float serveTimer, eatTimer;
    private final int body;

    public Customer(World w, float x, float y) {
        super(w, x, y);
        body = COLORS[w.rnd.nextInt(COLORS.length)];
    }

    void leave() { s = S.LEAVING; }

    @Override
    public void update(float dt) {
        switch (s) {
            case QUEUE: {
                int idx = w.queue.indexOf(this);
                boolean arrived = World.step(this, World.QX, w.queueY(idx), SPEED, dt);
                patience -= dt;
                if (patience <= 0) {                      // lost patience
                    w.queue.remove(this);
                    angry = true;
                    leave();
                    break;
                }
                if (idx == 0 && arrived && w.counter.stock > 0) {
                    serveTimer += dt;
                    if (serveTimer > 0.6f) w.sell(this);
                } else {
                    serveTimer = 0;
                }
                break;
            }
            case TO_SEAT:
                if (World.step(this, World.SEATS[seat][0], World.SEATS[seat][1], SPEED, dt)) {
                    s = S.EATING;
                    eatTimer = 4f + w.rnd.nextFloat() * 3f;
                }
                break;
            case EATING:
                eatTimer -= dt;
                if (eatTimer <= 0) {
                    w.seatTaken[seat] = false;
                    holding = false;
                    leave();
                }
                break;
            case LEAVING:
                if (World.step(this, World.EX, World.EY, SPEED, dt)) dead = true;
                break;
        }
    }

    @Override
    public void draw(Canvas c, Paint p, Iso iso) {
        float u = iso.u();
        iso.person(c, p, x, y, body, 0, holding ? 1 : 0, 0f);
        float px = iso.sx(x, y), py = iso.sy(x, y);

        if (angry) {
            iso.text(c, p, "!", px, py - 1.6f * u, 0.7f * u, 0xFFE53935, true);
        }
        // speech bubble: the first customer is waiting for a pizza that is not there yet
        if (s == S.QUEUE && !w.queue.isEmpty() && w.queue.get(0) == this && w.counter.stock == 0) {
            float by = py - 1.75f * u;
            p.setStyle(Paint.Style.FILL);
            p.setColor(0xFFFFFFFF);
            c.drawRoundRect(px - 0.45f * u, by - 0.3f * u, px + 0.45f * u, by + 0.2f * u,
                    0.15f * u, 0.15f * u, p);
            iso.pizzaStack(c, p, px - 0.18f * u, by + 0.05f * u, 1, 0.6f);
            iso.text(c, p, "1", px + 0.2f * u, by + 0.12f * u, 0.45f * u, 0xFF000000, true);
        }
    }
}
