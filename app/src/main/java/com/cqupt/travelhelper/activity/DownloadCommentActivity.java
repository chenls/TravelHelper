package com.cqupt.travelhelper.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.fragment.AttractionFragment;
import com.cqupt.travelhelper.fragment.StrategyFragment;
import com.cqupt.travelhelper.fragment.TravelsFragment;


public class DownloadCommentActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_comment);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assert toolBar != null;
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return AttractionFragment.newInstance(true);
                    case 1:
                        return TravelsFragment.newInstanceLocal(true);
                    case 2:
                        return StrategyFragment.newInstance(true);
                    default:
                        return AttractionFragment.newInstance(true);
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
                    default:
                        return getString(R.string.attraction);
                }
            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }
}
