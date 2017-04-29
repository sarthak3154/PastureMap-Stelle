package com.stelle.stelleapp.homescreen.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.stelle.stelleapp.BaseActivity;
import com.stelle.stelleapp.R;
import com.stelle.stelleapp.homescreen.fragments.MapScreenFragment;
import com.stelle.stelleapp.homescreen.interfaces.HomeScreenContract;

import butterknife.ButterKnife;

public class HomeScreenActivity extends BaseActivity implements HomeScreenContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        init();
    }

    @Override
    public void init() {
        ButterKnife.bind(this);
        showMap();
    }

    @Override
    public void showMap() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentFrame, MapScreenFragment.newInstance())
                .commitAllowingStateLoss();
    }
}
