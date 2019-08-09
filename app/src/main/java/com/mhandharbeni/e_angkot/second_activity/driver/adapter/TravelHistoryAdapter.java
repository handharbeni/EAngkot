package com.mhandharbeni.e_angkot.second_activity.driver.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mhandharbeni.e_angkot.R;
import com.mhandharbeni.e_angkot.model.TravelHistory;

import java.util.List;

import static com.mhandharbeni.e_angkot.utils.BaseActivity.TAG;

public class TravelHistoryAdapter extends RecyclerView.Adapter<TravelHistoryAdapter.ViewHolder> {
    Context context;
    List<TravelHistory> listTravelHistory;

    public TravelHistoryAdapter(Context context, List<TravelHistory> listTravelHistory) {
        this.context = context;
        this.listTravelHistory = listTravelHistory;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_itemtravelhistory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtUser.setText(listTravelHistory.get(position).getNamaUser());
        holder.txtEndDestination.setText(listTravelHistory.get(position).getEndDestination());
    }

    @Override
    public int getItemCount() {
        return listTravelHistory.size();
    }

    public void addListHistory(List<TravelHistory> listTravelHistory){
//        this.listTravelHistory.clear();
        this.listTravelHistory.addAll(listTravelHistory);
        notifyDataSetChanged();
    }

    public void addListHistory(TravelHistory travelHistory){
        this.listTravelHistory.add(travelHistory);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUser, txtEndDestination;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtEndDestination = itemView.findViewById(R.id.txtEndDestination);
        }
    }
}
