package com.example.waterintake.viewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.waterintake.R;


public class WaterIntakeViewHolder extends RecyclerView.ViewHolder {
    private final TextView waterIntakeViewItem;
    private WaterIntakeViewHolder(View waterIntakeView){
        super(waterIntakeView);
        waterIntakeViewItem = waterIntakeView.findViewById(R.id.AdminRecyclerView);
    }
    public void bind(String text){
        waterIntakeViewItem.setText(text);
    }
    static WaterIntakeViewHolder create(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_dashboard, parent, false);
        return new WaterIntakeViewHolder(view);
    }
}
