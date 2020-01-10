package cn.xylin.miui.step.manage;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author XyLin
 * @date 2020年1月9日
 **/
class RootTool {
    private static final String SYS_APP_DIR = Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1 ? "priv-app" : "app";

    private static Process getRootProcess() throws IOException {
        return Runtime.getRuntime().exec("su");
    }

    private static int getExecCommandResult(Context context, String... commands) {
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
        } catch (IOException e) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.toast_add_steps_failed)
                    .setMessage(String.format(context.getString(R.string.dialog_message_error), e.toString()))
                    .setPositiveButton(R.string.btn_ok, null)
                    .create()
                    .show();
        } catch (InterruptedException e) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.toast_add_steps_failed)
                    .setMessage(String.format(context.getString(R.string.dialog_message_error), e.toString()))
                    .setPositiveButton(R.string.btn_ok, null)
                    .create()
                    .show();
        }
        return -1;
    }

    static boolean haveRoot(Context context) {
        return getExecCommandResult(context, "exit") == 0;
    }

    static boolean convertSystemApp(Context context) {
        return getExecCommandResult(context,
                "mount -o rw,remount -t auto /system",
                String.format("mkdir /system/%s/StepManage/", SYS_APP_DIR),
                String.format(
                        "cat %s > /system/%s/StepManage/StepManage.apk",
                        context.getApplicationInfo().sourceDir,
                        SYS_APP_DIR
                ),
                String.format("cd system/%s/StepManage/", SYS_APP_DIR),
                "chmod 644 StepManage.apk",
                "reboot"
        ) == 0;
    }
}
