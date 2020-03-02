package cn.xylin.miui.step.manage.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author XyLin
 * @date 2020年3月2日 16:55:04
 * Shared.java
 **/
public class Shared {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static final String KEY_WORK_MODE = "currentWorkMode";

    public Shared(Context context) {
        preferences = context.getSharedPreferences("app_setting", Context.MODE_PRIVATE);
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, Final.BOOL_NULL);
    }

    public int getInt(String key) {
        return preferences.getInt(key, Final.INTEGER_NULL);
    }

    public Shared getEdit() {
        if (editor == null) {
            editor = preferences.edit();
        }
        return this;
    }

    public Shared putInt(String key, int value) {
        editor.putInt(key, value);
        return this;
    }

    public Shared putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return this;
    }

    public void editApply() {
        editor.apply();
    }
}
