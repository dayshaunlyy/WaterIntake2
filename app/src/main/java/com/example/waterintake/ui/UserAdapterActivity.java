package com.example.waterintake.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.waterintake.data.entities.User;
import com.example.waterintake.databinding.ItemUserBinding;

public class UserAdapter extends ListAdapter<User, UserAdapter.UserViewHolder> {

    public interface OnUserClickListener {
        void onUserClicked(User user);
    }

    private final OnUserClickListener listener;

    public UserAdapter(OnUserClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<User> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<User>() {
                @Override
                public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                    return oldItem.getUsername().equals(newItem.getUsername()) &&
                            oldItem.getHeight() == newItem.getHeight() &&
                            oldItem.getWeight() == newItem.getWeight();
                }
            };

    @Override
    public <ItemUserBinding> UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = getItem(position);
        holder.bind(user);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        public final ItemUserBinding binding;

        UserViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public <ItemUserBinding> UserViewHolder(ItemUserBinding binding) {
            super((View) binding);
        }

        void bind(User user) {
            binding.tvUsername.setText(user.getUsername());
            binding.tvEmail.setText(user.getEmail());
            binding.tvHeight.setText(String.format("Height: %.1f cm", user.getHeight()));
            binding.tvWeight.setText(String.format("Weight: %.1f kg", user.getWeight()));

            // Add click listener if needed
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClicked(user);
                }
            });
        }
    }
}
