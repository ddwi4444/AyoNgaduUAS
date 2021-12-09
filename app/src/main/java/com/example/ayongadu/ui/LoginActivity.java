package com.example.ayongadu.ui;

import static com.example.ayongadu.App.CHANNEL_1_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;

import com.example.ayongadu.R;
import com.example.ayongadu.databinding.ActivityLoginBinding;
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

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {
    private AppSharedPreference preference;
    private ActivityLoginBinding binding;
    private User user;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        preference = new AppSharedPreference(this);

        auth = FirebaseAuth.getInstance();
        user = new User();
        binding.setActivity(this);
        binding.setUser(user);
    }

    public View.OnClickListener onLoginClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Harap isi form!", Toast.LENGTH_SHORT).show();
            } else {
                login(user.getEmail(), user.getPassword());
            }
        }
    };

    public View.OnClickListener onRegisterClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    };

    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                Call<Response<User>> call = ApiClient.getClient().create(ApiInterface.class).login(email, password);
                                call.enqueue(new Callback<Response<User>>() {
                                    @Override
                                    public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                                        if (response.isSuccessful() && response.body() != null && response.body().getStatus().equalsIgnoreCase("ok")) {
                                            pushNotification();
                                            User loggedUser = response.body().getData();

                                            preference.setUser(loggedUser.getId(), loggedUser.getName(), loggedUser.getEmail(), loggedUser.getPhone(), "");
                                            preference.setIsLogin(true);
                                            Toast.makeText(LoginActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Login gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Response<User>> call, Throwable t) {
                                        Toast.makeText(LoginActivity.this, "Login gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(LoginActivity.this, "Email belum diverifikasi!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Login gagal!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (preference.isLogin()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preference.isLogin()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    private void pushNotification() {
        Intent intent = new Intent(this, HomeActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_priority)
                .setContentTitle("Selamat!")
                .setContentText("Anda berhasil login!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(notificationId, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager) {
        String channelName = "Suara Rakyat";
        String channelDescription = "Notifikasi suara rakyat";

        NotificationChannel adminChannel = new NotificationChannel(CHANNEL_1_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(channelDescription);
        adminChannel.enableLights(true);
        adminChannel.enableVibration(true);
        notificationManager.createNotificationChannel(adminChannel);
    }
}