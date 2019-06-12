package com.mhandharbeni.e_angkot.main_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.model.Location;
import com.mhandharbeni.e_angkot.second_activity.user.MainActivity;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.fabSwitch)
    FloatingActionButton fabSwitch;

    @BindView(R.id.login_title)
    TextView loginTitle;

    @BindView(R.id.txtEmail)
    TextInputEditText txtEmail;

    @BindView(R.id.txtPassword)
    TextInputEditText txtPassword;

    @BindView(R.id.txtRegister)
    TextView txtRegister;

    @BindView(R.id.btnMasuk)
    Button btnMasuk;

    @BindView(R.id.ic_logo)
    ImageView icLogo;

    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;


    RotateAnimation rotate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        ButterKnife.bind(this);

        hideActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenerPref();
        initAnimation();
        initMode();
    }

    private void initMode() {
        if (getPref(Constant.MODE, "USER").equalsIgnoreCase("USER")) {
            loginTitle.setText(getResources().getString(R.string.txt_label_masuk_user));
            fabSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_angkot));
        } else {
            loginTitle.setText(getResources().getString(R.string.txt_label_masuk_driver));
            fabSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
        }
    }

    private void initAnimation() {
        rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
    }

    private void startRotate() {
        icLogo.startAnimation(rotate);
    }

    private void stopRotate() {
        icLogo.clearAnimation();
    }

    @OnClick(R.id.fabSwitch)
    public void clickSwitch() {
        if (getPref(Constant.MODE, "USER").equalsIgnoreCase("USER")) {
            setPref(Constant.MODE, "DRIVER");
        } else {
            setPref(Constant.MODE, "USER");
        }
    }

    @OnClick(R.id.txtRegister)
    public void clickRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @OnClick(R.id.btnMasuk)
    public void clickMasuk() {
        btnMasuk.setEnabled(false);
        startRotate();
        if (getPref(Constant.MODE, "USER").equalsIgnoreCase("USER")) {
            checkFirebaseAccountUser();
        } else {
            checkFirebaseAccountDriver();
        }
    }

    public void listenerPref() {
        getPref().registerOnSharedPreferenceChangeListener((encryptedPreferences, key) -> {
            if (key.equalsIgnoreCase(Constant.MODE)) {
                if (getPref(Constant.MODE, "USER").equalsIgnoreCase("USER")) {
                    loginTitle.setText(getResources().getString(R.string.txt_label_masuk_user));
                    fabSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_angkot));
                } else {
                    loginTitle.setText(getResources().getString(R.string.txt_label_masuk_driver));
                    fabSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
                }
            }
        });
    }

    private void checkFirebaseAccountUser() {
        CollectionReference user = getFirebase().getDb().collection(Constant.COLLECTION_USER);
        Query query = user.whereEqualTo("email", txtEmail.getText().toString()).whereEqualTo("password", txtPassword.getText().toString());

        Task<QuerySnapshot> querySnapshotTask = query.get();
        querySnapshotTask.addOnCompleteListener(task -> {
            if (task.getResult().size() > 0) {
                showToast(getApplicationContext(), "Sukses Login");
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    showToast(getApplicationContext(), documentSnapshot.getId());

                    setPref(Constant.ID_USER, documentSnapshot.getId());
                    setPref(Constant.ID_TOKEN, Constant.TOKEN);

                    String latitude = Constant.mLastLocation != null ? String.valueOf(Constant.mLastLocation.getLatitude()) : "0.0";
                    String longitude = Constant.mLastLocation != null ? String.valueOf(Constant.mLastLocation.getLongitude()) : "0.0";
                    Location location = new Location(documentSnapshot.getId(), latitude, longitude, true, Constant.TOKEN);
                    getFirebase().getDb().collection(Constant.COLLECTION_TRACK_USER).document(documentSnapshot.getId()).set(location);
                }
                setPref(Constant.IS_LOGGIN, true);
                startActivity(new Intent(this, MainActivity.class));
            } else {
                showToast(getApplicationContext(), "Gagal Login");
            }
            stopRotate();
            btnMasuk.setEnabled(true);
        });
    }

    private void checkFirebaseAccountDriver() {
        CollectionReference driver = getFirebase().getDb().collection(Constant.COLLECTION_DRIVER);
        Query query = driver.whereEqualTo("email", txtEmail.getText().toString()).whereEqualTo("password", txtPassword.getText().toString());
        Task<QuerySnapshot> querySnapshotTask = query.get();
        querySnapshotTask.addOnCompleteListener(task -> {
            if (task.getResult().size() > 0) {
                showToast(getApplicationContext(), "Sukses Login");
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    showToast(getApplicationContext(), documentSnapshot.getId());

                    setPref(Constant.ID_USER, documentSnapshot.getId());
                    setPref(Constant.ID_TOKEN, Constant.TOKEN);

                    String latitude = Constant.mLastLocation != null ? String.valueOf(Constant.mLastLocation.getLatitude()) : "0.0";
                    String longitude = Constant.mLastLocation != null ? String.valueOf(Constant.mLastLocation.getLongitude()) : "0.0";
                    Location location = new Location(documentSnapshot.getId(), latitude, longitude, true, Constant.TOKEN);
                    getFirebase().getDb().collection(Constant.COLLECTION_TRACK_DRIVER).document(documentSnapshot.getId()).set(location);
                }
                finish();
            } else {
                showToast(getApplicationContext(), "Gagal Login");
            }
            stopRotate();
            btnMasuk.setEnabled(true);
        });
    }

}
