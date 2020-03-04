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
    public static final String KEY_TRY_CONVERT_SYSTEM_APP="tryConvertSystemApp";
    public static final String KEY_UNZIP_SQLITE_FILE="isUnzipSqliteFile";
    public static final String KEY_AUTO_ADD_STEPS="AutoAddSteps";
    public static final String KEY_NEW_DAY_AUTO_ADD="isNewDayAutoAddSteps";
    public static final String KEY_CURRENT_DAY="currentDayInt";

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
