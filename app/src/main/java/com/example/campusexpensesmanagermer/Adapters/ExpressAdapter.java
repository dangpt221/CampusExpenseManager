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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        Express express = expressList.get(position);

        // Hiển thị tiêu đề
        holder.tvTitle.setText(express.getTitle());

        // Hiển thị số tiền
        holder.tvAmount.setText(String.format(Locale.getDefault(), "%.0f ₫", express.getAmount()));

        // Hiển thị loại chi
        holder.tvCategory.setText(express.getCategoryName());

        // Hiển thị ngày
        if (express.getDate() != null && !express.getDate().isEmpty()) {
            try {
                // Parse ngày từ database (format: yyyy-MM-dd HH:mm:ss)
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = inputFormat.parse(express.getDate());

                // Format lại thành dd/MM/yyyy
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                holder.tvDate.setText(outputFormat.format(date));
            } catch (Exception e) {
                // Nếu parse lỗi, hiển thị ngày gốc
                holder.tvDate.setText(express.getDate());
            }
        } else {
            holder.tvDate.setText("N/A");
        }
    }

    @Override
    public int getItemCount() {
        return expressList != null ? expressList.size() : 0;
    }

    /**
     * Cập nhật danh sách chi tiêu
     */
    public void updateList(List<Express> newList) {
        this.expressList = newList;
        notifyDataSetChanged();
    }

    /**
     * Thêm chi tiêu vào danh sách
     */
    public void addExpress(Express express) {
        if (expressList != null) {
            expressList.add(0, express);
            notifyItemInserted(0);
        }
    }

    /**
     * Xóa chi tiêu khỏi danh sách
     */
    public void removeExpress(int position) {
        if (expressList != null && position >= 0 && position < expressList.size()) {
            expressList.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * ViewHolder cho Express item
     */
    public static class ExpressViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAmount, tvCategory, tvDate;

        public ExpressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}