package com.mhandharbeni.e_angkot.second_activity.driver;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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
import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.model.Room;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import illiyin.mhandharbeni.libraryroute.Navigation;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, Navigation.NavigationListener {
    private GoogleMap mMap;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.mainLayout)
    CoordinatorLayout mainLayout;

    HashMap<String, LatLng> listUser = new HashMap<>();
    HashMap<String, ListenerRegistration> listSnapshot = new HashMap<>();

    public ListenerRegistration trackOrder;
    public Navigation navigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startServices();
        setContentView(R.layout.mainactivity_driver);
        ButterKnife.bind(this);
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
        listenerMyLocation();

        navigation = new Navigation();

        navigation.setMap(mMap);
        navigation.setContext(getApplicationContext());
        navigation.setActivity(this);
        navigation.setListener(this);
        navigation.setKey(Constant.API_MAPS);

        centerMaps();
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
        if (location != null)
        {
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
    private void setTrack(){
        Log.d(TAG, "setTrack: ");
        navigation.clearMaps();
        mMap.clear();
        if (getPref(Constant.DRIVER_ISACTIVE, false)){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                listUser.forEach((s, latLng) -> {
                    LatLng startLocation = new LatLng(
                            mMap.getMyLocation().getLatitude(),
                            mMap.getMyLocation().getLongitude()
                    );
                    LatLng endLocation = new LatLng(latLng.latitude, latLng.longitude);
                    navigation.setStartLocation(startLocation);
                    navigation.setEndPosition(endLocation);

                    navigation.setTitleStart("Anda");
                    navigation.setTitleEnd("User");

                    navigation.setMarkerStart(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_angkot));
                    navigation.setMarkerEnd(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_user));

                    navigation.find(false,false);
                });
            }else{
                for (Map.Entry<String, LatLng> entry : listUser.entrySet()){
                    LatLng startLocation = new LatLng(
                            mMap.getMyLocation().getLatitude(),
                            mMap.getMyLocation().getLongitude()
                    );
                    LatLng endLocation = new LatLng(entry.getValue().latitude, entry.getValue().longitude);
                    navigation.setStartLocation(startLocation);
                    navigation.setEndPosition(endLocation);

                    navigation.setTitleStart("Anda");
                    navigation.setTitleEnd("User");

                    navigation.setMarkerStart(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_angkot));
                    navigation.setMarkerEnd(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_user));

                    navigation.find(false,false);
                }
            }
        }else{
            centerMaps();
        }
    }
    private void listenOrder(){
        CollectionReference user = getFirebase().getDb().collection(Constant.COLLECTION_ORDER);
        Query query = user
                .whereEqualTo("jurusan", getPref().getString(Constant.ID_JURUSAN, "0"));
        Log.d(TAG, "listenOrder: "+getPref().getString(Constant.ID_JURUSAN, "0"));

        trackOrder = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots.getDocuments().size() > 0){
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    listenerUserOrder(documentSnapshot.get("idUser").toString(), (boolean) documentSnapshot.get("isActive"));
                }
            }
        });


    }
    private void listenerUserOrder(String idUser, boolean isActive){
        Log.d(TAG, "listenerUserOrder: "+isActive);
        if (isActive){
            CollectionReference user = getFirebase().getDb().collection(Constant.COLLECTION_TRACK_USER);
            Query query = user.whereEqualTo("id", idUser);
            ListenerRegistration listenerRegistration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots.getDocuments().size() > 0){
                    LatLng latLngUser = new LatLng(
                            Double.valueOf(queryDocumentSnapshots.getDocuments().get(0).get("latitude").toString()),
                            Double.valueOf(queryDocumentSnapshots.getDocuments().get(0).get("longitude").toString())
                    );
                    listUser.put(idUser, latLngUser);
                    setTrack();
                }
            });
            listSnapshot.put(idUser, listenerRegistration);
            setTrack();
        }else{
            listUser.remove(idUser);
            listSnapshot.remove(idUser);
            setTrack();
        }
    }
    private void clearMaps(){
        navigation.clearMaps();
        mMap.clear();
    }
    private void startingApps(){
        if (getPref(Constant.DRIVER_ISACTIVE, false)){
            createRoom();
            listenOrder();
        }
    }
    private void listenerPref(){
        getPref().registerOnSharedPreferenceChangeListener((encryptedPreferences, key) -> {
            if (key.equalsIgnoreCase(Constant.DRIVER_ISACTIVE)) {
                Log.d(TAG, "listenerPref: "+key+" "+getPref(Constant.DRIVER_ISACTIVE, false));
                if (getPref(Constant.DRIVER_ISACTIVE, false)) {
                    createRoom();
                    listenOrder();
                } else {
                    deleteRoom();
                    if (trackOrder !=null)
                        trackOrder.remove();

                    for (ListenerRegistration listenerRegistration : listSnapshot.values()){
                        listenerRegistration.remove();
                    }
                    clearMaps();
                    centerMaps();
                }
            }
        });

    }
    private void listenerMyLocation(){
        mMap.setOnMyLocationChangeListener(location -> {
            try {
                if (mMap.getMyLocation() != null){
                    if (
                            !getPref(Constant.MY_OLD_LATITUDE, "0").equalsIgnoreCase(String.valueOf(mMap.getMyLocation().getLatitude())) &&
                                    !getPref(Constant.MY_OLD_LONGITUDE, "0").equalsIgnoreCase(String.valueOf(mMap.getMyLocation().getLongitude()))
                    ){
                        setPref(Constant.MY_OLD_LATITUDE, String.valueOf(mMap.getMyLocation().getLatitude()));
                        setPref(Constant.MY_OLD_LONGITUDE, String.valueOf(mMap.getMyLocation().getLongitude()));
                        setTrack();
                    }
                }
            }catch (Exception e){
                setPref(Constant.MY_OLD_LATITUDE, String.valueOf(mMap.getMyLocation().getLatitude()));
                setPref(Constant.MY_OLD_LONGITUDE, String.valueOf(mMap.getMyLocation().getLongitude()));
            }
        });
    }
    private void createRoom(){
        Room room = new Room(
                String.valueOf(getPref(Constant.ID_USER, "0")),String.valueOf(getPref(Constant.PLAT_NO, "0")),
                "null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null");

        getFirebase()
                .getDb()
                .collection(Constant.COLLECTION_ROOM)
                .document(String.valueOf(getPref(Constant.PLAT_NO, "0")))
                .set(room);
    }
    private void deleteRoom(){
        String idUser = String.valueOf(getPref(Constant.ID_USER, "0"));
        String platNo = String.valueOf(getPref(Constant.PLAT_NO, "0"));
        getFirebase()
                .getDb()
                .collection(Constant.COLLECTION_ROOM)
                .document(String.valueOf(getPref(Constant.PLAT_NO, "0")))
                .delete();
    }
}
