package com.pizzaempire.game;

/** A hired worker: takes pizzas from the best oven to the counter, again and again. */
public class Helper extends Carrier {
    private final int id;
    private int mode = 0;     // 0 = go to oven, 1 = go to counter

    public Helper(World w, int id) { super(w, 4.5f + id * 0.5f, 2.6f, 0xFF43A047, 0xFF7B1FA2); this.id = id; }

    @Override public int capacity() { return 3; }
    @Override protected float speed() { return 2.6f; }

    @Override
    public void update(float dt) {
        float off = (id - 1) * 0.5f;      // spread helpers out so they do not overlap
        if (mode == 0) {
            Oven o = w.bestOven();
            if (o != null) {
                float gx = o.px() + off, gy = o.py();
                setTarget(gx, gy);
                boolean near = Math.hypot(x - gx, y - gy) < 0.4;
                if (carried > 0 && (carried >= capacity() || (near && o.stock == 0))) mode = 1;
            }
        } else {
            setTarget(Counter.PX + off, Counter.PY);
            if (carried == 0) mode = 0;
        }
        super.update(dt);
    }
}
