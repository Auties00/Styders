package it.auties.styders.utils;

import android.content.Context;
import android.media.AudioManager;

public class AudioUtils {
    public static boolean isWiredToHeadphones(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) {
            return false;
        }

        return audioManager.isWiredHeadsetOn();
    }
}
