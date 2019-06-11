package com.mhandharbeni.e_angkot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.mhandharbeni.e_angkot.main_activity.LoginActivity;
import com.mhandharbeni.e_angkot.second_activity.user.MainActivity;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;

import butterknife.ButterKnife;

public class SplashScreen extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        hideActionBar();
        new Handler().postDelayed(() -> {
            if (getPref(Constant.IS_LOGGIN, false)) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            }
            finish();
        }, 2000);

    }
}
