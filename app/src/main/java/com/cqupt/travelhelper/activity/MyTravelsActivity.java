package com.cqupt.travelhelper.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.fragment.TravelsFragment;

public class MyTravelsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_travels);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = TravelsFragment.newInstanceMine(true);
        fragmentTransaction.add(R.id.relativeLayout, fragment);
        fragmentTransaction.commit();
    }
}
