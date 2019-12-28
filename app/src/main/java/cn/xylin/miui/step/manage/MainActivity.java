package cn.xylin.miui.step.manage;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;

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
    private final Uri STEP_URI = Uri.parse("content://com.miui.providers.steps/item");
    private TextView tvTodaySteps;
    private EditText edtAddSteps;
    private int todayStepCount;
    private long clickTime = 0L;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTodaySteps = findViewById(R.id.tvTodaySteps);
        edtAddSteps = findViewById(R.id.edtAddSteps);
        getTodayStep();
    }

    private void getTodayStep() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Cursor cursor = getContentResolver().query(STEP_URI, QUERY_FILED, null, null, null);
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
        }).

                start();
    }

    private String getTodayTime(boolean flag) {
        return String.format("%s%s", timeFormat.format(System.currentTimeMillis()).substring(0, 11), flag ? "00:00:00" : "23:59:59");
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void startStepAdd(View view) {
        try {
                int addSteps = Integer.parseInt(edtAddSteps.getText().toString());
                ContentValues values = new ContentValues();
                values.put(BEGIN_TIME, (System.currentTimeMillis() - 1200000L));
                values.put(END_TIME, System.currentTimeMillis());
                values.put(MODE, 2);
                values.put(STEPS, addSteps);
                getContentResolver().insert(STEP_URI, values);
                Toast.makeText(this, R.string.toast_add_steps_success, Toast.LENGTH_SHORT).show();
                getTodayStep();
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.toast_add_steps_failed)
                    .setMessage(e.toString())
                    .setPositiveButton(R.string.btn_ok, null)
                    .create()
                    .show();
        }
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
}
