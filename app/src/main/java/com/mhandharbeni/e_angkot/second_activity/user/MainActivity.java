package com.mhandharbeni.e_angkot.second_activity.user;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    @BindView(R.id.fabOrder)
    FloatingActionButton fabOrder;

    @BindView(R.id.mainLayout)
    CoordinatorLayout mainLayout;


    String checkedJurusan = null;
    public static String idDocument;
    public static boolean activeOrder = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_user);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenOrder();
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
                });


                mBottomSheetDialog.dismiss();
            });
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
}
