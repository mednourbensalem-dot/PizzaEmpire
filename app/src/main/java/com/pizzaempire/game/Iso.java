package com.pizzaempire.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Isometric projection + drawing helpers.
 * The game logic uses flat (x, y) tile coordinates; only here do we convert to screen pixels.
 */
public class Iso {
    public float tw = 128f, th = 64f;   // tile width / height in pixels
    public float ox, oy;                // screen position of world (0, 0)
    public float time;                  // seconds, for little animations
    private final Path path = new Path();

    /** One "height unit" in pixels. */
    public float u() { return tw * 0.5f; }

    public float sx(float x, float y) { return ox + (x - y) * tw * 0.5f; }
    public float sy(float x, float y) { return oy + (x + y) * th * 0.5f; }

    /** Screen -> world (used to turn a finger tap into a floor position). */
    public float wx(float px, float py) {
        float a = (px - ox) / (tw * 0.5f), b = (py - oy) / (th * 0.5f);
        return (a + b) * 0.5f;
    }
    public float wy(float px, float py) {
        float a = (px - ox) / (tw * 0.5f), b = (py - oy) / (th * 0.5f);
        return (b - a) * 0.5f;
    }

    private void poly(Canvas c, Paint p, int color, float... pts) {
        path.reset();
        path.moveTo(pts[0], pts[1]);
        for (int i = 2; i < pts.length; i += 2) path.lineTo(pts[i], pts[i + 1]);
        path.close();
        p.setColor(color);
        c.drawPath(path, p);
    }

    /** A flat diamond (floor tile, road...) at height h. */
    public void quad(Canvas c, Paint p, float x0, float y0, float x1, float y1, float h, int color) {
        float hp = h * u();
        p.setStyle(Paint.Style.FILL);
        poly(c, p, color,
            sx(x0, y0), sy(x0, y0) - hp, sx(x1, y0), sy(x1, y0) - hp,
            sx(x1, y1), sy(x1, y1) - hp, sx(x0, y1), sy(x0, y1) - hp);
    }

    /** A 3-D box standing on the floor. */
    public void box(Canvas c, Paint p, float x0, float y0, float x1, float y1, float h,
                    int top, int left, int right) {
        boxZ(c, p, x0, y0, x1, y1, 0f, h, top, left, right);
    }

    /** A 3-D box whose bottom is lifted by z. */
    public void boxZ(Canvas c, Paint p, float x0, float y0, float x1, float y1, float z, float h,
                     int top, int left, int right) {
        float zp = z * u(), hp = h * u();
        float ax = sx(x0, y0), ay = sy(x0, y0) - zp;
        float bx = sx(x1, y0), by = sy(x1, y0) - zp;
        float cx = sx(x1, y1), cy = sy(x1, y1) - zp;
        float dx = sx(x0, y1), dy = sy(x0, y1) - zp;
        p.setStyle(Paint.Style.FILL);
        poly(c, p, left,  dx, dy, cx, cy, cx, cy - hp, dx, dy - hp);   // front-left face
        poly(c, p, right, cx, cy, bx, by, bx, by - hp, cx, cy - hp);   // front-right face
        poly(c, p, top,   ax, ay - hp, bx, by - hp, cx, cy - hp, dx, dy - hp);
    }

    /** A vertical wall along the line (x0,y0)-(x1,y1). */
    public void wall(Canvas c, Paint p, float x0, float y0, float x1, float y1,
                     float z, float h, int color) {
        float zp = z * u(), hp = h * u();
        float ax = sx(x0, y0), ay = sy(x0, y0) - zp;
        float bx = sx(x1, y1), by = sy(x1, y1) - zp;
        p.setStyle(Paint.Style.FILL);
        poly(c, p, color, ax, ay, bx, by, bx, by - hp, ax, ay - hp);
    }

    /** A stack of pizzas whose bottom is at screen (px, py). */
    public void pizzaStack(Canvas c, Paint p, float px, float py, int count, float scale) {
        float u = u() * scale;
        int n = Math.min(count, 14);
        p.setStyle(Paint.Style.FILL);
        for (int i = 0; i < n; i++) {
            float cy = py - i * 0.09f * u;
            p.setColor(0xFFD98A3D);
            c.drawOval(px - 0.34f * u, cy - 0.15f * u, px + 0.34f * u, cy + 0.15f * u, p);
            p.setColor(0xFFFFB74D);
            c.drawOval(px - 0.30f * u, cy - 0.13f * u, px + 0.30f * u, cy + 0.11f * u, p);
        }
        if (n > 0) {
            float cy = py - (n - 1) * 0.09f * u;
            p.setColor(0xFFC62828);
            c.drawCircle(px - 0.12f * u, cy - 0.02f * u, 0.06f * u, p);
            c.drawCircle(px + 0.10f * u, cy - 0.04f * u, 0.06f * u, p);
            c.drawCircle(px, cy + 0.05f * u, 0.06f * u, p);
        }
    }

    /** A little blob character standing at world (x, y). */
    public void person(Canvas c, Paint p, float x, float y, int body, int hat, int carried, float bob) {
        float u = u();
        float px = sx(x, y), py = sy(x, y);
        p.setStyle(Paint.Style.FILL);
        p.setColor(0x33000000);
        c.drawOval(px - 0.38f * u, py - 0.14f * u, px + 0.38f * u, py + 0.14f * u, p);

        float by = py - bob * u;
        p.setColor(body);
        c.drawRoundRect(px - 0.2f * u, by - 0.85f * u, px + 0.2f * u, by - 0.05f * u,
                0.15f * u, 0.15f * u, p);
        p.setColor(0xFF4E342E);
        c.drawCircle(px, by - 1.05f * u, 0.2f * u, p);
        if (hat != 0) {
            p.setColor(hat);
            c.drawOval(px - 0.24f * u, by - 1.34f * u, px + 0.24f * u, by - 1.08f * u, p);
        }
        if (carried > 0) {
            pizzaStack(c, p, px, by - 1.5f * u, carried, 1f);
            text(c, p, "x" + carried, px + 0.55f * u, by - 1.45f * u, 0.38f * u, 0xFFFFFFFF, true);
        }
    }

    /** Centered text with a dark outline so it is readable on any background. */
    public void text(Canvas c, Paint p, String s, float px, float py, float size, int color, boolean bold) {
        p.setStyle(Paint.Style.FILL);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(size);
        p.setFakeBoldText(bold);
        p.setColor(0x99000000);
        c.drawText(s, px + size * 0.06f, py + size * 0.06f, p);
        p.setColor(color);
        c.drawText(s, px, py, p);
        p.setTextAlign(Paint.Align.LEFT);
        p.setFakeBoldText(false);
    }
}
