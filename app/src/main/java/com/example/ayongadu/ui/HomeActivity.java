package com.example.ayongadu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.ayongadu.R;
import com.example.ayongadu.databinding.ActivityHomeBinding;
import com.example.ayongadu.util.AppSharedPreference;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        binding.setName(new AppSharedPreference(this).getUser().getName());
        binding.setActivity(this);
    }

    public View.OnClickListener onCallAmbulanceClicked = v -> startActivity(new Intent(HomeActivity.this, CallAmbulanceActivity.class));
    public View.OnClickListener onComplainClicked = v -> startActivity(new Intent(HomeActivity.this, ComplainActivity.class));
    public View.OnClickListener onContactListClicked = v -> startActivity(new Intent(HomeActivity.this, ContactActivity.class));
    public View.OnClickListener onEditProfileClicked = v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
    public View.OnClickListener onHistoryClicked = v -> startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
    public View.OnClickListener onAboutClicked = v -> startActivity(new Intent(HomeActivity.this, AboutActivity.class));

    private void setName() {
        binding.setName(new AppSharedPreference(this).getUser().getName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        setName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setName();
    }
}