package com.example.ayongadu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.ayongadu.R;
import com.example.ayongadu.databinding.ActivityRegisterBinding;
import com.example.ayongadu.model.Response;
import com.example.ayongadu.model.User;
import com.example.ayongadu.network.ApiClient;
import com.example.ayongadu.network.ApiInterface;
import com.example.ayongadu.util.AppSharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    private AppSharedPreference preference;
    private ActivityRegisterBinding binding;
    private User user;
    private FirebaseAuth auth;

    public View.OnClickListener onLoginClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }
    };

    public View.OnClickListener onRegisterClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (user.getName().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Harap isi form!", Toast.LENGTH_SHORT).show();
            } else {
                register(user.getName(), user.getEmail(), user.getPassword());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        preference = new AppSharedPreference(this);
        user = new User();
        binding.setActivity(this);
        binding.setUser(user);
    }

    private void register(String name, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Call<Response<String>> call = ApiClient.getClient().create(ApiInterface.class).register(name, email, password);
                                                call.enqueue(new Callback<Response<String>>() {
                                                    @Override
                                                    public void onResponse(Call<Response<String>> call, retrofit2.Response<Response<String>> response) {
                                                        if (response.isSuccessful() && response.body() != null && response.body().getStatus().equalsIgnoreCase("ok")) {
                                                            Toast.makeText(RegisterActivity.this, "Berhasil mendaftar!", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                            finish();
                                                        } else {
                                                            Toast.makeText(RegisterActivity.this, "Registrasi gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Response<String>> call, Throwable t) {
                                                        Toast.makeText(RegisterActivity.this, "Registrasi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Registrasi gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}