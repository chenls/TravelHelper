package com.cqupt.travelhelper.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;

import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.switchButton.SwitchButton;

import cn.bmob.push.BmobPush;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SwitchButton update_switch_button = (SwitchButton) findViewById(R.id.update_switch_button);
        assert update_switch_button != null;
        update_switch_button.setOnCheckedChangeListener(this);
        update_switch_button.setCheckedDelayed(prefs.getBoolean("update", true));
        SwitchButton push_switch_button = (SwitchButton) findViewById(R.id.push_switch_button);
        assert push_switch_button != null;
        push_switch_button.setOnCheckedChangeListener(this);
        push_switch_button.setCheckedDelayed(prefs.getBoolean("push", true));
        SwitchButton shake_switch_button = (SwitchButton) findViewById(R.id.shake_switch_button);
        assert shake_switch_button != null;
        shake_switch_button.setOnCheckedChangeListener(this);
        shake_switch_button.setCheckedDelayed(prefs.getBoolean("shake", true));
        SwitchButton back_switch_button = (SwitchButton) findViewById(R.id.back_switch_button);
        assert back_switch_button != null;
        back_switch_button.setOnCheckedChangeListener(this);
        back_switch_button.setCheckedDelayed(prefs.getBoolean("back", true));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.update_switch_button:
                saveData("update", isChecked);
                break;
            case R.id.push_switch_button:
                saveData("push", isChecked);
                if (isChecked) {
                    // 启动推送服务
                    BmobPush.startWork(this);
                } else {
                    // 关闭推送服务
                    BmobPush.stopWork();
                }
                break;
            case R.id.shake_switch_button:
                saveData("shake", isChecked);
                break;
            case R.id.back_switch_button:
                saveData("back", isChecked);
                break;
        }
    }

    private void saveData(String key, boolean value) {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + "_preferences"
                , Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
