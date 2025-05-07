package com.example.waterintake.viewHolder;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.waterintake.data.entities.WaterIntake;

public class WaterIntakeAdapter extends ListAdapter<WaterIntake, WaterIntakeViewHolder> {
    public WaterIntakeAdapter(@NonNull DiffUtil.ItemCallback<WaterIntake> diffCallback){
        super(diffCallback);
    }
    @NonNull
    @Override
    public WaterIntakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return WaterIntakeViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull WaterIntakeViewHolder holder, int position) {
        WaterIntake current = getItem(position);
        holder.bind(current.toString());
    }

    static class WaterIntakeDiff extends DiffUtil.ItemCallback<WaterIntake>{
        @Override
        public boolean areItemsTheSame(@NonNull WaterIntake oldItem, @NonNull WaterIntake newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull WaterIntake oldItem, @NonNull WaterIntake newItem) {
            return newItem.equals(oldItem);
        }
    }

}
