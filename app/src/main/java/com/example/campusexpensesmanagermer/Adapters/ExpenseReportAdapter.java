package com.example.campusexpensesmanagermer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensesmanagermer.Models.ExpenseReport;
import com.example.campusexpensesmanagermer.R;

import java.util.ArrayList;
import java.util.List;

public class ExpenseReportAdapter extends RecyclerView.Adapter<ExpenseReportAdapter.ReportViewHolder> {

    private Context context;
    private List<ExpenseReport> reportList;

    public ExpenseReportAdapter(Context context, List<ExpenseReport> reportList) {
        this.context = context;
        this.reportList = reportList != null ? reportList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ExpenseReport report = reportList.get(position);

        holder.tvCategoryName.setText(report.getCategoryName());
        holder.tvAmount.setText(String.format("%.0f ₫", report.getTotalAmount()));
        holder.tvTransactionCount.setText(report.getTransactionCount() + " giao dịch");
        holder.tvPercentage.setText(String.format("%.1f%%", report.getPercentage()));
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public void updateData(List<ExpenseReport> newReportList) {
        this.reportList = newReportList != null ? newReportList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvAmount, tvTransactionCount, tvPercentage;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvTransactionCount = itemView.findViewById(R.id.tvTransactionCount);
            tvPercentage = itemView.findViewById(R.id.tvPercentage);
        }
    }
}