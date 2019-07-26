package com.mhandharbeni.e_angkot.second_activity.user;

import android.Manifest;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
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
import com.mhandharbeni.e_angkot.model.Room;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import illiyin.mhandharbeni.libraryroute.Navigation;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, Navigation.NavigationListener, LocationListener, GoogleMap.OnMyLocationChangeListener {
    private GoogleMap mMap;

    @BindView(R.id.fabOrder)
    FloatingActionButton fabOrder;

    @BindView(R.id.mainLayout)
    CoordinatorLayout mainLayout;

    BottomAppBar bottomAppBar;

    HashMap<String, LatLng> listDriver = new HashMap<>();
    HashMap<String, Navigation> listNavigation = new HashMap<>();


    String checkedJurusan = null;
    String checkAngkot = null;
    public static String idDocument;
    public static boolean activeOrder = false;

    public ListenerRegistration trackDriver;
    public ListenerRegistration trackRoom;

//    public Navigation navigation;

    LocationManager locationManager;
    Location location;
    Criteria criteria;
    String provider;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startServices();
        setContentView(R.layout.mainactivity_user);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.queue:
                    listPlatDriver();
                    break;
            }
            return false;
        });
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        hideSwitchActionBar();

        setToActivePage();
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
                    mMap.setOnMyLocationChangeListener(MainActivity.this);
