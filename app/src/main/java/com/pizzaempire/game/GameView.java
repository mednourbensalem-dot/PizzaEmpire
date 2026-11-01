package com.pizzaempire.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Draws the world 60 times a second and turns finger touches into walking targets. */
public class GameView extends View {
    private static final Comparator<Entity> BY_DEPTH = (a, b) -> Float.compare(a.depth(), b.depth());

    private final Iso iso = new Iso();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Entity> sorted = new ArrayList<>();
    private World world;
    private long last = System.nanoTime();
    private float camX, camY;
    private boolean camReady;

    public GameView(Context c) { super(c); }
    public GameView(Context c, AttributeSet a) { super(c, a); }

    public void setWorld(World w) { world = w; camReady = false; last = System.nanoTime(); }

    // ------------------------------------------------------------------ loop

    @Override
    protected void onDraw(Canvas canvas) {
        long now = System.nanoTime();
        float dt = Math.min(0.05f, (now - last) / 1_000_000_000f);   // seconds since last frame
        last = now;

        if (world != null && getWidth() > 0) {
            world.update(dt);
            render(canvas, dt);
        }
        postInvalidateOnAnimation();     // ask Android for the next frame
    }

    private void render(Canvas c, float dt) {
        int W = getWidth(), H = getHeight();
        iso.tw = W / 5.0f;
        iso.th = iso.tw / 2f;
        iso.time = world.time;

        // camera follows the player smoothly
        Player pl = world.player;
        float tx = Math.max(2.5f, Math.min(World.GW - 2.5f, pl.x));
        float ty = Math.max(3f, Math.min(World.GH - 3f, pl.y));
        if (!camReady) { camX = tx; camY = ty; camReady = true; }
        float k = Math.min(1f, dt * 5f);
        camX += (tx - camX) * k;
        camY += (ty - camY) * k;
        iso.ox = W / 2f - (camX - camY) * iso.tw / 2f;
        iso.oy = H * 0.5f - (camX + camY) * iso.th / 2f;

        c.drawColor(0xFF9CCC65);                                            // grass
        iso.quad(c, paint, -3, World.GH, World.GW + 3, World.GH + 3.5f, 0, 0xFF546E7A);      // road
        iso.quad(c, paint, -3, World.GH, World.GW + 3, World.GH + 1.2f, 0, 0xFFCFD8DC);     // sidewalk

        // floor tiles (skip the ones that are off screen)
        for (int i = 0; i < World.GW; i++) {
            for (int j = 0; j < World.GH; j++) {
                float cx = iso.sx(i + 0.5f, j + 0.5f), cy = iso.sy(i + 0.5f, j + 0.5f);
                if (cx < -iso.tw || cx > W + iso.tw || cy < -iso.th * 2 || cy > H + iso.th * 2) continue;
                boolean even = (i + j) % 2 == 0;
                int color = j < 7 ? (even ? 0xFFF3D9B8 : 0xFFEFCFA8) : (even ? 0xFFFFE0D6 : 0xFFFAD2C4);
                iso.quad(c, paint, i, j, i + 1, j + 1, 0f, color);
            }
        }

        // back walls
        iso.wall(c, paint, 0, 0, World.GW, 0, 0f, 2.2f, 0xFFE3F2FD);
        iso.wall(c, paint, 0, 0, World.GW, 0, 1.0f, 0.9f, 0xFF90CAF9);
        iso.wall(c, paint, 0, 0, World.GW, 0, 0f, 0.25f, 0xFF1976D2);
        iso.wall(c, paint, 0, 0, 0, World.GH, 0f, 2.2f, 0xFFD6E6F5);
        iso.wall(c, paint, 0, 0, 0, World.GH, 1.0f, 0.9f, 0xFF90CAF9);
        iso.wall(c, paint, 0, 0, 0, World.GH, 0f, 0.25f, 0xFF1976D2);

        // everything else, back to front
        sorted.clear();
        sorted.addAll(world.entities);
        Collections.sort(sorted, BY_DEPTH);
        for (Entity e : sorted) e.draw(c, paint, iso);
    }

    // ------------------------------------------------------------------ touch

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (world == null) return true;
        int a = e.getActionMasked();
        if (a == MotionEvent.ACTION_DOWN || a == MotionEvent.ACTION_MOVE) {
            world.player.setTarget(iso.wx(e.getX(), e.getY()), iso.wy(e.getX(), e.getY()));
        }
        return true;
    }
}
