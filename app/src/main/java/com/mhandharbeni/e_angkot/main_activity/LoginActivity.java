package com.mhandharbeni.e_angkot.main_activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mhandharbeni.e_angkot.CoreApplication;
import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.model.Jurusan;
import com.mhandharbeni.e_angkot.model.Location;
import com.mhandharbeni.e_angkot.model.LocationDriver;
import com.mhandharbeni.e_angkot.model.Profile;
import com.mhandharbeni.e_angkot.model.Terminal;
import com.mhandharbeni.e_angkot.second_activity.user.MainActivity;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;
import com.mhandharbeni.e_angkot.utils.ToolsFirebase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

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

    @BindView(R.id.txtJurusan)
    ChipGroup txtJurusan;

    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;

    @BindView(R.id.mainLayoutChip)
    HorizontalScrollView mainLayoutChip;

    @BindView(R.id.txtPlatNo)
    TextInputEditText txtPlatNo;

    @BindView(R.id.platNoText)
    TextInputLayout platNoText;

    String checkedJurusan = null;


    RotateAnimation rotate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        ButterKnife.bind(this);

        hideActionBar();
        insertTerminal();
        insertJurusan();
    }

    private void fillInput(){
        if (Constant.CURRENT_USERNAME != null && Constant.CURRENT_PASSWORD != null){
            txtEmail.setText(Constant.CURRENT_USERNAME);
            txtPassword.setText(Constant.CURRENT_PASSWORD);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillInput();
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenerPref();
        initAnimation();
        initMode();
        syncJurusan();
        fillInput();
    }

    private void initMode() {
        if (getPref(Constant.MODE, "USER").equalsIgnoreCase("USER")) {
            loginTitle.setText(getResources().getString(R.string.txt_label_masuk_user));
            fabSwitch.setImageResource(R.drawable.ic_angkot);
            mainLayoutChip.setVisibility(View.GONE);
            platNoText.setVisibility(View.GONE);
        } else {
            loginTitle.setText(getResources().getString(R.string.txt_label_masuk_driver));
//            fabSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
            fabSwitch.setImageResource(R.drawable.ic_user);
            mainLayoutChip.setVisibility(View.VISIBLE);
            platNoText.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.txtRegister)
    public void clickRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @OnClick(R.id.btnMasuk)
    public void clickMasuk() {
        if (validateLogin()){
            btnMasuk.setEnabled(false);
            startRotate();
            if (getPref(Constant.MODE, "USER").equalsIgnoreCase("USER")) {
                checkFirebaseAccountUser();
            } else {
                checkFirebaseAccountDriver();
            }
        }
    }

    public void listenerPref() {
        getPref().registerOnSharedPreferenceChangeListener((encryptedPreferences, key) -> {
            if (key.equalsIgnoreCase(Constant.MODE)) {
                if (getPref(Constant.MODE, "USER").equalsIgnoreCase("USER")) {
                    loginTitle.setText(getResources().getString(R.string.txt_label_masuk_user));
                    fabSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_angkot));
                    mainLayoutChip.setVisibility(View.GONE);
                    platNoText.setVisibility(View.GONE);
                } else {
                    loginTitle.setText(getResources().getString(R.string.txt_label_masuk_driver));
                    fabSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
                    mainLayoutChip.setVisibility(View.VISIBLE);
                    platNoText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void syncJurusan(){
        CollectionReference jurusan = getFirebase().getDb().collection(Constant.COLLECTION_JURUSAN);
        Query query = jurusan;
        Task<QuerySnapshot> querySnapshotTask = jurusan.get();
        querySnapshotTask.addOnCompleteListener(task -> {
            if (task.getResult().size() > 0){
                for (DocumentSnapshot documentSnapshot : task.getResult()){
                    Chip layout_chip = new Chip(this, null, R.attr.chipStyle);
                    layout_chip.setClickable(true);
                    layout_chip.setCheckable(true);

                    layout_chip.setChipText(documentSnapshot.getId());
                    txtJurusan.addView(layout_chip);
                }
            }
        });

        txtJurusan.setOnCheckedChangeListener((chipGroup, i) -> {
            Chip chip = chipGroup.findViewById(chipGroup.getCheckedChipId());
            if (chip != null){
                checkedJurusan = chip.getText().toString();
            }else{
                checkedJurusan = null;
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
                logUser(txtPassword.getText().toString(), txtEmail.getText().toString());
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    showToast(getApplicationContext(), documentSnapshot.getId());

                    setPref(Constant.ID_USER, documentSnapshot.getId());
                    setPref(Constant.ID_TOKEN, Constant.TOKEN);
                    setPref(Constant.NAMA_USER, documentSnapshot.get("email").toString());

                    getFirebase().listenData(Constant.COLLECTION_PROFILE, getPref(Constant.ID_USER, "0"), documentSnapshot1 -> {
                        if (documentSnapshot1.exists()){
                            setPref(Constant.IMAGE_USER, documentSnapshot1.get("imageProfile").toString());
                            setPref(Constant.NAMA_USER, documentSnapshot1.get("nama").toString());
                            setPref(Constant.ALAMAT_USER, documentSnapshot1.get("alamat").toString());
                            setPref(Constant.PHONE_USER, documentSnapshot1.get("nomorHp").toString());
                            setPref(Constant.TYPE_USER, documentSnapshot1.get("typeUser").toString());
                        }

                        String latitude = Constant.mLastLocation != null ? String.valueOf(Constant.mLastLocation.getLatitude()) : "0.0";
                        String longitude = Constant.mLastLocation != null ? String.valueOf(Constant.mLastLocation.getLongitude()) : "0.0";
                        Location location = new Location(documentSnapshot.getId(), latitude, longitude, true, Constant.TOKEN);
                        getFirebase().getDb().collection(Constant.COLLECTION_TRACK_USER).document(documentSnapshot.getId()).set(location);

                        Profile profile = new Profile(
                                getPref(Constant.ID_USER, "0"),
                                getPref(Constant.NAMA_USER, ""),
                                getPref(Constant.ALAMAT_USER, ""),
                                getPref(Constant.IMAGE_USER, "https://firebasestorage.googleapis.com/v0/b/prototypeproject-1d503.appspot.com/o/E-ANGKOT%2Fuser.png?alt=media&token=c4e79611-7b05-4ade-9ad5-39e55e636f67"),
                                getPref(Constant.PHONE_USER, ""),
                                getPref(Constant.TYPE_USER, "").equalsIgnoreCase("USER")?Constant.TypeUser.USER:Constant.TypeUser.DRIVER
                        );
                        CoreApplication.getFirebase().getDb().collection(Constant.COLLECTION_PROFILE)
                                .document(CoreApplication.getPref().getString(Constant.ID_USER, "0"))
                                .set(profile);
                    });
                }
                setPref(Constant.IS_LOGGIN, true);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                showToast(getApplicationContext(), "Gagal Login");
            }
            stopRotate();
            btnMasuk.setEnabled(true);
        });
    }

    private void checkFirebaseAccountDriver() {
        String jurusan = checkedJurusan;

        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String platNo = txtPlatNo.getText().toString().replaceAll("\\s+","").toUpperCase();


        CollectionReference driver = getFirebase().getDb().collection(Constant.COLLECTION_DRIVER);
        Query query = driver.whereEqualTo("email", email).whereEqualTo("password", password);
        Task<QuerySnapshot> querySnapshotTask = query.get();
        querySnapshotTask.addOnCompleteListener(task -> {
            if (task.getResult().size() > 0) {
                showToast(getApplicationContext(), "Sukses Login");
                logUser(platNo, email);
                for (DocumentSnapshot documentSnapshot : task.getResult()) {

//                    showToast(getApplicationContext(), documentSnapshot.getId());

                    setPref(Constant.ID_USER, documentSnapshot.getId());
                    setPref(Constant.ID_TOKEN, Constant.TOKEN);
                    setPref(Constant.PLAT_NO, platNo);
                    setPref(Constant.ID_JURUSAN, jurusan);
                    getFirebase().listenData(Constant.COLLECTION_PROFILE, getPref(Constant.ID_USER, "0"), documentSnapshot1 -> {
                        if (documentSnapshot1.exists()){
                            setPref(Constant.IMAGE_USER, documentSnapshot1.get("imageProfile").toString());
                            setPref(Constant.NAMA_USER, documentSnapshot1.get("nama").toString());
                            setPref(Constant.ALAMAT_USER, documentSnapshot1.get("alamat").toString());
                            setPref(Constant.PHONE_USER, documentSnapshot1.get("nomorHp").toString());
                            setPref(Constant.TYPE_USER, documentSnapshot1.get("typeUser").toString());
                        }
                        String latitude = Constant.mLastLocation != null ? String.valueOf(Constant.mLastLocation.getLatitude()) : "0.0";
                        String longitude = Constant.mLastLocation != null ? String.valueOf(Constant.mLastLocation.getLongitude()) : "0.0";
                        LocationDriver location = new LocationDriver(
                                documentSnapshot.getId(),
                                latitude,
                                longitude,
                                Constant.TOKEN,
                                jurusan,
                                true,
                                platNo,
                                false,
                                CoreApplication.getPref().getString(Constant.ID_TUJUAN, "0")
                        );
                        getFirebase().getDb().collection(Constant.COLLECTION_TRACK_DRIVER).document(documentSnapshot.getId()).set(location);

                        Profile profile = new Profile(
                                getPref(Constant.ID_USER, "0"),
                                getPref(Constant.NAMA_USER, ""),
                                getPref(Constant.ALAMAT_USER, ""),
                                getPref(Constant.IMAGE_USER, "https://firebasestorage.googleapis.com/v0/b/prototypeproject-1d503.appspot.com/o/E-ANGKOT%2Fuser.png?alt=media&token=c4e79611-7b05-4ade-9ad5-39e55e636f67"),
                                getPref(Constant.PHONE_USER, ""),
                                getPref(Constant.TYPE_USER, "").equalsIgnoreCase("USER")?Constant.TypeUser.USER:Constant.TypeUser.DRIVER
                        );
                        CoreApplication.getFirebase().getDb().collection(Constant.COLLECTION_PROFILE)
                                .document(CoreApplication.getPref().getString(Constant.ID_USER, "0"))
                                .set(profile);
                    });
                }
                setPref(Constant.IS_LOGGIN, true);
                startActivity(new Intent(this, com.mhandharbeni.e_angkot.second_activity.driver.MainActivity.class));
                finish();
            } else {
                showToast(getApplicationContext(), "Gagal Login");
            }
            stopRotate();
            btnMasuk.setEnabled(true);
        });
    }

    private boolean validateLogin(){
        if (txtEmail.getText().toString().isEmpty()){
            txtEmail.setError("Tidak Bisa Kosong");
            return false;
        }
        if (txtPassword.getText().toString().isEmpty()){
            txtPassword.setError("Tidak Bisa Kosong");
            return false;
        }
        if (!getPref(Constant.MODE, "USER").equalsIgnoreCase("USER")){
            if (checkedJurusan == null){
                showSnackBar(mainLayout, new SpannableStringBuilder().append("Jurusan Belum dipilih"));
                return false;
            }
            if (txtPlatNo.getText().toString().isEmpty()){
                txtPlatNo.setError("Tidak Boleh Kosong");
                return false;
            }
        }

        return true;
    }

    private void insertJurusan(){
        String[] aJurusan = new String[]{
                "AMG",
                "ABG",
                "GA",
                "AG",
                "TST",
                "CKL",
                "ADL",
                "AL",
                "AT",
                "AJG",
                "ASD",
                "LDG",
                "LG",
                "GML",
                "GL",
                "GM",
                "TSG",
                "MK",
                "MM",
                "PBB",
                "TAT",
                "JPK",
                "JDM",
                "MKS",
                "MT"
        };

        for (String jurusan : aJurusan){
            Jurusan mJurusan = new Jurusan();
            mJurusan.setId(jurusan);

            getFirebase().getDb().collection(Constant.COLLECTION_JURUSAN).document(jurusan).set(mJurusan);
        }
    }

    private void insertTerminal(){
        String[] aTerminal = new String[]{
                "Terminal Arjosari",
                "Terminal Landungsari",
                "Terminal Gadang",
                "Terminal Hamid Rusdi",
                "Terminal Madyopuro",
                "Terminal Mulyorejo",
                "Terminal Batu",
                "Terminal Kepanjen",
                "Terminal Dampit",
                "Terminal Wajak",
                "Terminal Lawang",
                "Terminal Tumpang"
        };
        for (String terminal : aTerminal){
            Terminal mTerminal = new Terminal();
            mTerminal.setId(terminal);

            getFirebase().getDb().collection(Constant.COLLECTION_TERMINAL).document(terminal).set(mTerminal);
        }
    }
}
