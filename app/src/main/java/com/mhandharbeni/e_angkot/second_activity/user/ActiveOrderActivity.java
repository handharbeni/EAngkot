package com.mhandharbeni.e_angkot.second_activity.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.model.Room;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveOrderActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {
    private GoogleMap mMap;

    @BindView(R.id.txtPlatNo)
    TextView txtPlatNo;
    @BindView(R.id.txtJurusan)
    TextView txtJurusan;
    @BindView(R.id.fabDone)
    FloatingActionButton fabDone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_useractiveorder);

        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        hideSwitchActionBar();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(this);
        centerMaps();
        setLabel();
    }
    private void centerMaps() {
        if (mMap.getMyLocation() != null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void setLabel(){
        txtPlatNo.setText(String.valueOf(getPref(Constant.ACTIVE_ORDER_PLATNO, "0")).toUpperCase());
        txtJurusan.setText(String.valueOf(getPref(Constant.ACTIVE_ORDER_JURUSAN, "0")).toUpperCase());
    }

    @Override
    public void onMyLocationChange(Location location) {
        centerMaps();
    }

    @OnClick(R.id.fabDone)
    public void clickDone(){
        String idUser = getPref(Constant.ID_USER, "0");
        String userCount = "idUser"+getPref(Constant.ACTIVE_ORDER_COUNT, "0");
        int count = getPref(Constant.ACTIVE_ORDER_COUNT, 0);
        String platNo = getPref(Constant.ACTIVE_ORDER_PLATNO, "0");
        String jurusan = getPref(Constant.ACTIVE_ORDER_JURUSAN, "0");
        String idDriver = getPref(Constant.ACTIVE_ORDER_IDDRIVER, "0");

        HashMap<String, String> listUser = new HashMap<>();
        getFirebase().getDb().collection(Constant.COLLECTION_ROOM).document(platNo).get().addOnCompleteListener(task -> {
            listUser.clear();
            listUser.putAll((Map<String,String>) task.getResult().get("listUser"));
            listUser.put(userCount, null);

            Room room = new Room();
            room.setPlatNo(platNo);
            room.setJurusan(jurusan);
            room.setCount(count - 1);
            room.setIdDriver(idDriver);
            room.setListUser(listUser);

            setPref(Constant.ACTIVE_ORDER_JURUSAN, "null");
            setPref(Constant.ACTIVE_ORDER_COUNT, "null");
            setPref(Constant.ACTIVE_ORDER_IDDRIVER, "null");
            setPref(Constant.ACTIVE_ORDER_PLATNO, "null");
            setPref(Constant.STATE_ORDER, false);

            getFirebase().getDb().collection(Constant.COLLECTION_ROOM).document(platNo).set(room);

            startActivity(new Intent(ActiveOrderActivity.this, MainActivity.class));
            finish();
        });
    }
}
