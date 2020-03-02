package cn.xylin.miui.step.manage.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author XyLin
 * @date 2020年1月9日
 **/
public class RootTool {
    private static final String SYS_APP_DIR = Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1 ? "priv-app" : "app";

    private static Process getRootProcess() throws IOException {
        return Runtime.getRuntime().exec("su");
    }

    private static int getExecCommandResult(String... commands) {
        try {
            StringBuilder builder = new StringBuilder();
            for (String command : commands) {
                builder.append(command);
                builder.append("\n");
            }
            Process process = getRootProcess();
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes(builder.toString());
            dataOutputStream.flush();
            dataOutputStream.close();
            return process.waitFor();
        } catch (IOException ignored) {
        } catch (InterruptedException ignored) {
        }
        return -1;
    }

    public static boolean haveRoot() {
        return getExecCommandResult( "exit") == 0;
    }

    public static boolean convertSystemApp(Context context) {
        return getExecCommandResult(
                "mount -o rw,remount -t auto /system",
                String.format("mkdir /system/%s/", SYS_APP_DIR),
                String.format(
                        "cat %s > /system/%s/StepManage.apk",
                        context.getApplicationInfo().sourceDir,
                        SYS_APP_DIR
                ),
                String.format("cd system/%s/", SYS_APP_DIR),
                "chmod 644 StepManage.apk",
                "reboot"
        ) == 0;
    }

    public static boolean isSystemApp(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}
