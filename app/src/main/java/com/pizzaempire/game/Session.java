package com.pizzaempire.game;

import android.content.Context;

/** Remembers which player is logged in (so the app skips the login screen next time). */
public final class Session {
    private static final String FILE = "pizza_empire_session";

    private Session() {}

    public static long id(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE).getLong("player_id", -1);
    }
    public static boolean isLoggedIn(Context c) { return id(c) > 0; }

    public static void set(Context c, long id) {
        c.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putLong("player_id", id).apply();
    }
    public static void clear(Context c) {
        c.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().remove("player_id").apply();
    }
}
