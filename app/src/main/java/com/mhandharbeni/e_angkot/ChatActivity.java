package com.mhandharbeni.e_angkot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.mhandharbeni.e_angkot.adapters.ChatAdapters;
import com.mhandharbeni.e_angkot.model.ChatRoom;
import com.mhandharbeni.e_angkot.utils.BaseActivity;
import com.mhandharbeni.e_angkot.utils.Constant;

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
    @BindView(R.id.messageBox) TextView messageBox;
    @BindView(R.id.btnSend) Button btnSend;


    List<ChatRoom> listChat = new ArrayList<>();
    ChatAdapters chatAdapters;

    String idRoom = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat);
        ButterKnife.bind(this);

        getBundleData();
        initAdapter();
    }

    void getBundleData(){
        Bundle bundle = getIntent().getExtras();
        idRoom = bundle.getString(KEY_ROOM);
    }

    void listenData(){
        getFirebase().listenData("e_angkot_chat/N111AB/room", (listDocument, e) -> {
            listChat.clear();
            for (DocumentSnapshot documentSnapshot : listDocument) {
                ChatRoom cr = new ChatRoom();
                cr.setIdUser(documentSnapshot.get("idUser").toString());
                cr.setImageProfile(documentSnapshot.get("imageProfile").toString());
                cr.setMessage(documentSnapshot.get("message").toString());
                cr.setName(documentSnapshot.get("name").toString());
                cr.setTime(documentSnapshot.get("time").toString());
                cr.setTypeMessage(documentSnapshot.get("typeMessage").toString());
                listChat.add(cr);
            }
            Collections.sort(listChat, (o1, o2) -> Long.valueOf(o1.time).compareTo(Long.valueOf(o2.time)));
            chatAdapters.updateData(listChat);
            rvChat.scrollToPosition(chatAdapters.getItemCount());
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
        getFirebase().getDb().collection("e_angkot_chat/N111AB/room").document().set(chatRoom);
    }

    void initAdapter(){
        chatAdapters = new ChatAdapters(getApplicationContext(), listChat, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvChat.setLayoutManager(linearLayoutManager);
        rvChat.setAdapter(chatAdapters);
        listenData();
    }

    @Override
    public void onChatClick(ChatRoom chatRoom) {

    }
}
