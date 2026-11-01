package com.pizzaempire.game;

import android.content.Context;

public final class SaveManager {
    private static final String SETTINGS = "pizza_empire_settings";

    private SaveManager() {}

    /** The game save now lives in the players database, one save per logged-in player. */
    public static GameState load(Context c) {
        long id = Session.id(c);
        return id > 0 ? PlayerDb.get(c).loadState(id) : new GameState();
    }

    public static void save(Context c, GameState s) {
        long id = Session.id(c);
        if (id > 0) PlayerDb.get(c).saveState(id, s);
    }

    public static boolean hasSave(Context c) {
        long id = Session.id(c);
        return id > 0 && PlayerDb.get(c).hasProgress(id);
    }

    // ---- settings (separate file so "New game" never resets them) ----
    public static boolean isSoundOn(Context c) {
        return c.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).getBoolean("sound", true);
    }
    public static void setSoundOn(Context c, boolean on) {
        c.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).edit().putBoolean("sound", on).apply();
    }
    public static boolean isTutorialSeen(Context c) {
        return c.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).getBoolean("tutorial", false);
    }
    public static void setTutorialSeen(Context c, boolean seen) {
        c.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).edit().putBoolean("tutorial", seen).apply();
    }
}
