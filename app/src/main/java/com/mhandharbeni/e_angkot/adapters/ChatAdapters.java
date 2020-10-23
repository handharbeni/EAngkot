package com.mhandharbeni.e_angkot.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mhandharbeni.e_angkot.CoreApplication;
import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.model.ChatRoom;
import com.mhandharbeni.e_angkot.utils.Constant;
import com.mhandharbeni.e_angkot.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapters extends RecyclerView.Adapter<ChatAdapters.ViewHolder>{
    Context context;
    List<ChatRoom> listChat;
    ChatInterface chatInterface;

    public ChatAdapters(Context context, List<ChatRoom> listChat, ChatInterface chatInterface) {
        this.context = context;
        this.listChat = listChat;
        this.chatInterface = chatInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatRoom chatRoom = listChat.get(position);
        Log.d("ChatAdapters", "onBindViewHolder: "+position+" "+chatRoom.toString());
        String currentUser = CoreApplication.getPref().getString(Constant.ID_USER, "0");
        if (chatRoom.getIdUser().equalsIgnoreCase(currentUser)){
            // right
            holder.chatRight.setVisibility(View.VISIBLE);
            holder.tvTimeRight.setText(Utils.getDate(chatRoom.getTime()));
            holder.tvMessageRight.setText(chatRoom.getMessage());
        } else {
            // left
            Picasso.get().load(chatRoom.getImageProfile()).into(holder.ivPhotoLeft);
            holder.chatLeft.setVisibility(View.VISIBLE);
            holder.tvNamaLeft.setText(chatRoom.getName());
            holder.tvTimeLeft.setText(Utils.getDate(chatRoom.getTime()));
            holder.tvMessageLeft.setText(chatRoom.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    public void updateData(ChatRoom chatRoom){
//        if (!listChat.contains(chatRoom)){
        this.listChat.add(chatRoom);
        Collections.sort(this.listChat, (o1, o2) -> Long.valueOf(o1.time).compareTo(Long.valueOf(o2.time)));
        notifyDataSetChanged();
//        }
    }

    public void updateData(List<ChatRoom> newChat){
        this.listChat.clear();
        this.listChat.addAll(newChat);
        notifyDataSetChanged();
    }

    @SuppressLint("NonConstantResourceId")
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chatRight) CardView chatRight;
        @BindView(R.id.tvTimeRight) TextView tvTimeRight;
        @BindView(R.id.tvMessageRight) TextView tvMessageRight;


        @BindView(R.id.chatLeft) CardView chatLeft;
        @BindView(R.id.ivPhotoLeft) ImageView ivPhotoLeft;
        @BindView(R.id.tvNamaLeft) TextView tvNamaLeft;
        @BindView(R.id.tvTimeLeft) TextView tvTimeLeft;
        @BindView(R.id.tvMessageLeft) TextView tvMessageLeft;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ChatInterface{
        void onChatClick(ChatRoom chatRoom);
    }
}
