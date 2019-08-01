package com.mhandharbeni.e_angkot.second_activity.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.model.RatingAngkot;
import com.mhandharbeni.e_angkot.model.RatingDriver;
import com.mhandharbeni.e_angkot.model.Room;
import com.mhandharbeni.e_angkot.model.TravelHistory;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;
import com.mhandharbeni.e_angkot.utils.ToolsFirebase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    @BindView(R.id.txtTujuan)
    TextView txtTujuan;
    @BindView(R.id.fabDone)
    FloatingActionButton fabDone;

    BottomAppBar bottomAppBar;
    String phoneNumberDriver = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_useractiveorder);

        Dexter.withActivity(this).withPermissions(Constant.listPermission).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();

        fillInformation();

        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.chat:
                    showToast(getApplicationContext(), "Future Release");
                    break;
                case R.id.call :
                    if (phoneNumberDriver != null){
                        Intent intent = new Intent(Intent.ACTION_CALL);

                        intent.setData(Uri.parse("tel:" + phoneNumberDriver));
                        startActivity(intent);
                    }
                    break;
            }
            return false;
        });


        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        hideSwitchActionBar();
    }

    private void fillInformation(){
        getFirebase().listenData(Constant.COLLECTION_PROFILE, getPref(Constant.ACTIVE_ORDER_IDDRIVER, "0"), documentSnapshot -> {
            if (documentSnapshot.get("nomorHp") != null){
                phoneNumberDriver = documentSnapshot.get("nomorHp").toString();
            }
        });
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
        txtTujuan.setText(String.valueOf(getPref(Constant.ACTIVE_ORDER_TUJUAN, "0")).toUpperCase());
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
        String tujuan = getPref(Constant.ACTIVE_ORDER_TUJUAN, "0");
        String idDriver = getPref(Constant.ACTIVE_ORDER_IDDRIVER, "0");

        HashMap<String, String> listUser = new HashMap<>();
        getFirebase().getDb().collection(Constant.COLLECTION_ROOM).document(platNo).get().addOnCompleteListener(task -> {
            listUser.clear();
            listUser.putAll((Map<String,String>) task.getResult().get("listUser"));
            listUser.put(userCount, null);

            Room room = new Room();
            room.setPlatNo(platNo);
            room.setJurusan(jurusan);
            room.setTujuan(tujuan);
            room.setCount(count - 1);
            room.setIdDriver(idDriver);
            room.setListUser(listUser);

            getFirebase().getDb().collection(Constant.COLLECTION_ROOM).document(platNo).set(room);
            saveTravelHistory();
            setRating();
        });
    }

    private void setRating(){
        String platNo = getPref(Constant.ACTIVE_ORDER_PLATNO, "0");
        String idDriver = getPref(Constant.ACTIVE_ORDER_IDDRIVER, "0");
        String idUser = getPref(Constant.ID_USER, "0");


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActiveOrderActivity.this);
        View viewSheet = getLayoutInflater().inflate(R.layout.layout_rating, null);
        bottomSheetDialog.setContentView(viewSheet);
        bottomSheetDialog.show();

        RatingBar ratingDriver = viewSheet.findViewById(R.id.ratingDriver);
        RatingBar ratingAngkot = viewSheet.findViewById(R.id.ratingAngkot);
        AppCompatButton btnSimpan = viewSheet.findViewById(R.id.btnSimpan);

        btnSimpan.setOnClickListener(view->{
            float fRatingDriver = ratingDriver.getRating();
            float fRatingAngkot = ratingAngkot.getRating();

            RatingDriver mRatingDriver = new RatingDriver();
            mRatingDriver.setIdDriver(idDriver);
            mRatingDriver.setIdUser(idUser);
            mRatingDriver.setRate(fRatingDriver);

            getFirebase().getDb().collection(Constant.COLLECTION_RATING_DRIVER).document().set(mRatingDriver);

            RatingAngkot mRatingAngkot = new RatingAngkot();
            mRatingAngkot.setPlatNo(platNo);
            mRatingAngkot.setIdUser(idUser);
            mRatingAngkot.setRate(fRatingAngkot);

            getFirebase().getDb().collection(Constant.COLLECTION_RATING_ANGKOT).document().set(mRatingAngkot);

            setPref(Constant.ACTIVE_ORDER_JURUSAN, "null");
            setPref(Constant.ACTIVE_ORDER_COUNT, "null");
            setPref(Constant.ACTIVE_ORDER_IDDRIVER, "null");
            setPref(Constant.ACTIVE_ORDER_PLATNO, "null");
            setPref(Constant.STATE_ORDER, false);

            startActivity(new Intent(ActiveOrderActivity.this, MainActivity.class));
            finish();
        });
    }

    private void saveTravelHistory(){
        try {
            String key = getFirebase().getDb().collection(Constant.COLLECTION_TRAVEL_HISTORY).getId();

            String platNo = getPref(Constant.ACTIVE_ORDER_PLATNO, "0");
            String idDriver = getPref(Constant.ACTIVE_ORDER_IDDRIVER, "0");
            String idUser = getPref(Constant.ID_USER, "0");


            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude(), 1);

            String address = addresses.get(0).getAddressLine(0);

            TravelHistory travelHistory = new TravelHistory();
            travelHistory.setIdUser(idUser);
            travelHistory.setEndDestination(address);
            travelHistory.setIdDriver(idDriver);
            travelHistory.setPlatNo(platNo);
            travelHistory.setDateMillis(System.currentTimeMillis());
            travelHistory.setNamaUser(getPref(Constant.NAMA_USER, "0"));

            getFirebase().getDb().collection(Constant.COLLECTION_TRAVEL_HISTORY).document().set(travelHistory);
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
