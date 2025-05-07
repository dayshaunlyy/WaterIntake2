package com.example.waterintake.viewHolder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.waterintake.R;


public class WaterIntakeViewHolder extends RecyclerView.ViewHolder {
    private WaterIntakeViewHolder(View waterIntakeView){
        super(waterIntakeView);
        waterIntakeView = waterIntakeView.findViewById(R.id.AdminRecyclerView);
    }
    public void bind(String text){

    }
}
