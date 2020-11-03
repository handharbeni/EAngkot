package com.mhandharbeni.e_angkot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.mhandharbeni.e_angkot.adapters.ChatAdapters;
import com.mhandharbeni.e_angkot.model.ChatRoom;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;
import com.vanniktech.emoji.EmojiButton;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class ChatActivity extends BaseActivity implements ChatAdapters.ChatInterface {
    public static String KEY_ROOM = "KEYROOM";
    @BindView(R.id.rvChat) RecyclerView rvChat;
    @BindView(R.id.messageBox) EmojiEditText messageBox;
    @BindView(R.id.btnSend) Button btnSend;
    @BindView(R.id.showEmoticon) EmojiButton showEmoticon;
    @BindView(R.id.rootView) ConstraintLayout rootView;


    List<ChatRoom> listChat = new ArrayList<>();
    List<ChatRoom> tempListChat = new ArrayList<>();
    ChatAdapters chatAdapters;
    ListenerRegistration listener;

    String idRoom = null;

    EmojiPopup emojiPopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat);
        ButterKnife.bind(this);
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(messageBox);
        getBundleData();
        initAdapter();
        listenData();

        hideSwitchActionBar();
    }

    void getBundleData(){
        Bundle bundle = getIntent().getExtras();
        idRoom = bundle.getString(KEY_ROOM);
    }

    public void hideSwitchActionBar(){ idSwitch.setVisibility(View.GONE); }

    void listenData(){
        getFirebase().listenData("e_angkot_chat/"+idRoom+"/room", (listDocument, e) -> {
            if (listDocument != null){
                if (listDocument.size() > 0){
                    Log.d(TAG, "listenData: "+listDocument.size());
                    tempListChat.clear();
                    for (DocumentSnapshot documentSnapshot : listDocument) {
                        Log.d(TAG, "onComplete: "+documentSnapshot.get("idUser").toString());
                        ChatRoom cr = new ChatRoom();
                        cr.setIdUser(documentSnapshot.get("idUser").toString());
                        cr.setImageProfile(documentSnapshot.get("imageProfile").toString());
                        cr.setMessage(documentSnapshot.get("message").toString());
                        cr.setName(documentSnapshot.get("name").toString());
                        cr.setTime(documentSnapshot.get("time").toString());
                        cr.setTypeMessage(documentSnapshot.get("typeMessage").toString());
                        tempListChat.add(cr);
                    }
                    chatAdapters.updateData(tempListChat);
                    if (chatAdapters.getItemCount() > 0){
                        rvChat.smoothScrollToPosition(tempListChat.size()-1);
                    }
                }
            }
        });
    }

    @OnClick(R.id.btnSend)
    public void sendMessage(){
        String idUser = getPref(Constant.ID_USER, "0");
        String namaUser = getPref(Constant.NAMA_USER, "");
        String message = messageBox.getText().toString();
        String time = String.valueOf(System.currentTimeMillis());
        String imageProfile = getPref(Constant.IMAGE_USER, "");
        String typeMessage = "text";
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setIdUser(idUser);
        chatRoom.setName(namaUser);
        chatRoom.setMessage(message);
        chatRoom.setTime(time);
        chatRoom.setImageProfile(imageProfile);
        chatRoom.setTypeMessage(typeMessage);
        getFirebase().getDb().collection("e_angkot_chat/"+idRoom+"/room").document().set(chatRoom);

        messageBox.setText("");
    }

    void initAdapter(){
        chatAdapters = new ChatAdapters(getApplicationContext(), listChat, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        linearLayoutManager.setStackFromEnd(true);

        rvChat.setLayoutManager(linearLayoutManager);

        rvChat.setAdapter(chatAdapters);

        if (chatAdapters.getItemCount() > 0){
            rvChat.smoothScrollToPosition(listChat.size()-1);
        }
    }

    void getAllData(){
        CoreApplication.getFirebase().getDb().collection("e_angkot_chat/"+idRoom+"/room").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentChange documentChange : task.getResult().getDocumentChanges()) {
                    Log.d(TAG, "onComplete: "+documentChange.getDocument().getData().get("idUser").toString());
                    ChatRoom cr = new ChatRoom();
                    cr.setIdUser(documentChange.getDocument().getData().get("idUser").toString());
                    cr.setImageProfile(documentChange.getDocument().getData().get("imageProfile").toString());
                    cr.setMessage(documentChange.getDocument().getData().get("message").toString());
                    cr.setName(documentChange.getDocument().getData().get("name").toString());
                    cr.setTime(documentChange.getDocument().getData().get("time").toString());
                    cr.setTypeMessage(documentChange.getDocument().getData().get("typeMessage").toString());
                    listChat.add(cr);
                }

                initAdapter();
            }
        });
    }

    @Override
    public void onChatClick(ChatRoom chatRoom) {

    }

    @OnClick(R.id.showEmoticon)
    public void openEmojiDialog(){
        emojiPopup.toggle();
    }

    @Override
    protected void onDestroy() {
        try {
            listener.remove();
        } catch (Exception e){}
        super.onDestroy();
    }
}
