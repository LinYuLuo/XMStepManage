package cn.xylin.miui.step.manage.util;

import android.net.Uri;

/**
 * @author XyLin
 * @date 2020年3月2日 16:56:16
 * Final.java
 **/
public class Final {
    public static final String ID = "_id";
    public static final String BEGIN_TIME = "_begin_time";
    public static final String END_TIME = "_end_time";
    public static final String MODE = "_mode";
    public static final String STEPS = "_steps";
    public static final boolean BOOL_NULL = false;
    public static final int INTEGER_NULL = 0;
    public static final int WORK_MODE_CORE=0;
    public static final int WORK_MODE_ROOT=1;
    public static final int WORK_MODE_SYSTEM=2;
    public static final String CURRENT_WORK_MODE = "模式：%s";
    public static final String WORK_MODE_NAME_CORE = "核心破解";
    public static final String WORK_MODE_NAME_ROOT = "ROOT";
    public static final String WORK_MODE_NAME_SYSTEM = "系统洗白";
    public static final Uri STEP_URI = Uri.parse("content://com.miui.providers.steps/item");
}
