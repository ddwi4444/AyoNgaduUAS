package com.example.ayongadu.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.ayongadu.R;
import com.example.ayongadu.database.DatabaseBuilder;
import com.example.ayongadu.database.entity.Complain;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private HistoryAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        btnBack = findViewById(R.id.img_back);
        recyclerView = findViewById(R.id.recycler_history);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getComplains();
    }

    private void getComplains() {
        class GetComplains extends AsyncTask<Void, Void, List<Complain>> {

            @Override
            protected List<Complain> doInBackground(Void... voids) {
                return DatabaseBuilder.getInstance(HistoryActivity.this)
                        .getDatabase()
                        .appDao()
                        .getComplainList();
            }

            @Override
            protected void onPostExecute(List<Complain> complains) {
                super.onPostExecute(complains);
                adapter = new HistoryAdapter(complains, HistoryActivity.this);
                recyclerView.setAdapter(adapter);
            }
        }

        GetComplains getComplains = new GetComplains();
        getComplains.execute();
    }
}