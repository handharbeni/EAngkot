package com.mhandharbeni.e_angkot.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mhandharbeni.e_angkot.CoreApplication;
import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.main_activity.LoginActivity;
import com.mhandharbeni.e_angkot.model.LocationDriver;
import com.mhandharbeni.e_angkot.model.Profile;
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

    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private final int LOCATION_INTERVAL = 2000;
    private final int LOCATION_DISTANCE = 10;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection();
        initActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenerSwitch();
    }

    public ToolsFirebase getFirebase() {
        return CoreApplication.getFirebase();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_angkot);
    }

    private void checkConnection(){
        if (!Constant.isInternetAvailable(getApplicationContext())){
            Constant.displayNoInternet(BaseActivity.this);
        }
    }

    @OnClick(R.id.idProfile)
    public void onProfileClick(){
//        startActivity(new Intent(this, ProfileActivity.class));
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.activity_profile, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        TextView txtLabelUser = sheetView.findViewById(R.id.txtLabelUser);
        TextView profileName = sheetView.findViewById(R.id.profile_name);
        TextInputEditText txtNama = sheetView.findViewById(R.id.txtNama);
        TextInputEditText txtAlamat = sheetView.findViewById(R.id.txtAlamat);
        TextInputEditText txtNomorHape = sheetView.findViewById(R.id.txtNomorHape);
        Button btnUpdate = sheetView.findViewById(R.id.btnUpdate);
        Button btnKeluar = sheetView.findViewById(R.id.btnLogout);

        txtLabelUser.setText(String.valueOf(getPref(Constant.MODE, "USER")).toUpperCase());

        getFirebase().listenData(Constant.COLLECTION_PROFILE, getPref(Constant.ID_USER, "0"), documentSnapshot -> {
            if (documentSnapshot.exists()){
                profileName.setText(Objects.requireNonNull(documentSnapshot.get("nama")).toString());
                txtNama.setText(Objects.requireNonNull(documentSnapshot.get("nama")).toString());
                txtAlamat.setText(Objects.requireNonNull(documentSnapshot.get("alamat")).toString());
                txtNomorHape.setText(Objects.requireNonNull(documentSnapshot.get("nomorHp")).toString());
            }
        });

        btnUpdate.setOnClickListener(view -> {
            Profile profile = new Profile(
                    getPref(Constant.ID_USER, "0"),
                    txtNama.getText().toString(),
                    txtAlamat.getText().toString(),
                    "",
                    txtNomorHape.getText().toString(),
                    Constant.TypeUser.USER
            );
            CoreApplication.getFirebase().getDb().collection(Constant.COLLECTION_PROFILE)
                    .document(CoreApplication.getPref().getString(Constant.ID_USER, "0"))
                    .set(profile);

            mBottomSheetDialog.dismiss();
        });

        btnKeluar.setOnClickListener(view -> {
            setPref(Constant.IS_LOGGIN, false);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
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

    public void hideSwitchActionBar(){ idSwitch.setVisibility(View.GONE); }

    public EncryptedPreferences getPref() {
        return CoreApplication.getPref();
    }

    public void setPref(String key, String value) {
        getPref().edit()
                .putString(key, value)
                .apply();
    }

    public void setPref(String key, boolean value) {
        Log.d(TAG, "setPref: "+value);
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
//        Toast.makeText(context, message, duration).show();
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
        setPref(Constant.DRIVER_ISACTIVE, isChecked);
    }

    private void listenerSwitch(){
        idSwitch.setChecked(getPref(Constant.DRIVER_ISACTIVE, false));
    }

    public void sendMessage(String token, String caption, String params) {
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

    public BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void startTracking() {
        initializeLocationManager();
        mLocationListener = new LocationListener("fused");

        try {
            mLocationManager.requestLocationUpdates("fused", LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);

        } catch (java.lang.SecurityException ex) {
            // Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            // Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            }
        }
    }

    private class LocationListener implements android.location.LocationListener {
        private Location lastLocation = null;
        private final String TAG = "LocationListener";
        private Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;
            Constant.mLastLocation = mLastLocation;
            if (!CoreApplication.getPref().getString(Constant.ID_USER, "0").equalsIgnoreCase("0")) {
                String collection = CoreApplication.getPref().getString(Constant.MODE, "USER").equalsIgnoreCase("user") ? Constant.COLLECTION_TRACK_USER : Constant.COLLECTION_TRACK_DRIVER;
                if (CoreApplication.getPref().getString(Constant.MODE, "USER").equalsIgnoreCase("user")){
                    com.mhandharbeni.e_angkot.model.Location location1 = new com.mhandharbeni.e_angkot.model.Location(
                            CoreApplication.getPref().getString(Constant.ID_USER, "0"),
                            String.valueOf(mLastLocation.getLatitude()),
                            String.valueOf(mLastLocation.getLongitude()),
                            true,
                            CoreApplication.getPref().getString(Constant.ID_TOKEN, "0")
                    );
                    CoreApplication.getFirebase().getDb().collection(collection).document(CoreApplication.getPref().getString(Constant.ID_USER, "0")).set(location1);
                }else{
                    LocationDriver locationDriver = new LocationDriver(
                            CoreApplication.getPref().getString(Constant.ID_USER, "0"),
                            String.valueOf(mLastLocation.getLatitude()),
                            String.valueOf(mLastLocation.getLongitude()),
                            CoreApplication.getPref().getString(Constant.ID_TOKEN, "0"),
                            CoreApplication.getPref().getString(Constant.ID_JURUSAN, "0"),
                            true,
                            CoreApplication.getPref().getString(Constant.PLAT_NO, "N/A"),
                            CoreApplication.getPref().getBoolean(Constant.DRIVER_ISACTIVE, false)
                    );
                    CoreApplication.getFirebase().getDb().collection(collection).document(CoreApplication.getPref().getString(Constant.ID_USER, "0")).set(locationDriver);
                }
            }
            Log.i(TAG, "BaseActivity LocationChanged: " + location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "BaseActivity onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "BaseActivity onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "BaseActivity onStatusChanged: " + status);
        }
    }
}
