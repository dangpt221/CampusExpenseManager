package com.example.campusexpensesmanagermer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensesmanagermer.Models.Express;
import com.example.campusexpensesmanagermer.R;

import java.util.List;

public class ExpressAdapter extends RecyclerView.Adapter<ExpressAdapter.ExpressViewHolder> {

    private Context context;
    private List<Express> expressList;

    public ExpressAdapter(Context context, List<Express> expressList) {
        this.context = context;
        this.expressList = expressList;
    }

    @NonNull
    @Override
    public ExpressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_express, parent, false);
        return new ExpressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpressViewHolder holder, int position) {
        Express e = expressList.get(position);
        holder.tvTitle.setText(e.getTitle());
        holder.tvAmount.setText(String.format("%.0f ₫", e.getAmount()));
        holder.tvCategory.setText(e.getCategoryName());
    }

    @Override
    public int getItemCount() {
        return expressList.size();
    }

    public static class ExpressViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAmount, tvCategory;

        public ExpressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
