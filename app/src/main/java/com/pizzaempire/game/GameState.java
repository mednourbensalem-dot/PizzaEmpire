package com.pizzaempire.game;

/** Everything that must be saved to disk. */
public class GameState {
    public long money;
    public int gems;
    public int pile;          // cash waiting on the floor, not yet collected
    public int capLvl, speedLvl, ovenLvl, marketLvl, priceLvl;
    public int helpers;       // 0..3
    public int ovens;         // 1..2
    public int totalServed;

    public GameState() { reset(); }

    public void reset() {
        money = 50; gems = 0; pile = 0;
        capLvl = speedLvl = ovenLvl = marketLvl = priceLvl = 0;
        helpers = 0; ovens = 1; totalServed = 0;
    }

    // ---- values derived from upgrade levels ----
    public int capacity()        { return 4 + 2 * capLvl; }                    // pizzas carried
    public float walkSpeed()     { return 3.2f + 0.35f * speedLvl; }           // tiles / second
    public float ovenInterval()  { return 4.0f / (1f + 0.25f * ovenLvl); }     // seconds per pizza
    public float spawnInterval() { return Math.max(1.6f, 6.0f - 0.45f * marketLvl); }
    public int pizzaPrice()      { return 10 + 3 * priceLvl; }
}
