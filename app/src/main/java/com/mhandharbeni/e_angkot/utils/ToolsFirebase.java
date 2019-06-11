package com.mhandharbeni.e_angkot.utils;

import android.content.Context;
import android.os.StrictMode;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ToolsFirebase {
    static FirebaseFirestore mFirestore;

    private Context context;
    private SendToolsListener sendToolsListener;
    private ReceiveToolsListener receiveToolsListener;
    private GetDataToolsListener getDataToolsListener;
    private DeleteToolsListener deleteToolsListener;
    private OnCompleteToolsListener onCompleteToolsListener;
    private GetSingleDataToolsListener getSingleDataToolsListener;


    public ToolsFirebase(Context context) {
        this.context = context;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initFirestore();
    }

    private void initFirestore() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.setFirestoreSettings(settings);
    }

    public FirebaseFirestore getDb() {
        return mFirestore;
    }

    public void sendDataToFirestore(String collection, String document, Map<String, String> data, SetOptions setOptions, SendToolsListener toolsListener) {
        try {
            this.sendToolsListener = toolsListener;
            mFirestore.collection(collection)
                    .document(document)
                    .set(data, setOptions)
                    .addOnCompleteListener(task -> this.sendToolsListener.onComplete(task))
                    .addOnSuccessListener(aVoid -> this.sendToolsListener.onSuccess(aVoid))
                    .addOnFailureListener(e -> this.sendToolsListener.onError(e));
        } catch (Exception ignored) {
        }
    }

    public ListenerRegistration listenData(String collection, String document, ReceiveToolsListener receiveToolsListener) {
        this.receiveToolsListener = receiveToolsListener;
        return mFirestore.collection(collection).document(document)
                .addSnapshotListener(((documentSnapshot, e) -> this.receiveToolsListener.onSnapshotListener(documentSnapshot, e))
                );
    }

    public ListenerRegistration listenData(String collection, GetDataToolsListener getDataToolsListener) {
        this.getDataToolsListener = getDataToolsListener;
        return mFirestore.collection(collection)
                .addSnapshotListener(((documentSnapshot, e) -> {
                            if (documentSnapshot != null) {
                                this.getDataToolsListener.onSnapshotListener(documentSnapshot.getDocuments(), e);
                            }
                        })
                );
    }

    public Task<QuerySnapshot> listenData(String collection, OnCompleteToolsListener onCompleteToolsListener) {
        this.onCompleteToolsListener = onCompleteToolsListener;
        return mFirestore.collection(collection)
                .get()
                .addOnCompleteListener(task -> this.onCompleteToolsListener.onSnapshotListener(Objects.requireNonNull(task.getResult()).getDocuments()));
    }

    public Task<DocumentSnapshot> listenData(String collection, String document, GetSingleDataToolsListener getSingleDataToolsListener) {
        this.getSingleDataToolsListener = getSingleDataToolsListener;
        return mFirestore.collection(collection).document(document)
                .get()
                .addOnCompleteListener(task -> this.getSingleDataToolsListener.onSnapshotListener(task.getResult()));
    }

    public void deleteData(String collection, String document, DeleteToolsListener deleteToolsListener) {
        try {
            this.deleteToolsListener = deleteToolsListener;
            mFirestore.collection(collection).document(document).delete()
                    .addOnCompleteListener(task -> this.deleteToolsListener.onComplete(task))
                    .addOnSuccessListener(aVoid -> this.deleteToolsListener.onSuccess(aVoid))
                    .addOnFailureListener(e -> this.deleteToolsListener.onError(e));
        } catch (Exception ignored) {
        }
    }


    public interface DeleteToolsListener {
        void onComplete(Task<Void> task);

        void onSuccess(Void task);

        void onError(Exception e);
    }

    public interface SendToolsListener {
        void onComplete(Task<Void> task);

        void onSuccess(Void task);

        void onError(Exception e);
    }

    public interface ReceiveToolsListener {
        void onSnapshotListener(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e);
    }

    public interface GetDataToolsListener {
        void onSnapshotListener(List<DocumentSnapshot> listDocument, FirebaseFirestoreException e);
    }

    public interface GetSingleDataToolsListener {
        void onSnapshotListener(DocumentSnapshot documentSnapshot);
    }

    public interface OnCompleteToolsListener {
        void onSnapshotListener(List<DocumentSnapshot> listDocument);
    }
}
