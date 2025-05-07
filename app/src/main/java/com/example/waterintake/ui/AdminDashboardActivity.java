package com.example.waterintake.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterintake.R;
import com.example.waterintake.data.entities.User;
import com.example.waterintake.databinding.ActivityAdminDashboardBinding;
import com.example.waterintake.viewmodels.UserViewModel;

public class AdminDashboardActivity extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;
    private UserViewModel userViewModel;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup RecyclerView
        userAdapter = new UserAdapter();
        binding.FirstUser.setLayoutMode(new LinearLayoutManager(this));
        binding.FirstUser.setAdapter(userAdapter);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, users -> {
            userAdapter.submitList(users);
        });

        // Swipe-to-delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(...) { return false; }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                User userToDelete = userAdapter.getUserAt(viewHolder.getAdapterPosition());
                showDeleteConfirmationDialog(userToDelete);
            }
        }).attachToRecyclerView(binding.FirstUser);
    }

    private void showDeleteConfirmationDialog(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Delete " + user.getUsername() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    userViewModel.deleteUser(user);
                    Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();