package com.example.campusexpensesmanagermer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensesmanagermer.Models.BudgetItem;
import com.example.campusexpensesmanagermer.R;

import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private Context context;
    private List<BudgetItem> budgetItems;
    private OnBudgetItemClickListener listener;

    public interface OnBudgetItemClickListener {
        void onEdit(BudgetItem item);
        // removed onDelete to disable delete functionality from UI
    }

    public BudgetAdapter(Context context, List<BudgetItem> budgetItems) {
        this.context = context;
        this.budgetItems = budgetItems;
    }

    public void setListener(OnBudgetItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        BudgetItem item = budgetItems.get(position);

        // Hiển thị tên danh mục
        holder.tvCategory.setText(item.getCategoryName());

        // Hiển thị số tiền đã cấp
        holder.tvAllocated.setText(String.format(Locale.getDefault(), "%.0f ₫", item.getAllocatedAmount()));

        // Hiển thị số tiền đã chi
        holder.tvSpent.setText(String.format(Locale.getDefault(), "%.0f ₫", item.getSpentAmount()));

        // Hiển thị số tiền còn lại
        double remaining = item.getRemaining();
        holder.tvRemaining.setText(String.format(Locale.getDefault(), "%.0f ₫", remaining));

        // Đổi màu nếu vượt quá
        if (item.isExceeded()) {
            holder.tvRemaining.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            holder.tvSpent.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        } else {
            holder.tvRemaining.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            holder.tvSpent.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark));
        }

        // Hiển thị progress bar
        int progress = item.getProgressPercentage();
        holder.progressBar.setProgress(progress);
        holder.tvProgress.setText(progress + "%");

        // Set màu progress bar
        if (progress <= 50) {
            holder.progressBar.setProgressDrawable(ContextCompat.getDrawable(context, android.R.drawable.progress_horizontal));
        } else if (progress <= 80) {
            holder.progressBar.setProgressDrawable(ContextCompat.getDrawable(context, android.R.drawable.progress_horizontal));
        } else {
            holder.progressBar.setProgressDrawable(ContextCompat.getDrawable(context, android.R.drawable.progress_horizontal));
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(item);
            }
        });

        // Removed delete click listener - delete UI disabled
    }

    @Override
    public int getItemCount() {
        return budgetItems != null ? budgetItems.size() : 0;
    }

    /**
     * Cập nhật danh sách
     */
    public void updateList(List<BudgetItem> newList) {
        this.budgetItems = newList;
        notifyDataSetChanged();
    }

    /**
     * Xóa item khỏi danh sách
     */
    public void removeItem(int position) {
        if (budgetItems != null && position >= 0 && position < budgetItems.size()) {
            budgetItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * ViewHolder
     */
    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAllocated, tvSpent, tvRemaining, tvProgress; // removed tvDelete
        ProgressBar progressBar;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category_name);
            tvAllocated = itemView.findViewById(R.id.tv_allocated_amount);
            tvSpent = itemView.findViewById(R.id.tv_spent_amount);
            tvRemaining = itemView.findViewById(R.id.tv_remaining_amount);
            tvProgress = itemView.findViewById(R.id.tv_progress_percent);
            progressBar = itemView.findViewById(R.id.progress_bar);
            // tvDelete removed
        }
    }
}