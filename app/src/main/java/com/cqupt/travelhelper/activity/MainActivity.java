package com.cqupt.travelhelper.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.fragment.AttractionFragment;
import com.cqupt.travelhelper.fragment.MineFragment;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    case 2:
                    case 3:
                        return new MineFragment();
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
                        return getString(R.string.strategy);
                    case 2:
                        return getString(R.string.travelogue);
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
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.action_notifications:
                Intent intent2 = new Intent(MainActivity.this, NotifyActivity.class);
                startActivity(intent2);
                break;
            case R.id.action_settings:
                Intent intent3 = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent3);
                break;
            case R.id.action_about:
                Intent intent4 = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent4);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
