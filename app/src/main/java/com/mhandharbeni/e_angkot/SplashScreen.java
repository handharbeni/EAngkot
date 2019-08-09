package com.mhandharbeni.e_angkot;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mhandharbeni.e_angkot.main_activity.LoginActivity;
import com.mhandharbeni.e_angkot.second_activity.user.MainActivity;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreen extends BaseActivity {
    @BindView(R.id.appnameversion)
    TextView appnameversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        try {
            String versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            String appName = "V";
            appnameversion.setText(String.format("%s %s", appName, versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        hideActionBar();
        new Handler().postDelayed(() -> {
            if (getPref(Constant.IS_LOGGIN, false)) {
                if (getPref(Constant.MODE, "USER").equalsIgnoreCase("USER")){
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashScreen.this, com.mhandharbeni.e_angkot.second_activity.driver.MainActivity.class));
                }
            } else {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            }
            finish();
        }, 2000);

    }
}
