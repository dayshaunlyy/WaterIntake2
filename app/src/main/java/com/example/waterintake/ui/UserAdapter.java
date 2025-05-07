package com.example.waterintake.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.waterintake.R;
import com.example.waterintake.data.entities.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;

    public UserAdapter(List<User> users) {
        this.userList = users;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserHeight, tvUserWeight;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserHeight = itemView.findViewById(R.id.tvUserHeight);
            tvUserWeight = itemView.findViewById(R.id.tvUserWeight);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false); // Make sure this file exists
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUserName.setText("Name: " + user.getUsername());
        holder.tvUserHeight.setText("Height: " + user.getHeight() + " cm");
        holder.tvUserWeight.setText("Weight: " + user.getWeight() + " kg");
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
