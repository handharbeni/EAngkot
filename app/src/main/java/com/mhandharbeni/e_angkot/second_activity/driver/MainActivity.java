package com.mhandharbeni.e_angkot.second_activity.driver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mhandharbeni.e_angkot.ChatActivity;
import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.model.LocationDriver;
import com.mhandharbeni.e_angkot.model.Room;
import com.mhandharbeni.e_angkot.model.TravelHistory;
import com.mhandharbeni.e_angkot.second_activity.driver.adapter.TravelHistoryAdapter;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import illiyin.mhandharbeni.libraryroute.Navigation;

public class MainActivity extends BaseActivity implements
        OnMapReadyCallback,
        Navigation.NavigationListener,
        LocationListener,
        GpsStatus.Listener, AdapterView.OnItemSelectedListener {
    private GoogleMap mMap;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.mainLayout)
    CoordinatorLayout mainLayout;

    @BindView(R.id.txtPlatNo)
    TextView txtPlatNo;

    @BindView(R.id.txtPenumpang)
    TextView txtPenumpang;

    @BindView(R.id.txtJurusan)
    TextView txtJurusan;

    @BindView(R.id.txtGps)
    TextView txtGps;

    @BindView(R.id.spinnerTujuan)
    SmartMaterialSpinner spinnerTujuan;

    BottomAppBar bottomAppBar;

    HashMap<String, LatLng> listUser = new HashMap<>();
    HashMap<String, ListenerRegistration> listSnapshot = new HashMap<>();

    public ListenerRegistration trackOrder;
    //    public Navigation navigation;
    HashMap<String, Navigation> listNavigation = new HashMap<>();
    List<String> listTujuan = new ArrayList<>();


    LocationManager locationManager;
    GpsStatus mStatus;
    Location location;
    Criteria criteria;
    String provider;
    boolean gpsReady;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startServices();

        setContentView(R.layout.mainactivity_driver);
        ButterKnife.bind(this);

        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.chatDriver:
//                    showToast(getApplicationContext(), "Future Release");
                    Bundle bundle = new Bundle();
