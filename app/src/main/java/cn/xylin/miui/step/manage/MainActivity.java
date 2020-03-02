package cn.xylin.miui.step.manage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import cn.xylin.miui.step.manage.util.Final;
import cn.xylin.miui.step.manage.util.RootTool;
import cn.xylin.miui.step.manage.util.Shared;

/**
 * @author XyLin
 * @date 2019/12/27
 **/
public class MainActivity extends Activity {
    private final String ID = "_id";
    private final String BEGIN_TIME = "_begin_time";
    private final String END_TIME = "_end_time";
    private final String MODE = "_mode";
    private final String STEPS = "_steps";
    private final String[] QUERY_FILED = {ID, BEGIN_TIME, END_TIME, MODE, STEPS};
    private TextView tvTodaySteps;
    private EditText edtAddSteps;
    private int todayStepCount;
    private long clickTime = 0L;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Shared shared;
    private AlertDialog.Builder dialogAppTip;
    private int currentWorkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTodaySteps = findViewById(R.id.tvTodaySteps);
        edtAddSteps = findViewById(R.id.edtAddSteps);
        dialogAppTip = new AlertDialog.Builder(this)
                .setNegativeButton(R.string.btn_ok, null);
        shared = new Shared(this);
        currentWorkMode = shared.getInt(Shared.KEY_WORK_MODE);
        getTodayStep();
    }

    private void getTodayStep() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Cursor cursor = getContentResolver().query(Final.STEP_URI, QUERY_FILED, null, null, null);
                    long todayBeginTime = timeFormat.parse(getTodayTime(true)).getTime();
                    long todayEndTime = timeFormat.parse(getTodayTime(false)).getTime();
                    if (cursor != null) {
                        todayStepCount = 0;
                        while (cursor.moveToNext()) {
                            if (cursor.getLong(1) > todayBeginTime && cursor.getLong(2) < todayEndTime && cursor.getInt(3) == 2) {
                                todayStepCount += cursor.getInt(4);
                            }
                        }
                        cursor.close();
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTodaySteps.setText(String.format(getString(R.string.text_today_steps), todayStepCount));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String getTodayTime(boolean flag) {
        return String.format("%s%s", timeFormat.format(System.currentTimeMillis()).substring(0, 11), flag ? "00:00:00" : "23:59:59");
    }

    private ContentValues getAddStepValues() throws NumberFormatException {
        ContentValues values = new ContentValues();
        values.put(BEGIN_TIME, (System.currentTimeMillis() - 600000L));
        values.put(END_TIME, System.currentTimeMillis());
        values.put(MODE, 2);
        values.put(STEPS, Integer.parseInt(edtAddSteps.getText().toString()));
        return values;
    }

    public void startStepAdd(View view) {
        try {
            getContentResolver().insert(Final.STEP_URI, getAddStepValues());
            Toast.makeText(this, R.string.toast_add_steps_success, Toast.LENGTH_SHORT).show();
            getTodayStep();
            return;
        } catch (SecurityException e) {
            dialogAppTip.setMessage(
                    currentWorkMode == Final.WORK_MODE_CORE ?
                            R.string.add_step_error_core :
                            currentWorkMode == Final.WORK_MODE_ROOT ?
                                    R.string.add_step_error_root :
                                    R.string.add_step_error_system
            );
        } catch (NumberFormatException e) {
            dialogAppTip.setMessage(R.string.dialog_message_int_parser_error);
        }
        dialogAppTip.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        setWorkModeMenuItemTitle(menu.findItem(R.id.menuSwitchWorkMode));
        if(RootTool.isSystemApp(this)){
            menu.findItem(R.id.menuUninstallAppByRoot).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void setWorkModeMenuItemTitle(MenuItem item) {
        item.setTitle(
                String.format(
                        Final.CURRENT_WORK_MODE,
                        currentWorkMode == Final.WORK_MODE_CORE ?
                                Final.WORK_MODE_NAME_CORE :
                                currentWorkMode == Final.WORK_MODE_ROOT ?
                                        Final.WORK_MODE_NAME_ROOT :
                                        Final.WORK_MODE_NAME_SYSTEM
                )
        );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - clickTime) > 2000L) {
                clickTime = System.currentTimeMillis();
                Toast.makeText(this, R.string.toast_exit_app, Toast.LENGTH_SHORT).show();
                return true;
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSwitchWorkMode: {
                currentWorkMode++;
                currentWorkMode %= 3;
                shared.getEdit().putInt(Shared.KEY_WORK_MODE, currentWorkMode).editApply();
                setWorkModeMenuItemTitle(item);
                return true;
            }
            case R.id.menuWorkModeDescription:
            case R.id.menuAboutApp: {
                dialogAppTip.setMessage(item.getItemId() == R.id.menuWorkModeDescription ? R.string.work_mode_description : R.string.about_app);
                dialogAppTip.show();
                return true;
            }
            default: {
                return Final.BOOL_NULL;
            }
        }
    }
}
