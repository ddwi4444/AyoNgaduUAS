package com.example.ayongadu.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ayongadu.R;
import com.example.ayongadu.database.DatabaseBuilder;
import com.example.ayongadu.database.entity.Complain;
import com.example.ayongadu.databinding.ItemHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<Complain> complains = new ArrayList<>();
    private Context context;

    public HistoryAdapter(List<Complain> complains, Context context) {
        this.complains = complains;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(complains.get(position));
    }

    @Override
    public int getItemCount() {
        return complains.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemHistoryBinding binding;

        public ViewHolder(@NonNull ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Complain complain) {
            binding.setComplain(complain);
            View.OnClickListener onDeleteListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteComplain(complain);
                }
            };

            binding.setOnDelete(onDeleteListener);
        }

        void deleteComplain(Complain complain) {
            class DeleteComplain extends AsyncTask<Complain, Void, Void> {
                @Override
                protected Void doInBackground(Complain... complains) {
                    DatabaseBuilder.getInstance(context)
                            .getDatabase()
                            .appDao()
                            .deleteComplain(complains[0]);

                    return null;
                }

                @Override
                protected void onPostExecute(Void unused) {
                    super.onPostExecute(unused);
                    context.startActivity(new Intent(context, HomeActivity.class));
                    ((HistoryActivity) context).finish();
                }
            }

            DeleteComplain deleteComplain = new DeleteComplain();
            deleteComplain.execute(complain);
        }
    }
}
