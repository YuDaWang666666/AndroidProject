package com.example.newbeemall.util;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String SP_NAME = "info";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";

    public static void saveToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_TOKEN, token).apply();
    }

    public static String getToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_TOKEN, "");
    }

    public static void saveUserInfo(Context context, String userName, int userId) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_USER_NAME, userName).putInt(KEY_USER_ID, userId).apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_USER_NAME, "");
    }

    public static int getUserId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(KEY_USER_ID, 0);
    }

    public static boolean isLoggedIn(Context context) {
        return !getToken(context).isEmpty();
    }

    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
}
