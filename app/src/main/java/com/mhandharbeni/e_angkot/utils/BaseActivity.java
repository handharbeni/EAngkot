package com.mhandharbeni.e_angkot.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.snackbar.Snackbar;
import com.mhandharbeni.e_angkot.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    public static String TAG = BaseActivity.class.getSimpleName();

    @BindView(R.id.idProfile)
    public AppCompatImageView idProfile;

    @BindView(R.id.idTitle)
    public TextView idTitle;

    @BindView(R.id.idSwitch)
    public SwitchCompat idSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayOptions(actionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_angkot);
    }

    @OnCheckedChanged(R.id.idSwitch)
    public void onSwitchActionBar(boolean isChecked){
        showToast(getApplicationContext(), String.valueOf(isChecked));
    }

    public void showActionBar(){
        Objects.requireNonNull(getSupportActionBar()).show();
    }

    public void hideActionBar(){
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    public void showLog(String TAG, String message){
        writeLog(TAG, message);
    }
    public void showLog(String message){
        writeLog(TAG, message);
    }
    private void writeLog(String TAG, String message){
        Log.d(TAG, message);
    }

    public void showToast(Context context, String message){
        writeToast(context, message, Toast.LENGTH_SHORT);
    }

    public void showToast(Context context, String message, int duration){
        writeToast(context, message, duration);
    }

    private void writeToast(Context context, String message, int duration){
        Toast.makeText(context, message, duration).show();
    }

    public void showSnackBar(View coordinatorLayout, SpannableStringBuilder snackbarText){
        writeSnackBar(coordinatorLayout, snackbarText, Snackbar.LENGTH_LONG);
    }

    public void showSnackBar(View coordinatorLayout, SpannableStringBuilder snackbarText, int duration){
        writeSnackBar(coordinatorLayout, snackbarText, duration);
    }

    private void writeSnackBar(View coordinatorLayout, SpannableStringBuilder snackbarText, int duration){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, snackbarText, duration)
                .setDuration(8000);
        snackbar.show();
    }
}
