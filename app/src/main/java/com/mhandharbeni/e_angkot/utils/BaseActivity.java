package com.mhandharbeni.e_angkot.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mhandharbeni.e_angkot.CoreApplication;
import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.main_activity.ProfileActivity;
import com.mhandharbeni.e_angkot.services.LocationServices;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    public static String TAG = BaseActivity.class.getSimpleName();

    @BindView(R.id.idProfile)
    public AppCompatImageView idProfile;

    @BindView(R.id.idTitle)
    TextView idTitle;

    @BindView(R.id.idSwitch)
    public SwitchCompat idSwitch;

    public LocationServices gpsService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startServices();
    }

    public ToolsFirebase getFirebase() {
        return CoreApplication.getFirebase();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_angkot);
    }

    @OnClick(R.id.idProfile)
    public void onProfileClick(){
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void showActionBar() {
        Objects.requireNonNull(getSupportActionBar()).show();
    }

    public void hideActionBar() {
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    public void changeTitleActionBar(String title) {
        idTitle.setText(title);
    }


    public EncryptedPreferences getPref() {
        return CoreApplication.getPref();
    }

    public void setPref(String key, String value) {
        getPref().edit()
                .putString(key, value)
                .apply();
    }

    public void setPref(String key, boolean value) {
        getPref().edit()
                .putBoolean(key, value)
                .apply();
    }

    public void setPref(String key, float value) {
        getPref().edit()
                .putFloat(key, value)
                .apply();
    }

    public void setPref(String key, long value) {
        getPref().edit()
                .putLong(key, value)
                .apply();
    }

    public void setPref(String key, int value) {
        getPref().edit()
                .putInt(key, value)
                .apply();
    }

    public String getPref(String key, String defaultValue) {
        return getPref().getString(key, defaultValue);
    }

    public boolean getPref(String key, boolean defaultValue) {
        return getPref().getBoolean(key, defaultValue);
    }

    public float getPref(String key, float defaultValue) {
        return getPref().getFloat(key, defaultValue);
    }

    public long getPref(String key, long defaultValue) {
        return getPref().getLong(key, defaultValue);
    }

    public int getPref(String key, int defaultValue) {
        return getPref().getInt(key, defaultValue);
    }

    public void showLog(String TAG, String message) {
        writeLog(TAG, message);
    }

    public void showLog(String message) {
        writeLog(TAG, message);
    }

    private void writeLog(String TAG, String message) {
        Log.d(TAG, message);
    }

    public void showToast(Context context, String message) {
        writeToast(context, message, Toast.LENGTH_SHORT);
    }

    public void showToast(Context context, String message, int duration) {
        writeToast(context, message, duration);
    }

    private void writeToast(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public void showSnackBar(View coordinatorLayout, SpannableStringBuilder snackbarText) {
        writeSnackBar(coordinatorLayout, snackbarText, Snackbar.LENGTH_LONG);
    }

    public void showSnackBar(View coordinatorLayout, SpannableStringBuilder snackbarText, int duration) {
        writeSnackBar(coordinatorLayout, snackbarText, duration);
    }

    private void writeSnackBar(View coordinatorLayout, SpannableStringBuilder snackbarText, int duration) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, snackbarText, duration)
                .setDuration(8000);
        snackbar.show();
    }

    public void startServices() {
        Dexter.withActivity(this).withPermissions(Constant.listPermission).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                final Intent intent = new Intent(getApplication(), LocationServices.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getApplication().startForegroundService(intent);
                } else {
                    getApplication().startService(intent);
                }
                getApplication().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            if (name.endsWith("BackgroundService")) {
                gpsService = ((LocationServices.LocationServiceBinder) service).getService();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("BackgroundService")) {
                gpsService = null;
            }
        }
    };

    @OnCheckedChanged(R.id.idSwitch)
    public void onSwitchActionBar(boolean isChecked) {
        showToast(getApplicationContext(), String.valueOf(isChecked));
    }

    private void sendMessage(String token, String caption, String params) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            JSONObject mainObject = new JSONObject();
            JSONObject notifObject = new JSONObject();
            JSONObject dataObject = new JSONObject();

            notifObject.put("body", caption);
            notifObject.put("title", "E-Angkot");
            notifObject.put("priority", "high");

            dataObject.put("params", params);


            mainObject.put("to", token);
            mainObject.put("notification", notifObject);
            mainObject.put("data", dataObject);
            mainObject.put("priority", "high");

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(mediaType, mainObject.toString());

            Request request = new Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "key=AAAAgSmt8Zk:APA91bGLJfXVXXCF2sTxOZtyO__1KYgAC0PtTvsB7tWWjpdPvtN3PhQU-ooFShey3ui5iKCYqbXVNzPlQNQkLme8SOzJyr5zl4S0Kt_J0gni8itV9IFxumuUo_GByv2wrOPBG5fe4DM7K8P2kLWbaW7xTYnW7MfzJQ")
                    .build();
            okhttp3.Response response = client.newCall(request).execute();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }


    }
}
