package com.pizzaempire.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** The whole game simulation. No drawing happens here (see GameView / Entity.draw). */
public class World {
    public static final int GW = 12, GH = 16;                 // floor size in tiles
    public static final float EX = 7.5f, EY = 16.8f;          // entrance / exit
    public static final float QX = 4.5f, QY0 = 9.1f, QSTEP = 0.95f;   // queue line
    public static final int MAX_QUEUE = 6;
    public static final float[][] SEATS = {
        {8.8f, 10.1f}, {10.8f, 10.1f}, {9.8f, 9.1f},  {9.8f, 11.1f},
        {8.8f, 13.1f}, {10.8f, 13.1f}, {9.8f, 12.1f}, {9.8f, 14.1f}
    };

    public final GameState state;
    public final Random rnd = new Random();
    public final List<Entity> entities = new ArrayList<>();
    public final List<Customer> queue = new ArrayList<>();
    public final List<Oven> ovens = new ArrayList<>();
    public final List<Helper> helpers = new ArrayList<>();
    public final List<Carrier> carriers = new ArrayList<>();
    public final boolean[] seatTaken = new boolean[SEATS.length];
    private final List<float[]> blocks = new ArrayList<>();   // solid rectangles {x0,y0,x1,y1}

    public Player player;
    public Counter counter;
    public float time;
    private float spawnTimer = 1.0f;

    public World(GameState state) {
        this.state = state;

        counter = new Counter();
        for (int i = 0; i < 4; i++) entities.add(new CounterPart(this, counter, i));
        blocks.add(new float[]{Counter.X0, Counter.Y0, Counter.X1, Counter.Y1});

        entities.add(new MoneyPile(this));

        addTable(9.2f, 9.5f, 0);
        addTable(9.2f, 12.5f, 4);

        player = new Player(this);
        entities.add(player);
        carriers.add(player);

        sync();
    }

    private void addTable(float x0, float y0, int firstSeat) {
        entities.add(new Table(this, x0, y0, firstSeat));
        blocks.add(new float[]{x0, y0, x0 + 1.2f, y0 + 1.2f});
    }

    /** Creates the ovens and helpers the player has bought. Call after shopping. */
    public void sync() {
        while (ovens.size() < state.ovens && ovens.size() < 2) {
            float ox = ovens.isEmpty() ? 2.25f : 5.5f;
            Oven o = new Oven(this, ox, 1.3f);
            ovens.add(o);
            entities.add(o);
            blocks.add(new float[]{ox - 0.75f, 0.65f, ox + 0.75f, 2.4f});
        }
        while (helpers.size() < state.helpers) {
            Helper h = new Helper(this, helpers.size());
            helpers.add(h);
            carriers.add(h);
            entities.add(h);
        }
    }

    // ------------------------------------------------------------------ helpers

    public boolean blocked(float px, float py) {
        for (float[] b : blocks) {
            if (px > b[0] - 0.3f && px < b[2] + 0.3f && py > b[1] - 0.3f && py < b[3] + 0.3f) return true;
        }
        return false;
    }

    public float queueY(int idx) { return QY0 + Math.max(0, idx) * QSTEP; }

    public Oven bestOven() {
        Oven best = null;
        for (Oven o : ovens) if (best == null || o.stock > best.stock) best = o;
        return best;
    }

    /** Moves e toward (tx,ty). Returns true when it has arrived. */
    public static boolean step(Entity e, float tx, float ty, float speed, float dt) {
        float dx = tx - e.x, dy = ty - e.y;
        float d = (float) Math.hypot(dx, dy);
        if (d < 0.08f) return true;
        float s = Math.min(d, speed * dt);
        e.x += dx / d * s;
        e.y += dy / d * s;
        return d - s < 0.08f;
    }

    public void addFloat(String text, float x, float y, int color) {
        entities.add(new FloatText(this, x, y, text, color));
    }

    private int takeSeat() {
        List<Integer> free = new ArrayList<>();
        for (int i = 0; i < seatTaken.length; i++) if (!seatTaken[i]) free.add(i);
        if (free.isEmpty()) return -1;
        int seat = free.get(rnd.nextInt(free.size()));
        seatTaken[seat] = true;
        return seat;
    }

    // ------------------------------------------------------------------ rules

    /** A customer at the front of the queue buys one pizza from the counter. */
    void sell(Customer c) {
        queue.remove(c);
        counter.stock--;
        state.pile += state.pizzaPrice();
        state.totalServed++;
        c.holding = true;
        SoundManager.play(SoundManager.SELL);
        if (rnd.nextFloat() < 0.06f) {
            state.gems++;
            addFloat("+1 gem", c.x, c.y, 0xFFE040FB);
        }
        int seat = takeSeat();
        if (seat >= 0) { c.seat = seat; c.s = Customer.S.TO_SEAT; } else { c.leave(); }
    }

    private void spawnCustomer() {
        Customer c = new Customer(this, EX, EY);
        queue.add(c);
        entities.add(c);
    }

    private void transfer(Carrier c, float dt) {
        c.xfer -= dt;
        if (c.xfer > 0) return;

        if (c.carried < c.capacity()) {                          // oven -> carrier
            for (Oven o : ovens) {
                if (o.stock > 0 && Math.hypot(c.x - o.px(), c.y - o.py()) < 1.7) {
                    o.stock--; c.carried++; c.xfer = 0.1f;
                    if (c == player && c.carried == c.capacity()) SoundManager.play(SoundManager.PICK);
                    return;
                }
            }
        }
        if (c.carried > 0 && counter.stock < Counter.CAP         // carrier -> counter
                && Math.hypot(c.x - Counter.PX, c.y - Counter.PY) < 1.9) {
            c.carried--; counter.stock++; c.xfer = 0.1f;
            if (c == player && c.carried == 0) SoundManager.play(SoundManager.DROP);
        }
    }

    private void collect() {
        if (state.pile > 0 && Math.hypot(player.x - MoneyPile.CX, player.y - MoneyPile.CY) < 1.3) {
            state.money += state.pile;
            addFloat("+$" + state.pile, player.x, player.y, 0xFF69F0AE);
            state.pile = 0;
            SoundManager.play(SoundManager.COLLECT);
        }
    }

    // ------------------------------------------------------------------ main update

    public void update(float dt) {
        time += dt;

        spawnTimer -= dt;
        if (spawnTimer <= 0) {
            spawnTimer = state.spawnInterval() * (0.7f + rnd.nextFloat() * 0.6f);
            if (queue.size() < MAX_QUEUE) spawnCustomer();
        }

        for (int i = 0; i < entities.size(); i++) entities.get(i).update(dt);
        for (int i = entities.size() - 1; i >= 0; i--) if (entities.get(i).dead) entities.remove(i);

        for (Carrier c : carriers) transfer(c, dt);
        collect();
    }
}
