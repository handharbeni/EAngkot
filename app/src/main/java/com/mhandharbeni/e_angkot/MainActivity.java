package com.mhandharbeni.e_angkot;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.widget.Switch;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.mhandharbeni.e_angkot.utils.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.switch1)
    Switch aSwitch;

    @BindView(R.id.mainLayout)
    ConstraintLayout mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        SpannableStringBuilder snackbarText = new SpannableStringBuilder();
        snackbarText.append("Selamat Datang");
        showSnackBar(mainLayout, snackbarText);

        idTitle.setText("Test Text");
    }

    @OnClick(R.id.switch1)
    public void onSwitch(){
        if (aSwitch.isChecked()){
            showActionBar();
        } else {
            hideActionBar();
        }
    }

    @OnCheckedChanged(R.id.switch1)
    public void onChangeSwitch(boolean isChecked){
        if (isChecked){
            showActionBar();
        }else{
            hideActionBar();
        }
    }
}
