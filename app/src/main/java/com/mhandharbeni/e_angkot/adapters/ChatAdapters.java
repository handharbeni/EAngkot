package com.mhandharbeni.e_angkot.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.ArrayList;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_chat, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatRoom chatRoom = listChat.get(position);
        String currentUser = CoreApplication.getPref().getString(Constant.ID_USER, "0");
        if (chatRoom.getIdUser().equalsIgnoreCase(currentUser)){
            // right
            holder.chatRight.setVisibility(View.VISIBLE);
            holder.tvNamaRight.setText(chatRoom.getName());
            holder.tvTimeRight.setText(Utils.getDate(chatRoom.getTime()));
            holder.tvMessageRight.setText(chatRoom.getMessage());
        } else {
            // left
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

    public void updateData(List<ChatRoom> listChats){
        this.listChat.clear();
        this.listChat = new ArrayList<>();
        this.listChat.addAll(listChats);
        notifyDataSetChanged();
    }

    @SuppressLint("NonConstantResourceId")
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chatRight) CardView chatRight;
        @BindView(R.id.ivPhotoRight) ImageView ivPhotoRight;
        @BindView(R.id.tvNamaRight) TextView tvNamaRight;
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
