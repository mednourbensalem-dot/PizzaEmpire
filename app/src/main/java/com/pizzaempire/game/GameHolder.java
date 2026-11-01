package com.pizzaempire.game;

import android.content.Context;

/** Keeps the saved state and the live world alive while screens change. */
public final class GameHolder {
    private static GameState state;
    private static World world;

    private GameHolder() {}

    public static GameState state(Context c) {
        if (state == null) state = SaveManager.load(c.getApplicationContext());
        return state;
    }

    public static World world(Context c) {
        if (world == null) world = new World(state(c));
        return world;
    }

    public static void save(Context c) {
        if (state != null) SaveManager.save(c.getApplicationContext(), state);
    }

    /** Forget the loaded game (used when another player logs in or the user logs out). */
    public static void clear() {
        state = null;
        world = null;
    }

    public static void newGame(Context c) {
        state(c).reset();
        world = null;           // a fresh world is built next time it is needed
        save(c);
    }
}
