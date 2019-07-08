package com.mhandharbeni.e_angkot.second_activity.user;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mhandharbeni.e_angkot.CoreApplication;
import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.model.ActiveOrder;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import illiyin.mhandharbeni.libraryroute.Navigation;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, Navigation.NavigationListener, LocationListener {
    private GoogleMap mMap;

    @BindView(R.id.fabOrder)
    FloatingActionButton fabOrder;

    @BindView(R.id.mainLayout)
    CoordinatorLayout mainLayout;

    HashMap<String, LatLng> listDriver = new HashMap<>();


    String checkedJurusan = null;
    public static String idDocument;
    public static boolean activeOrder = false;

    public ListenerRegistration trackDriver;

    public Navigation navigation;

    LocationManager locationManager;
    Location location;
    Criteria criteria;
    String provider;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startServices();
        setContentView(R.layout.mainactivity_user);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        hideSwitchActionBar();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.withActivity(this).withPermissions(Constant.listPermission).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);

                    navigation = new Navigation();

                    navigation.setMap(mMap);
                    navigation.setContext(getApplicationContext());
                    navigation.setActivity(MainActivity.this);
                    navigation.setListener(MainActivity.this);
                    navigation.setKey(Constant.API_MAPS);

                    centerMaps();

                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    criteria.setPowerRequirement(Criteria.POWER_HIGH);
                    criteria.setAltitudeRequired(true);
                    criteria.setSpeedRequired(true);
                    criteria.setCostAllowed(true);
                    criteria.setBearingRequired(true);

                    //API level 9 and up
                    criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                    criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                    criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
                    criteria.setSpeedAccuracy(Criteria.ACCURACY_MEDIUM);
                    provider = locationManager.getBestProvider(criteria, true);
                    locationManager.requestLocationUpdates(provider, 1000, 500, MainActivity.this);
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                }
            }).check();

        }
    }
    private void centerMaps() {
        if (location != null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        listenOrder();
        listenerPref();
    }
    @OnClick(R.id.fabOrder)
    public void clickOrder(){
        if (activeOrder){
            String idUser = getPref(Constant.ID_USER, "0");
            String idJurusan = checkedJurusan;
            boolean isActive = false;
            ActiveOrder activeOrder = new ActiveOrder(idUser, idJurusan, isActive);

            CollectionReference user = getFirebase().getDb().collection(Constant.COLLECTION_ORDER);
            Query queryActiveOrder = user.whereEqualTo("idUser", idUser);
            Task<QuerySnapshot> queryTaskActiveOrder = queryActiveOrder.get();
            queryTaskActiveOrder.addOnCompleteListener(task -> {
                if (task.getResult().size() > 0){
                    CoreApplication
                            .getFirebase()
                            .getDb()
                            .collection(Constant.COLLECTION_ORDER)
                            .document(task.getResult().getDocuments().get(0).getId())
                            .set(activeOrder);
                }
            });
            checkedJurusan = null;
            navigation.clearMaps();
            centerMaps();
        }else{
            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
            View sheetView = getLayoutInflater().inflate(R.layout.layout_pilihjurusan, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();

            ChipGroup txtJurusan = sheetView.findViewById(R.id.txtJurusan);
            AppCompatButton btnOrder = sheetView.findViewById(R.id.btnOrder);


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
            btnOrder.setOnClickListener(view -> {
                if (checkedJurusan == null){
                    showSnackBar(mainLayout, new SpannableStringBuilder().append("Angkot Belum Di pilih"));
                    mBottomSheetDialog.dismiss();
                    return;
                }
                String idUser = getPref(Constant.ID_USER, "0");
                String idJurusan = checkedJurusan;
                boolean isActive = !activeOrder;
                ActiveOrder activeOrder = new ActiveOrder(idUser, idJurusan, isActive);

                idDocument = CoreApplication.getFirebase().getDb().collection(Constant.COLLECTION_ORDER).document().getId();


                CollectionReference user = getFirebase().getDb().collection(Constant.COLLECTION_ORDER);
                Query queryActiveOrder = user.whereEqualTo("idUser", idUser);
                Task<QuerySnapshot> queryTaskActiveOrder = queryActiveOrder.get();
                queryTaskActiveOrder.addOnCompleteListener(task -> {
                    if (task.getResult().size() > 0){
                        idDocument = task.getResult().getDocuments().get(0).getId();
                    }
                    CoreApplication
                            .getFirebase()
                            .getDb()
                            .collection(Constant.COLLECTION_ORDER)
                            .document(idDocument)
                            .set(activeOrder);
                    sendNotificationToDriver();
                });


                mBottomSheetDialog.dismiss();
            });
        }
    }
    private void sendNotificationToDriver(){
        if (checkedJurusan != null){

            String jurusan = checkedJurusan;

            CollectionReference user = getFirebase().getDb().collection(Constant.COLLECTION_TRACK_DRIVER);
            Query query = user.whereEqualTo("jurusan", jurusan);

            trackDriver = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots.getDocuments().size()>0){
                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                        LatLng latLngDriver = new LatLng(
                                Double.valueOf(documentSnapshot.get("latitude").toString()),
                                Double.valueOf(documentSnapshot.get("longitude").toString())
                        );
                        listDriver.put(documentSnapshot.get("platNo").toString(), latLngDriver);

//                        sendMessage(documentSnapshot.get("token").toString(), getPref(Constant.NAMA_USER, "Jhon Doe")+" Sedang Mencari "+jurusan, "");
                    }
                    setTrack();
                }
            });
        }
    }
    private void setTrack(){
        navigation.clearMaps();
        mMap.clear();
        Log.d(TAG, "sendNotificationToDriver: "+location);
        if (location != null){
            if (listDriver.size() > 0){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    listDriver.forEach((s, latLng) -> {
                        Log.d(TAG, "setTrack: "+s+" "+latLng.latitude+" "+latLng.longitude);
                        LatLng startLocation = new LatLng(
                                location.getLatitude(),
                                location.getLongitude()
                        );
                        LatLng endLocation = new LatLng(latLng.latitude, latLng.longitude);
                        navigation.setStartLocation(startLocation);
                        navigation.setEndPosition(endLocation);

                        navigation.setTitleStart("Anda");
                        navigation.setTitleEnd(s);

                        navigation.setMarkerStart(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_user));
                        navigation.setMarkerEnd(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_angkot));

                        navigation.find(false,false);
                    });
                }else{
                    for (Map.Entry<String, LatLng> entry : listDriver.entrySet()){
                        Log.d(TAG, "setTrack: 2 "+entry.getKey()+" "+entry.getValue().latitude+" "+entry.getValue().longitude);
                        LatLng startLocation = new LatLng(
                                location.getLatitude(),
                                location.getLongitude()
                        );
                        LatLng endLocation = new LatLng(entry.getValue().latitude, entry.getValue().longitude);
                        navigation.setStartLocation(startLocation);
                        navigation.setEndPosition(endLocation);

                        navigation.setTitleStart("Anda");
                        navigation.setTitleEnd(entry.getKey());

                        navigation.setMarkerStart(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_user));
                        navigation.setMarkerEnd(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_angkot));

                        navigation.find(false,false);
                    }
                }
            }else{
                centerMaps();
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            locationManager.requestLocationUpdates(provider, 1000, 500, this);
        }
    }
    private void listenOrder(){
        String idUser = getPref(Constant.ID_USER, "0");

        CollectionReference user = getFirebase().getDb().collection(Constant.COLLECTION_ORDER);
        Query query = user.whereEqualTo("idUser", idUser);

        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots.getDocuments().size()>0){
                if ((boolean)queryDocumentSnapshots.getDocuments().get(0).get("isActive")){
                    fabOrder.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                    activeOrder = true;
                }else{
                    fabOrder.setImageDrawable(getResources().getDrawable(R.drawable.ic_angkot));
                    activeOrder = false;
                }
            }
        });

    }
    private void listenerPref(){
//        getPref().registerOnSharedPreferenceChangeListener((encryptedPreferences, key) -> {
//            if(key.equalsIgnoreCase(Constant.MY_LATITUDE) || key.equalsIgnoreCase(Constant.MY_LONGITUDE)){
//                setTrack();
//            }
//        });
    }
    @Override
    public void onCompleteLoad(int i, int i1) {

    }
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (
                !getPref(Constant.MY_OLD_LATITUDE, "0").equalsIgnoreCase(String.valueOf(location.getLatitude())) &&
                        !getPref(Constant.MY_OLD_LONGITUDE, "0").equalsIgnoreCase(String.valueOf(location.getLongitude()))
        ) {
            setPref(Constant.MY_OLD_LATITUDE, String.valueOf(location.getLatitude()));
            setPref(Constant.MY_OLD_LONGITUDE, String.valueOf(location.getLongitude()));
            setTrack();
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    protected void onStop() {
        locationManager.removeUpdates(this);
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        locationManager.removeUpdates(this);
        super.onDestroy();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        locationManager.requestLocationUpdates(provider, 10000, 5000, this);
    }

}