//                    mMap.my


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

                    centerMaps();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                }
            }).check();

        }
    }

    private void centerMaps() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            mMap.setMyLocationEnabled(true);
            Location myLocation = location;
            if (myLocation == null){
                myLocation = mMap.getMyLocation();
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }catch (Exception ignored){}
    }
    @Override
    protected void onStart() {
        super.onStart();
        clearOrder();
        listenOrder();
    }
    @OnClick(R.id.fabOrder)
    public void clickOrder(){
        String idUser = getPref(Constant.ID_USER, "0");
        if (activeOrder){
            activeOrder = false;
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
            clearOrder();
            centerMaps();
        }else{
            activeOrder = true;
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
                String idJurusan = checkedJurusan;
                boolean isActive = activeOrder;
                ActiveOrder activeOrder = new ActiveOrder(idUser, idJurusan, isActive);

                idDocument = CoreApplication.getFirebase().getDb().collection(Constant.COLLECTION_ORDER).document().getId();


                CollectionReference user = getFirebase().getDb().collection(Constant.COLLECTION_ORDER);
                Query queryActiveOrder = user.whereEqualTo("idUser", idUser);
                Task<QuerySnapshot> queryTaskActiveOrder = queryActiveOrder.get();
                queryTaskActiveOrder.addOnCompleteListener(task -> {
                    CoreApplication
                            .getFirebase()
                            .getDb()
                            .collection(Constant.COLLECTION_ORDER)
                            .document(idUser)
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
                if (activeOrder){
                    if (queryDocumentSnapshots.getDocuments().size()>0){
                        listDriver.clear();
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                            if (documentSnapshot.get("isActive") != null){
                                if ((boolean)documentSnapshot.get("isActive")){
                                    LatLng latLngDriver = new LatLng(
                                            Double.valueOf(documentSnapshot.get("latitude").toString()),
                                            Double.valueOf(documentSnapshot.get("longitude").toString())
                                    );
                                    listDriver.put(documentSnapshot.get("platNo").toString(), latLngDriver);
                                }
                            }

//                        sendMessage(documentSnapshot.get("token").toString(), getPref(Constant.NAMA_USER, "Jhon Doe")+" Sedang Mencari "+jurusan, "");
                        }
                        setTrack();
                    }
                }else{
                    showToast(getApplicationContext(), "Active Order False");
                }
            });
        }
    }
    private void setTrack(){
        Log.d(TAG, "setTrack: RUNNING");
        Log.d(TAG, "setTrack: RUNNING "+listDriver.size());
        if (listNavigation.size() > 0){
            for (Map.Entry<String, Navigation> listNav : listNavigation.entrySet()){
                listNav.getValue().clearMaps();
            }
        }
        mMap.clear();
        if (location != null){
            if (listDriver.size() > 0){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    listDriver.forEach((s, latLng) -> {
                        if (activeOrder){

                            Navigation navigation = new Navigation();

                            navigation.setMap(mMap);
                            navigation.setContext(getApplicationContext());
                            navigation.setActivity(MainActivity.this);
                            navigation.setListener(MainActivity.this);
                            navigation.setKey(Constant.API_MAPS);

                            LatLng startLocation = new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude()
                            );
                            LatLng endLocation = new LatLng(latLng.latitude, latLng.longitude);

                            navigation.setStartLocation(startLocation);
                            navigation.setEndPosition(endLocation);

                            navigation.setTitleStart("Anda");
                            navigation.setTitleEnd(checkedJurusan+"-"+s);

                            navigation.setMarkerStart(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_user));
                            navigation.setMarkerEnd(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_angkot));

                            navigation.clearMaps();

                            navigation.find(false,false);
                            listNavigation.put(s, navigation);
                        }
                    });
                }else{
                    for (Map.Entry<String, LatLng> entry : listDriver.entrySet()){
                        if (activeOrder){

                            Navigation navigation = new Navigation();

                            navigation.setMap(mMap);
                            navigation.setContext(getApplicationContext());
                            navigation.setActivity(MainActivity.this);
                            navigation.setListener(MainActivity.this);
                            navigation.setKey(Constant.API_MAPS);

                            LatLng startLocation = new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude()
                            );
                            LatLng endLocation = new LatLng(entry.getValue().latitude, entry.getValue().longitude);
                            navigation.setStartLocation(startLocation);
                            navigation.setEndPosition(endLocation);

                            navigation.setTitleStart("Anda");
                            navigation.setTitleEnd(checkedJurusan+"-"+entry.getKey());

                            navigation.setMarkerStart(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_user));
                            navigation.setMarkerEnd(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_angkot));
                            navigation.clearMaps();

                            navigation.find(false,false);
                            listNavigation.put(entry.getKey(), navigation);
                        }
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

    @Override
    protected void onResume() {
        super.onResume();
        clearOrder();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearOrder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearOrder();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearOrder();
    }

    private void clearOrder(){
        if (trackDriver != null){
            trackDriver.remove();
        }
        if (trackRoom !=null){
            trackRoom.remove();
        }
        listDriver.clear();
        if (listNavigation.size() > 0){
            for (Map.Entry<String, Navigation> listNav : listNavigation.entrySet()){
                Log.d(TAG, "clearOrder "+listNav.getKey());
                listNav.getValue().clearMaps();
            }
        }
        if (mMap != null){
            mMap.clear();
        }

        activeOrder = false;
        String idUser = getPref(Constant.ID_USER, "0");
        boolean isActive = false;
        ActiveOrder activeOrder = new ActiveOrder(idUser, "", isActive);
        getFirebase().getDb().collection(Constant.COLLECTION_ORDER).document(idUser).set(activeOrder);
    }
    private void listenOrder(){
        String idUser = getPref(Constant.ID_USER, "0");

        CollectionReference user = getFirebase().getDb().collection(Constant.COLLECTION_ORDER);
        Query query = user.whereEqualTo("idUser", idUser);

        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots.getDocuments().size()>0){
                if (queryDocumentSnapshots.getDocuments().size() > 0){
                    if (queryDocumentSnapshots.getDocuments().get(0).get("isActive") != null){
                        if ((boolean)queryDocumentSnapshots.getDocuments().get(0).get("isActive")){
                            fabOrder.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                            activeOrder = true;
                        }else{
                            fabOrder.setImageDrawable(getResources().getDrawable(R.drawable.ic_angkot));
                            activeOrder = false;
                        }
                    }
                }
            }
        });

    }
    @Override
    public void onCompleteLoad(int i, int i1) {

    }
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: activeOrder "+activeOrder);
        this.location = location;
        if (activeOrder){
            if (
                    !getPref(Constant.MY_OLD_LATITUDE, "0").equalsIgnoreCase(String.valueOf(location.getLatitude())) &&
                            !getPref(Constant.MY_OLD_LONGITUDE, "0").equalsIgnoreCase(String.valueOf(location.getLongitude()))
            ) {
                setPref(Constant.MY_OLD_LATITUDE, String.valueOf(location.getLatitude()));
                setPref(Constant.MY_OLD_LONGITUDE, String.valueOf(location.getLongitude()));
                setTrack();
            }
        }else{
            clearOrder();
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

    public void listPlatDriver(){
        if (activeOrder){
            HashMap<String, String> subListUser = new HashMap<>();
            HashMap<String, HashMap<String, String>> listUser = new HashMap<>();
            AtomicReference<String> idDriver = new AtomicReference<>("0");
            AtomicReference<String> platNo = new AtomicReference<>("0");
            AtomicInteger count = new AtomicInteger();
            count.set(0);

            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
            View sheetView = getLayoutInflater().inflate(R.layout.layout_masukangkot, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();

            ChipGroup txtPlatNo = sheetView.findViewById(R.id.txtPlatNo);
            txtPlatNo.removeAllViews();
            AppCompatButton btnMasuk = sheetView.findViewById(R.id.btnMasuk);

            CollectionReference crplatNo = getFirebase().getDb().collection(Constant.COLLECTION_ROOM);
            ListenerRegistration listenerPLat = crplatNo.addSnapshotListener((queryDocumentSnapshots, e) -> {
                txtPlatNo.removeAllViews();
                for (DocumentSnapshot documentSnapshot: Objects.requireNonNull(queryDocumentSnapshots).getDocuments()){
                    if (Objects.requireNonNull(documentSnapshot.get("jurusan")).toString().equalsIgnoreCase(checkedJurusan)){
                        subListUser.putAll((Map<String, String>) Objects.requireNonNull(documentSnapshot.get("listUser")));
                        listUser.put(String.valueOf(documentSnapshot.get("platNo")), subListUser);
                        String fbTotalAngkutan = String.valueOf(documentSnapshot.get("count"));
                        count.set(Integer.valueOf(fbTotalAngkutan));
                        String fbPlatNo = String.valueOf(documentSnapshot.get("platNo"));
                        Chip layout_chip = new Chip(this, null, R.attr.chipStyle);
                        layout_chip.setClickable(true);
                        layout_chip.setCheckable(true);
                        CharSequence charSequence =fbPlatNo+":"+fbTotalAngkutan;

                        layout_chip.setTag(documentSnapshot.get("idDriver"));
                        layout_chip.setChipText(charSequence);
                        txtPlatNo.addView(layout_chip);
                    }
                }
            });


            txtPlatNo.setOnCheckedChangeListener((chipGroup, i) -> {
                Chip chip = chipGroup.findViewById(chipGroup.getCheckedChipId());
                if (chip != null){
                    checkAngkot = chip.getText().toString();
                    platNo.set(checkAngkot.split(":")[0]);
                    idDriver.set(chip.getTag().toString());
                }else{
                    checkAngkot = null;
                }
            });
            btnMasuk.setOnClickListener(view -> {
                if (checkAngkot!=null){
                    count.set(count.get()+1);
                    subListUser.put("idUser"+count.get(), String.valueOf(getPref(Constant.ID_USER, "0")));
                    listUser.put(platNo.get(), subListUser);

                    Room room = new Room();
                    room.setJurusan(checkedJurusan);
                    room.setCount(count.get());
                    room.setIdDriver(idDriver.get());
                    room.setListUser(listUser.get(platNo.get()));
                    room.setPlatNo(platNo.get());

                    setPref(Constant.ACTIVE_ORDER_JURUSAN, checkedJurusan);
                    setPref(Constant.ACTIVE_ORDER_COUNT, count.get());
                    setPref(Constant.ACTIVE_ORDER_IDDRIVER, idDriver.get());
                    setPref(Constant.ACTIVE_ORDER_PLATNO, platNo.get());
                    setPref(Constant.STATE_ORDER, true);

                    getFirebase().getDb().collection(Constant.COLLECTION_ROOM).document(platNo.get()).set(room);

                    clearOrder();
                    startActivity(new Intent(MainActivity.this, ActiveOrderActivity.class));
                    finish();
                }else{
                    showSnackBar(mainLayout, new SpannableStringBuilder("Belum Memilih Angkot!"));
                }
            });
            mBottomSheetDialog.setOnDismissListener(dialog -> listenerPLat.remove());
        }
    }
    private void setToActivePage(){
        if (getPref(Constant.STATE_ORDER, false)){
            startActivity(new Intent(MainActivity.this, ActiveOrderActivity.class));
            finish();
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (!activeOrder){
            centerMaps();
        }
    }
}
