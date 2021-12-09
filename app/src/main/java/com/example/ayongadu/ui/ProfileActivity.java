package com.example.ayongadu.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.ayongadu.R;
import com.example.ayongadu.databinding.ActivityProfileBinding;
import com.example.ayongadu.model.Response;
import com.example.ayongadu.model.User;
import com.example.ayongadu.network.ApiClient;
import com.example.ayongadu.network.ApiInterface;
import com.example.ayongadu.util.AppSharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;

public class ProfileActivity extends AppCompatActivity {
    private AppSharedPreference preference;
    private ActivityProfileBinding binding;
    private User user;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        preference = new AppSharedPreference(this);

        auth = FirebaseAuth.getInstance();
        user = preference.getUser();
        binding.setActivity(this);
        binding.setUser(user);
    }

    public View.OnClickListener onBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    public View.OnClickListener onUpdateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (user.getName().isEmpty() || user.getPhone().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Harap isi form!", Toast.LENGTH_SHORT).show();
            } else {
                save(user.getName(), user.getPhone(), user.getEmail(), user.getPassword());
            }
        }
    };

    private void save(String name, String phone, String email, String password) {
        int id = new AppSharedPreference(this).getUser().getId();
        firebaseUser = auth.getCurrentUser();
        if (!email.equalsIgnoreCase(preference.getUser().getEmail()) && firebaseUser != null) {
            firebaseUser.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Call<Response<String>> call = ApiClient.getClient().create(ApiInterface.class).update(id, name, email, phone, password);
                                call.enqueue(new Callback<Response<String>>() {
                                    @Override
                                    public void onResponse(Call<Response<String>> call, retrofit2.Response<Response<String>> response) {
                                        if (response.isSuccessful() && response.body() != null && response.body().getStatus().equalsIgnoreCase("ok")) {
                                            preference.setUser(id, name, email, phone, password);
                                            Toast.makeText(ProfileActivity.this, "Berhasil mengubah profil!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Gagal mengubah profil: " + response.message(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Response<String>> call, Throwable t) {
                                        Toast.makeText(ProfileActivity.this, "Gagal mengubah profil: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Gagal mengubah profil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Call<Response<String>> call = ApiClient.getClient().create(ApiInterface.class).update(id, name, email, phone, password);
            call.enqueue(new Callback<Response<String>>() {
                @Override
                public void onResponse(Call<Response<String>> call, retrofit2.Response<Response<String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getStatus().equalsIgnoreCase("ok")) {
                        preference.setUser(id, name, email, phone, password);
                        Toast.makeText(ProfileActivity.this, "Berhasil mengubah profil!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Gagal mengubah profil: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<String>> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Gagal mengubah profil: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}