package com.mhandharbeni.e_angkot.main_activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.mhandharbeni.e_angkot.CoreApplication;
import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.model.Profile;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity {
    @BindView(R.id.profile_name)
    TextView profileName;

    @BindView(R.id.txtNama)
    TextInputEditText txtNama;

    @BindView(R.id.txtAlamat)
    TextInputEditText txtAlamat;

    @BindView(R.id.txtNomorHape)
    TextInputEditText txtNomorHape;

    @BindView(R.id.btnUpdate)
    Button btnUpdate;

    @BindView(R.id.txtLabelUser)
    TextView txtLabelUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);
        hideActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDataAccount();
    }

    public void initDataAccount(){
        txtLabelUser.setText(String.valueOf(getPref(Constant.MODE, "USER")).toUpperCase());
        getFirebase().listenData(Constant.COLLECTION_PROFILE, getPref(Constant.ID_USER, "0"), documentSnapshot -> {
            if (documentSnapshot.exists()){
                profileName.setText(Objects.requireNonNull(documentSnapshot.get("nama")).toString());
                txtNama.setText(Objects.requireNonNull(documentSnapshot.get("nama")).toString());
                txtAlamat.setText(Objects.requireNonNull(documentSnapshot.get("alamat")).toString());
                txtNomorHape.setText(Objects.requireNonNull(documentSnapshot.get("nomorHp")).toString());
            }
        });
    }

    @OnClick(R.id.btnUpdate)
    public void clickUpdate(){
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
    }
}
