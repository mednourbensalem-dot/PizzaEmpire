package com.pizzaempire.game;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Password hashing and input checks. Passwords are never stored, only a salted PBKDF2 hash. */
public final class Auth {
    private Auth() {}

    public static String checkUsername(String u) {
        if (u == null || !u.matches("[A-Za-z0-9_]{3,16}")) return "Username: 3-16 letters, digits or _";
        return null;      // null = OK
    }

    public static String checkPassword(String p) {
        if (p == null || p.length() < 6) return "Password must be at least 6 characters";
        return null;
    }

    public static String newSalt() {
        byte[] b = new byte[16];
        new SecureRandom().nextBytes(b);
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public static String hash(String password, String saltB64) {
        try {
            byte[] salt = Base64.decode(saltB64, Base64.NO_WRAP);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);
            byte[] h = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).getEncoded();
            return Base64.encodeToString(h, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Constant-time comparison, so timing does not leak how many characters matched. */
    public static boolean same(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
