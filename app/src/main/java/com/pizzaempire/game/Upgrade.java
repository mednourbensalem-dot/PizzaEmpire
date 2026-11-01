package com.pizzaempire.game;

import java.util.ArrayList;
import java.util.Locale;
import java.util.List;

/** One row in the shop, rebuilt from the current GameState every time. */
public class Upgrade {
    public final String title, desc, level;
    public final long cost;
    public final boolean maxed, usesGems;
    public final Runnable apply;

    private Upgrade(String title, String desc, String level, long cost,
                    boolean maxed, boolean usesGems, Runnable apply) {
        this.title = title; this.desc = desc; this.level = level;
        this.cost = cost; this.maxed = maxed; this.usesGems = usesGems; this.apply = apply;
    }

    public boolean canAfford(GameState s) { return usesGems ? s.gems >= cost : s.money >= cost; }
    public void pay(GameState s) { if (usesGems) s.gems -= cost; else s.money -= cost; }

    public static List<Upgrade> catalog(final GameState s) {
        List<Upgrade> l = new ArrayList<>();

        l.add(new Upgrade("🎒 Carry capacity",
                "Carry " + (s.capacity() + 2) + " pizzas at once",
                s.capLvl + "/8", (long) (60 * Math.pow(1.7, s.capLvl)),
                s.capLvl >= 8, false, () -> s.capLvl++));

        l.add(new Upgrade("👟 Walking speed",
                "Move faster around the shop",
                s.speedLvl + "/8", (long) (80 * Math.pow(1.7, s.speedLvl)),
                s.speedLvl >= 8, false, () -> s.speedLvl++));

        float next = 4.0f / (1f + 0.25f * (s.ovenLvl + 1));
        l.add(new Upgrade("🔥 Oven speed",
                "A pizza every " + String.format(Locale.US, "%.1f", next) + " s per oven",
                s.ovenLvl + "/8", (long) (100 * Math.pow(1.8, s.ovenLvl)),
                s.ovenLvl >= 8, false, () -> s.ovenLvl++));

        l.add(new Upgrade("♨️ Second oven",
                "Double your pizza production",
                (s.ovens - 1) + "/1", 500,
                s.ovens >= 2, false, () -> s.ovens++));

        l.add(new Upgrade("🧑‍🍳 Hire helper",
                "Carries pizzas from the oven to the counter",
                s.helpers + "/3", (long) (300 * Math.pow(2.2, s.helpers)),
                s.helpers >= 3, false, () -> s.helpers++));

        l.add(new Upgrade("📣 Marketing",
                "More customers arrive",
                s.marketLvl + "/8", (long) (120 * Math.pow(1.8, s.marketLvl)),
                s.marketLvl >= 8, false, () -> s.marketLvl++));

        l.add(new Upgrade("💲 Pizza price",
                "Sell each pizza for $" + (s.pizzaPrice() + 3),
                s.priceLvl + "/10", (long) (150 * Math.pow(1.9, s.priceLvl)),
                s.priceLvl >= 10, false, () -> s.priceLvl++));

        l.add(new Upgrade("💎 Exchange gems",
                "5 gems → $300",
                "You have " + s.gems + " gems", 5,
                false, true, () -> s.money += 300));

        return l;
    }
}
