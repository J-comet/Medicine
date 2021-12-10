package hs.project.medicine.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtil {

    /**
     * @param context
     * @param key
     * @param value   = String
     */
    public static void putSharedPreference(Context context, String key, String value) {
        if (context == null) return;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getSharedPreference(Context context, String key) {
        if (context == null) return null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, null);
    }

    public static String getSharedPreference(Context context, String key, String defaultValue) {
        if (context == null) return null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }


    /**
     * @param context
     * @param key
     * @param value   = boolean
     */
    public static void putSharedPreference(Context context, String key, boolean value) {
        if (context == null) return;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * @param context
     * @param key
     * @return 읽어온 값, 값이 없을 경우 false가 반환된다.
     */

    public static boolean getBooleanSharedPreference(Context context, String key) {
        if (context == null) return false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, false);
    }

    public static boolean getDefaultBooleanSharedPreference(Context context, String key) {
        if (context == null) return false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, true);
    }

    public static boolean getBooleanSharedPreference(Context context, String key, boolean result) {
        if (context == null) return false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, result);
    }


    /**
     * @param context
     * @param key
     * @param value   = int
     */
    public static void putSharedPreference(Context context, String key, int value) {
        if (context == null) return;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * <pre>
     * Int 데이터를 읽어옵니다.
     * </pre>
     *
     * @param context 컨텍스트
     * @param key     키
     * @return 읽어온 값, 값이 없을 경우 0이 반환된다.
     */
    public static int getIntSharedPreference(Context context, String key) {
        if (context == null) return 0;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, 0);
    }

    public static int getDefaultIntSharedPreference(Context context, String key) {
        if (context == null) return -1;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(key, -1);
    }

    public static int getIntSharedPreference(Context context, String key, int defaultVal) {
        if (context == null) return defaultVal;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, defaultVal);
    }

    /*public static void setUserInfo(Context context, String key, User user) {
        if (context == null) return;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, new Gson().toJson(user));
        editor.commit();
    }

    public static User getUserInfo(Context context, String key) {
        if (context == null) return null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return new Gson().fromJson(prefs.getString(key, null), User.class);
    }*/
}