//                    Log.d(TAG, "onCreate: "+getPref(Constant.ACTIVE_ORDER_IDDRIVER, "0"));
//                    Log.d(TAG, "onCreate: "+getPref(Constant.ACTIVE_ORDER_PLATNO, "0"));
                    bundle.putString(ChatActivity.KEY_ROOM, getPref(Constant.PLAT_NO, "0"));

                    Intent intents = new Intent(this, ChatActivity.class);
                    intents.putExtras(bundle);
                    startActivity(intents);
                    break;
            }
            return false;
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startingApps();
        listenerPref();
        fillInformation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                Dexter.withActivity(this).withPermissions(Constant.listPermission).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

            }
        }
        mMap.setMyLocationEnabled(true);

        centerMaps();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.addGpsStatusListener(this);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        locationManager.requestLocationUpdates(provider, 10000, 5000, this);
        location = locationManager.getLastKnownLocation(provider);
    }

    @Override
    public void onCompleteLoad(int i, int i1) {
    }

    private void centerMaps() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
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

    private void setTrack() {
        if (listNavigation.size() > 0) {
            for (Map.Entry<String, Navigation> listNav : listNavigation.entrySet()) {
                listNav.getValue().clearMaps();
            }
        }
        mMap.clear();
        if (location != null) {
            if (getPref(Constant.DRIVER_ISACTIVE, false)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (listUser.size() > 0) {
                        listUser.forEach((s, latLng) -> {
                            Navigation navigation = new Navigation();

                            navigation.setMap(mMap);
                            navigation.setContext(getApplicationContext());
                            navigation.setActivity(this);
                            navigation.setListener(this);
                            navigation.setKey(Constant.API_MAPS);


                            LatLng startLocation = new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude()
                            );
                            LatLng endLocation = new LatLng(latLng.latitude, latLng.longitude);
                            navigation.setStartLocation(startLocation);
                            navigation.setEndPosition(endLocation);

                            List<String> snippetStart = new ArrayList<>();
                            List<String> snippetEnd = new ArrayList<>();

                            navigation.setSnippetStart(snippetStart);
                            navigation.setSnippetEnd(snippetEnd);


                            navigation.setTitleStart("Anda");
                            navigation.setTitleEnd("User");

                            navigation.setMarkerStart(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_angkot));
                            navigation.setMarkerEnd(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_user));

                            navigation.find(false, false);
                            listNavigation.put(s, navigation);
                        });
                    }
                } else {
                    if (listUser.size() > 0) {
                        for (Map.Entry<String, LatLng> entry : listUser.entrySet()) {

                            Navigation navigation = new Navigation();

                            navigation.setMap(mMap);
                            navigation.setContext(getApplicationContext());
                            navigation.setActivity(this);
                            navigation.setListener(this);
                            navigation.setKey(Constant.API_MAPS);

                            LatLng startLocation = new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude()
                            );
                            LatLng endLocation = new LatLng(entry.getValue().latitude, entry.getValue().longitude);
                            navigation.setStartLocation(startLocation);
                            navigation.setEndPosition(endLocation);

                            List<String> snippetStart = new ArrayList<>();
                            List<String> snippetEnd = new ArrayList<>();

                            navigation.setSnippetStart(snippetStart);
                            navigation.setSnippetEnd(snippetEnd);

                            navigation.setTitleStart("Anda");
                            navigation.setTitleEnd("User");

                            navigation.setMarkerStart(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_angkot));
                            navigation.setMarkerEnd(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_user));

                            navigation.find(false, false);
                            listNavigation.put(entry.getKey(), navigation);
                        }
                    }
                }
            } else {
                centerMaps();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            locationManager.requestLocationUpdates(provider, 10000, 5000, this);
        }
    }

    private void listenOrder() {
        CollectionReference user = getFirebase().getDb().collection(Constant.COLLECTION_ORDER);
        Query query = user
                .whereEqualTo("jurusan", getPref().getString(Constant.ID_JURUSAN, "0"))
                .whereEqualTo("tujuan", getPref().getString(Constant.ID_TUJUAN, "0"));

        trackOrder = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots.getDocuments().size() > 0) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    if (documentSnapshot.get("isActive") != null){
                        listenerUserOrder(documentSnapshot.get("idUser").toString(), (boolean) documentSnapshot.get("isActive"));
                    }
                }
            }
        });
    }

    private void listenerUserOrder(String idUser, boolean isActive) {
        if (isActive) {
            CollectionReference user = getFirebase().getDb().collection(Constant.COLLECTION_TRACK_USER);
            Query query = user.whereEqualTo("id", idUser);
            ListenerRegistration listenerRegistration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    LatLng latLngUser = new LatLng(
                            Double.parseDouble(queryDocumentSnapshots.getDocuments().get(0).get("latitude").toString()),
                            Double.parseDouble(queryDocumentSnapshots.getDocuments().get(0).get("longitude").toString())
                    );
                    listUser.put(idUser, latLngUser);
                    setTrack();
                }else{
                    listUser.clear();
                    setTrack();
                }
            });
            listSnapshot.put(idUser, listenerRegistration);
        } else {
            listUser.remove(idUser);
            listSnapshot.remove(idUser);
            setTrack();
        }
    }

    private void clearMaps() {
        for (Map.Entry<String, Navigation> entry : listNavigation.entrySet()) {
            entry.getValue().clearMaps();
        }
        mMap.clear();
    }

    private void startingApps() {
        if (getPref(Constant.DRIVER_ISACTIVE, false)) {
            createRoom();
            listenOrder();
        }
    }

    private void listenerPref() {
        getPref().registerOnSharedPreferenceChangeListener((encryptedPreferences, key) -> {
            if (key.equalsIgnoreCase(Constant.DRIVER_ISACTIVE)) {
                if (getPref(Constant.DRIVER_ISACTIVE, false)) {
                    deleteRoom();
                    updateTrackLocationDriver(false);
                    if (trackOrder != null)
                        trackOrder.remove();

                    for (ListenerRegistration listenerRegistration : listSnapshot.values()) {
                        listenerRegistration.remove();
                    }
                    clearMaps();
                    centerMaps();

                    createRoom();
                    listenOrder();
                    updateTrackLocationDriver(true);
                } else {
                    deleteRoom();
                    updateTrackLocationDriver(false);
                    if (trackOrder != null)
                        trackOrder.remove();

                    for (ListenerRegistration listenerRegistration : listSnapshot.values()) {
                        listenerRegistration.remove();
                    }
                    clearMaps();
                    centerMaps();
                }
            }
            if(key.equalsIgnoreCase(Constant.ID_TUJUAN)){
                if (getPref(Constant.DRIVER_ISACTIVE, false)) {
                    if (trackOrder != null)
                        trackOrder.remove();

                    for (ListenerRegistration listenerRegistration : listSnapshot.values()) {
                        listenerRegistration.remove();
                    }
                    clearMaps();
                    centerMaps();

                    deleteRoom();
                    createRoom();
                    listenOrder();
                    updateTrackLocationDriver(true);
                }
            }
        });

    }

    private void createRoom() {
        Room room = new Room(
                String.valueOf(getPref(Constant.ID_USER, "0")),
                String.valueOf(getPref(Constant.ID_JURUSAN, "0")),
                String.valueOf(getPref(Constant.PLAT_NO, "0")),
                0,
                new HashMap<>(),
                String.valueOf(getPref(Constant.ID_TUJUAN, "0"))
        );

        getFirebase()
                .getDb()
                .collection(Constant.COLLECTION_ROOM)
                .document(String.valueOf(getPref(Constant.PLAT_NO, "0")))
                .set(room);
    }

    private void deleteRoom() {
        String idUser = String.valueOf(getPref(Constant.ID_USER, "0"));
        String platNo = String.valueOf(getPref(Constant.PLAT_NO, "0"));
        getFirebase()
                .getDb()
                .collection(Constant.COLLECTION_ROOM)
                .document(String.valueOf(getPref(Constant.PLAT_NO, "0")))
                .delete();
    }

    private void updateTrackLocationDriver(boolean active){
        boolean isLogin = true;
        String id = getPref(Constant.ID_USER, "0");
        String jurusan = getPref(Constant.ID_JURUSAN, "0");
        String tujuan = getPref(Constant.ID_TUJUAN, "0");
        String platNo = getPref(Constant.PLAT_NO, "0");
        String token = getPref(Constant.ID_TOKEN, "0");
        LocationDriver locationDriver = new LocationDriver();
        locationDriver.setId(id);
        locationDriver.setActive(active);
        locationDriver.setLogin(isLogin);
        locationDriver.setJurusan(jurusan);
        locationDriver.setTujuan(tujuan);
        if (active){
            String latitude = "-7.983908";
            String longitude = "112.621391";
            try {
                if (mMap!=null){
                    latitude = String.valueOf(mMap.getMyLocation().getLatitude());
                    longitude = String.valueOf(mMap.getMyLocation().getLongitude());
                }
            } catch (Exception e){}
            locationDriver.setLatitude(latitude);
            locationDriver.setLongitude(longitude);
        }
        locationDriver.setPlatNo(platNo);
        locationDriver.setToken(token);
        getFirebase().getDb().collection(Constant.COLLECTION_TRACK_DRIVER).document(id).set(locationDriver);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            gpsReady = true;
            txtGps.setText("Siap");
        }
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
        try {
            locationManager.removeUpdates(this);
        } catch (Exception ignored) {
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            locationManager.removeUpdates(this);
        } catch (Exception ignored) {
        }
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

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.fab)
    public void onInfoClick() {
        RecyclerView rvTravelHistory;
        TextView txtRatingAngkot, txtRatingDriver;

        TravelHistoryAdapter travelHistoryAdapter;

        List<TravelHistory> listTravelHistory = new ArrayList<>();

        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_informasiangkot, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        txtRatingAngkot = sheetView.findViewById(R.id.txtRatingAngkot);
        txtRatingDriver = sheetView.findViewById(R.id.txtRatingDriver);
        rvTravelHistory = sheetView.findViewById(R.id.rvTravelHistory);

        CollectionReference collectionRatingAngkot = getFirebase().getDb().collection(Constant.COLLECTION_RATING_ANGKOT);
        ListenerRegistration listenerAngkot = collectionRatingAngkot.whereEqualTo("platNo", String.valueOf(getPref(Constant.PLAT_NO, "0"))).addSnapshotListener((queryDocumentSnapshots, e) -> {
            double total = 0;
            int totalData = 0;
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                total += (double) documentSnapshot.get("rate");
                totalData++;
            }
            total = total / totalData;
            DecimalFormat formatter = new DecimalFormat("#,###.0");
            txtRatingAngkot.setText(String.valueOf(formatter.format(total)));
        });

        CollectionReference collectionRatingDriver = getFirebase().getDb().collection(Constant.COLLECTION_RATING_DRIVER);
        ListenerRegistration listenerDriver = collectionRatingDriver.whereEqualTo("idDriver", String.valueOf(getPref(Constant.ID_USER, "0"))).addSnapshotListener((queryDocumentSnapshots, e) -> {
            double total = 0;
            int totalData = 0;
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                total += (double) documentSnapshot.get("rate");
                totalData++;
            }
            total = total / totalData;
            DecimalFormat formatter = new DecimalFormat("#,###.0");
            txtRatingDriver.setText(String.valueOf(formatter.format(total)));
        });

        travelHistoryAdapter = new TravelHistoryAdapter(getApplicationContext(), listTravelHistory);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvTravelHistory.setLayoutManager(linearLayoutManager);
        rvTravelHistory.setAdapter(travelHistoryAdapter);

        showLog(getPref(Constant.ID_USER, "0"));

        CollectionReference collectionHistory = getFirebase().getDb().collection(Constant.COLLECTION_TRAVEL_HISTORY);

        collectionHistory.whereEqualTo("idDriver", String.valueOf(getPref(Constant.ID_USER, "0"))).get().addOnCompleteListener(task -> {
            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                TravelHistory travelHistory = new TravelHistory();
                travelHistory.setIdUser(String.valueOf(documentSnapshot.get("idUser")));
                travelHistory.setDateMillis((long) documentSnapshot.get("dateMillis"));
                travelHistory.setIdDriver(String.valueOf(documentSnapshot.get("idDriver")));
                travelHistory.setPlatNo(String.valueOf(documentSnapshot.get("platNo")));
                travelHistory.setEndDestination(String.valueOf(documentSnapshot.get("endDestination")));
                travelHistory.setNamaUser(String.valueOf(documentSnapshot.get("namaUser")));
                travelHistoryAdapter.addListHistory(travelHistory);
            }
        });

        mBottomSheetDialog.setOnDismissListener(dialog -> {
            listenerAngkot.remove();
            listenerDriver.remove();
//            listenerTravel.remove();
        });
    }

    private void fillInformation() {
        txtPlatNo.setText(getPref(Constant.PLAT_NO, "0"));
        txtJurusan.setText(getPref(Constant.ID_JURUSAN, "0"));
        getFirebase().getDb().collection(Constant.COLLECTION_ROOM)
                .whereEqualTo("platNo", getPref(Constant.PLAT_NO, "0"))
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        List<DocumentSnapshot> listDocumentSnapshot = queryDocumentSnapshots.getDocuments();
                        DocumentSnapshot documentSnapshot = listDocumentSnapshot.get(0);
                        HashMap<String, String> listPenumpang = new HashMap<>((Map<String, String>) Objects.requireNonNull(documentSnapshot.get("listUser")));
                        txtPenumpang.setText(String.valueOf(listPenumpang.size()));
                    }
                });
        getFirebase().listenData(Constant.COLLECTION_TERMINAL, listDocument -> {
            if (listDocument.size() > 0){
                listTujuan.clear();
                for (DocumentSnapshot sTerminal : listDocument){
                    listTujuan.add(sTerminal.getId());
                }
                spinnerTujuan.setItem(listTujuan);
                spinnerTujuan.setSelection(listTujuan.indexOf(getPref(Constant.ID_TUJUAN, "")), true);
            }
        });

        spinnerTujuan.setOnItemSelectedListener(this);
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        mStatus = locationManager.getGpsStatus(mStatus);
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                // Do Something with mStatus info
                txtGps.setText("Mulai");
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                // Do Something with mStatus info
                txtGps.setText("Berhenti");
                break;

            case GpsStatus.GPS_EVENT_FIRST_FIX:
                // Do Something with mStatus info
                txtGps.setText("Tidak Ada Update Lokasi");
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                // Do Something with mStatus info
                txtGps.setText("Siap");
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setPref(Constant.ID_TUJUAN, listTujuan.get(position));
        if (getPref(Constant.DRIVER_ISACTIVE, false)){
            updateTrackLocationDriver(true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
