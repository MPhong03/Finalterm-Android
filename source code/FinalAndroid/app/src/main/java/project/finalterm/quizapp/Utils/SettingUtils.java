package project.finalterm.quizapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

public class SettingUtils {
    public static final String KEY_FLASHCARD_FLIP_TIME = "flashcard_flip_time";
    public static final String KEY_READ_FLASHCARD_TIME = "read_flashcard_time";
    public static final String KEY_FLASHCARD_INTERVAL_TIME = "flashcard_interval_time";

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean settingExists(Context context, String settingKey) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.contains(settingKey);
    }

    public static long getFlashcardFlipTime(Context context) {
        return getSharedPreferences(context).getLong(KEY_FLASHCARD_FLIP_TIME, TimeUnit.SECONDS.toNanos(5));
    }

    public static long getReadFlashcardTime(Context context) {
        return getSharedPreferences(context).getLong(KEY_READ_FLASHCARD_TIME, TimeUnit.SECONDS.toNanos(5));
    }

    public static long getFlashcardIntervalTime(Context context) {
        return getSharedPreferences(context).getLong(KEY_FLASHCARD_INTERVAL_TIME, TimeUnit.SECONDS.toNanos(5));
    }

    public static void setFlashcardFlipTime(Context context, long timeInSeconds) {
        getSharedPreferences(context).edit().putLong(KEY_FLASHCARD_FLIP_TIME, TimeUnit.SECONDS.toNanos(timeInSeconds)).apply();
    }

    public static void setReadFlashcardTime(Context context, long timeInSeconds) {
        getSharedPreferences(context).edit().putLong(KEY_READ_FLASHCARD_TIME, TimeUnit.SECONDS.toNanos(timeInSeconds)).apply();
    }

    public static void setFlashcardIntervalTime(Context context, long timeInSeconds) {
        getSharedPreferences(context).edit().putLong(KEY_FLASHCARD_INTERVAL_TIME, TimeUnit.SECONDS.toNanos(timeInSeconds)).apply();
    }
}
