package com.cqupt.travelhelper.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.fragment.AttractionFragment;
import com.cqupt.travelhelper.fragment.MineFragment;
import com.cqupt.travelhelper.fragment.StrategyFragment;
import com.cqupt.travelhelper.fragment.TravelsFragment;
import com.cqupt.travelhelper.utils.CommonUtil;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.views.PgyerDialog;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolBar;
    private final MineFragment mineFragment = new MineFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUpdate();
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);

        viewPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new AttractionFragment();
                    case 1:
                        return new TravelsFragment();
                    case 2:
                        return new StrategyFragment();
                    case 3:
                        return mineFragment;
                    default:
                        return new AttractionFragment();
                }
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getString(R.string.attraction);
                    case 1:
                        return getString(R.string.travelogue);
                    case 2:
                        return getString(R.string.strategy);
                    case 3:
                        return getString(R.string.mine);
                    default:
                        return getString(R.string.attraction);
                }
            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.action_notifications:
                Intent intent2 = new Intent(MainActivity.this, NotifyActivity.class);
                startActivity(intent2);
                break;
            case R.id.action_settings:
                Intent intent3 = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent3);
                break;
            case R.id.action_about:
                Intent intent4 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent4);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Bitmap bitmap = data.getParcelableExtra("picture");
                String name = data.getStringExtra("name");
                mineFragment.changeInformation(bitmap, name);
            }
        }
    }

    private void checkUpdate() {
        //蒲公英自动更新
        PgyUpdateManager.register(MainActivity.this,
                new UpdateManagerListener() {
                    @Override
                    public void onUpdateAvailable(final String result) {
                        // 将新版本信息封装到AppBean中
                        final AppBean appBean = getAppBeanFromString(result);
                        //如果WIFI可用且用户设置了自动下载  不弹对话框，直接下载
                        if (CommonUtil.isWifiAvailable(MainActivity.this)) {
                            SharedPreferences prefs = PreferenceManager
                                    .getDefaultSharedPreferences(MainActivity.this);
                            if (prefs.getBoolean("update", true)) {
                                Toast.makeText(MainActivity.this, "检测到WIFI可用，" +
                                        "\n开始自动下载更新安装包", Toast.LENGTH_LONG).show();
                                startDownloadTask(MainActivity.this, appBean.getDownloadURL());
                            }
                            return;
                        }
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("更新应用")
                                .setMessage(appBean.getReleaseNote())
                                .setPositiveButton(
                                        "确定"
                                        , new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startDownloadTask(MainActivity.this,
                                                        appBean.getDownloadURL());
                                            }
                                        })
                                .setNegativeButton(
                                        "取消",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).show();
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                    }
                });
    }

    //蒲公英 反馈相关
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("shake", true)) {
            // 摇一摇的灵敏度，默认为950，数值越小灵敏度越高。
            PgyFeedbackShakeManager.setShakingThreshold(800);
            // 以对话框的形式弹出
            PgyFeedbackShakeManager.register(MainActivity.this);
            //noinspection ResourceType
            PgyerDialog.setDialogTitleBackgroundColor(getString(R.color.colorPrimary));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PgyFeedbackShakeManager.unregister();
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.getBoolean("back", true)) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    finish();
                    System.exit(0);
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
