package com.pizzaempire.game;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * The players database (SQLite, stored on the phone).
 * One row per player: account data (username, salt, password hash) + that player's saved game.
 */
public class PlayerDb extends SQLiteOpenHelper {
    private static final String NAME = "pizza_empire.db";
    private static final int VERSION = 1;
    private static PlayerDb instance;

    public static synchronized PlayerDb get(Context c) {
        if (instance == null) instance = new PlayerDb(c.getApplicationContext());
        return instance;
    }

    private PlayerDb(Context c) { super(c, NAME, null, VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE players ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "username TEXT NOT NULL UNIQUE COLLATE NOCASE,"
            + "salt TEXT NOT NULL,"
            + "pass_hash TEXT NOT NULL,"
            + "created_at INTEGER NOT NULL,"
            + "last_login INTEGER NOT NULL,"
            + "money INTEGER NOT NULL DEFAULT 50,"
            + "gems INTEGER NOT NULL DEFAULT 0,"
            + "pile INTEGER NOT NULL DEFAULT 0,"
            + "cap_lvl INTEGER NOT NULL DEFAULT 0,"
            + "speed_lvl INTEGER NOT NULL DEFAULT 0,"
            + "oven_lvl INTEGER NOT NULL DEFAULT 0,"
            + "market_lvl INTEGER NOT NULL DEFAULT 0,"
            + "price_lvl INTEGER NOT NULL DEFAULT 0,"
            + "helpers INTEGER NOT NULL DEFAULT 0,"
            + "ovens INTEGER NOT NULL DEFAULT 1,"
            + "total_served INTEGER NOT NULL DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Future versions: add columns with "ALTER TABLE players ADD COLUMN ..." (never drop player data).
    }

    // ------------------------------------------------------------------ accounts

    /** Creates a player. Returns the new id, or -1 if the username already exists. */
    public long createPlayer(String username, String password) {
        String salt = Auth.newSalt();
        long now = System.currentTimeMillis();
        ContentValues v = new ContentValues();
        v.put("username", username);
        v.put("salt", salt);
        v.put("pass_hash", Auth.hash(password, salt));
        v.put("created_at", now);
        v.put("last_login", now);
        return getWritableDatabase().insert("players", null, v);
    }

    public boolean usernameTaken(String username) {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT 1 FROM players WHERE username=?", new String[]{username})) {
            return c.moveToFirst();
        }
    }

    /** Returns the player id if the password is right, otherwise -1. */
    public long login(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        try (Cursor c = db.rawQuery("SELECT id, salt, pass_hash FROM players WHERE username=?",
                new String[]{username})) {
            if (!c.moveToFirst()) {
                Auth.hash(password, Auth.newSalt());     // same work as a real check
                return -1;
            }
            long id = c.getLong(0);
            if (!Auth.same(Auth.hash(password, c.getString(1)), c.getString(2))) return -1;
            ContentValues v = new ContentValues();
            v.put("last_login", System.currentTimeMillis());
            db.update("players", v, "id=?", new String[]{String.valueOf(id)});
            return id;
        }
    }

    public String usernameOf(long id) {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT username FROM players WHERE id=?", new String[]{String.valueOf(id)})) {
            return c.moveToFirst() ? c.getString(0) : null;
        }
    }

    public void deletePlayer(long id) {
        getWritableDatabase().delete("players", "id=?", new String[]{String.valueOf(id)});
    }

    // ------------------------------------------------------------------ game data

    public GameState loadState(long id) {
        GameState s = new GameState();
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT money, gems, pile, cap_lvl, speed_lvl, oven_lvl, market_lvl, price_lvl,"
              + " helpers, ovens, total_served FROM players WHERE id=?",
                new String[]{String.valueOf(id)})) {
            if (c.moveToFirst()) {
                s.money = c.getLong(0);       s.gems = c.getInt(1);        s.pile = c.getInt(2);
                s.capLvl = c.getInt(3);       s.speedLvl = c.getInt(4);    s.ovenLvl = c.getInt(5);
                s.marketLvl = c.getInt(6);    s.priceLvl = c.getInt(7);    s.helpers = c.getInt(8);
                s.ovens = c.getInt(9);        s.totalServed = c.getInt(10);
            }
        }
        return s;
    }

    public void saveState(long id, GameState s) {
        ContentValues v = new ContentValues();
        v.put("money", s.money);        v.put("gems", s.gems);          v.put("pile", s.pile);
        v.put("cap_lvl", s.capLvl);     v.put("speed_lvl", s.speedLvl); v.put("oven_lvl", s.ovenLvl);
        v.put("market_lvl", s.marketLvl); v.put("price_lvl", s.priceLvl);
        v.put("helpers", s.helpers);    v.put("ovens", s.ovens);        v.put("total_served", s.totalServed);
        getWritableDatabase().update("players", v, "id=?", new String[]{String.valueOf(id)});
    }

    public boolean hasProgress(long id) {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT total_served FROM players WHERE id=?", new String[]{String.valueOf(id)})) {
            return c.moveToFirst() && c.getInt(0) > 0;
        }
    }

    /** Leaderboard rows: {username, pizzasServed, money}. */
    public List<String[]> topPlayers(int limit) {
        List<String[]> rows = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT username, total_served, money FROM players"
              + " ORDER BY total_served DESC, money DESC LIMIT ?", new String[]{String.valueOf(limit)})) {
            while (c.moveToNext()) {
                rows.add(new String[]{c.getString(0), String.valueOf(c.getInt(1)), String.valueOf(c.getLong(2))});
            }
        }
        return rows;
    }
}
