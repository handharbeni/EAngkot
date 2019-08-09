package com.mhandharbeni.e_angkot.main_activity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mhandharbeni.e_angkot.CoreApplication;
import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.model.Profile;
import com.mhandharbeni.e_angkot.model.User;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.txtEmail)
    TextInputEditText txtEmail;

    @BindView(R.id.txtPassword)
    TextInputEditText txtPassword;

    @BindView(R.id.txtPasswordRepeat)
    TextInputEditText txtPasswordRepeat;

    @BindView(R.id.txtMasuk)
    TextView txtMasuk;

    @BindView(R.id.btnDaftar)
    Button btnDaftar;

    @BindView(R.id.register_title)
    TextView registerTitle;

    @BindView(R.id.fabSwitch)
    FloatingActionButton fabSwitch;

    @BindView(R.id.ic_logo)
    ImageView icLogo;

    RotateAnimation rotate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

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
            registerTitle.setText(getResources().getString(R.string.txt_label_user));
        } else {
            registerTitle.setText(getResources().getString(R.string.txt_label_driver));
        }
    }

    private void initAnimation() {
        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
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

    @OnClick(R.id.txtMasuk)
    public void clickMasuk() {
        finish();
    }

    @OnClick(R.id.btnDaftar)
    public void clickDaftar() {
        btnDaftar.setEnabled(false);
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
                    registerTitle.setText(getResources().getString(R.string.txt_label_user));
                    fabSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_angkot));
                } else {
                    registerTitle.setText(getResources().getString(R.string.txt_label_driver));
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
                showToast(getApplicationContext(), "User Sudah Ada");
            } else {
                doRegister();
            }
            stopRotate();
            btnDaftar.setEnabled(true);
        });
    }

    private void checkFirebaseAccountDriver() {
        CollectionReference driver = getFirebase().getDb().collection(Constant.COLLECTION_DRIVER);
        Query query = driver.whereEqualTo("email", txtEmail.getText().toString()).whereEqualTo("password", txtPassword.getText().toString());
        Task<QuerySnapshot> querySnapshotTask = query.get();
        querySnapshotTask.addOnCompleteListener(task -> {
            if (task.getResult().size() > 0) {
                showToast(getApplicationContext(), "User Sudah Ada");
            } else {
                doRegister();
            }
            stopRotate();
            btnDaftar.setEnabled(true);
        });
    }

    private void doRegister() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String repeatPassword = txtPasswordRepeat.getText().toString();
        User user = new User(email, password, "https://firebasestorage.googleapis.com/v0/b/prototypeproject-1d503.appspot.com/o/E-ANGKOT%2Fuser.png?alt=media&token=c4e79611-7b05-4ade-9ad5-39e55e636f67");
        if (password.equalsIgnoreCase(repeatPassword)) {
            String id = getFirebase()
                    .getDb()
                    .collection(
                            getPref(Constant.MODE, "USER").equalsIgnoreCase("USER") ? Constant.COLLECTION_USER : Constant.COLLECTION_DRIVER
                    )
                    .document()
                    .getId();
            getFirebase()
                    .getDb()
                    .collection(
                            getPref(Constant.MODE, "USER")
                                    .equalsIgnoreCase("USER") ? Constant.COLLECTION_USER : Constant.COLLECTION_DRIVER
                    )
                    .document(id)
                    .set(user);

            Constant.CURRENT_USERNAME = email;
            Constant.CURRENT_PASSWORD = password;

            finish();
        } else {
            txtPasswordRepeat.setError("Password Tidak Sama");
        }
    }
}
