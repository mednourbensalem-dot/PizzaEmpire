package com.pizzaempire.game;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;

/** Sound effects made with Android's built-in ToneGenerator (no audio files needed). */
public final class SoundManager {
    public static final int PICK = 0, DROP = 1, SELL = 2, COLLECT = 3, BUY = 4, ERROR = 5;

    private static ToneGenerator tone;
    private static boolean enabled = true;

    private SoundManager() {}

    public static void init(Context c) { enabled = SaveManager.isSoundOn(c); }
    public static void setEnabled(boolean on) { enabled = on; }

    public static void play(int sfx) {
        if (!enabled) return;
        try {
            if (tone == null) tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 60);
            switch (sfx) {
                case PICK:    tone.startTone(ToneGenerator.TONE_PROP_BEEP, 60);    break;
                case DROP:    tone.startTone(ToneGenerator.TONE_PROP_ACK, 120);    break;
                case SELL:    tone.startTone(ToneGenerator.TONE_PROP_PROMPT, 120); break;
                case COLLECT: tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 160);  break;
                case BUY:     tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 200);  break;
                case ERROR:   tone.startTone(ToneGenerator.TONE_PROP_NACK, 150);   break;
                default: break;
            }
        } catch (RuntimeException e) {
            tone = null;
        }
    }
}
