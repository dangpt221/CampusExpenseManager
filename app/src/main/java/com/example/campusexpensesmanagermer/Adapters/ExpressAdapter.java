package com.example.campusexpensesmanagermer.Adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensesmanagermer.Models.Express;
import com.example.campusexpensesmanagermer.R;
import com.example.campusexpensesmanagermer.Repositories.ExpressRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.campusexpensesmanagermer.Utils.CurrencyUtils;

public class ExpressAdapter extends RecyclerView.Adapter<ExpressAdapter.ExpressViewHolder> {

    private Context context;
    private List<Express> expressList;
    private ExpressRepository expressRepository;

    // Listener for data changes
    private OnExpenseChangeListener listener;

    // Categories array
    private final String[] categories = {
            "Food", "Transport", "Shopping", "Entertainment",
            "Health", "Education", "Housing", "Utilities", "Other"
    };

    public ExpressAdapter(Context context, List<Express> expressList) {
        this.context = context;
        this.expressList = expressList;
        this.expressRepository = new ExpressRepository(context);
    }

    // Listener interface
    public interface OnExpenseChangeListener {
        void onExpenseChanged();
    }

    public void setOnExpenseChangeListener(OnExpenseChangeListener listener) {
        this.listener = listener;
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

        // Display data
        holder.tvTitle.setText(express.getTitle());
        holder.tvAmount.setText(CurrencyUtils.formatCurrency(context, express.getAmount()));
        holder.tvCategory.setText(express.getCategoryName());

        // Format date
        if (express.getDate() != null && !express.getDate().isEmpty()) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = inputFormat.parse(express.getDate());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                holder.tvDate.setText(outputFormat.format(date));
            } catch (Exception e) {
                holder.tvDate.setText(express.getDate());
            }
        } else {
            holder.tvDate.setText("N/A");
        }

        // Edit button click
        holder.btnEdit.setOnClickListener(v -> showEditDialog(express, position));

        // Delete button click
        holder.btnDelete.setOnClickListener(v -> showDeleteDialog(express, position));
    }

    @Override
    public int getItemCount() {
        return expressList != null ? expressList.size() : 0;
    }

    // ✨ UPDATED: Show edit dialog with date picker
    private void showEditDialog(Express express, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_expense, null);
        builder.setView(dialogView);

        // Get views
        EditText edtTitle = dialogView.findViewById(R.id.edt_edit_title);
        EditText edtAmount = dialogView.findViewById(R.id.edt_edit_amount);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinner_edit_category);
        LinearLayout layoutDatePicker = dialogView.findViewById(R.id.layout_edit_date_picker);
        TextView tvDate = dialogView.findViewById(R.id.tv_edit_date);

        // Setup spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Fill current data
        edtTitle.setText(express.getTitle());
        // ✅ FIX: Display full amount without casting to int
        edtAmount.setText(String.format(Locale.getDefault(), "%.0f", express.getAmount()));

        // Set current category
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(express.getCategoryName())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        // ✨ NEW: Parse and display current date
        final Calendar selectedCalendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date currentDate = sdf.parse(express.getDate());
            if (currentDate != null) {
                selectedCalendar.setTime(currentDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Display formatted date
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDate.setText(displayFormat.format(selectedCalendar.getTime()));

        // ✨ NEW: Date picker click listener
        layoutDatePicker.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    (view, year, month, dayOfMonth) -> {
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, month);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Update display
                        tvDate.setText(displayFormat.format(selectedCalendar.getTime()));
                    },
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
            );

            // Optional: Set max date to today
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        builder.setTitle("✏️ Edit Expense");
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = edtTitle.getText().toString().trim();
            String newAmountStr = edtAmount.getText().toString().trim();
            String newCategory = spinnerCategory.getSelectedItem().toString();

            // Validate
            if (newTitle.isEmpty()) {
                Toast.makeText(context, "Please enter expense name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newAmountStr.isEmpty()) {
                Toast.makeText(context, "Please enter amount", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double newAmount = Double.parseDouble(newAmountStr);
                if (newAmount <= 0) {
                    Toast.makeText(context, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✨ NEW: Format selected date for database
                SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String newDate = dbFormat.format(selectedCalendar.getTime());

                // Update express object
                express.setTitle(newTitle);
                express.setAmount(newAmount);
                express.setCategoryName(newCategory);
                express.setDate(newDate); // ✨ NEW: Update date

                // Update in database
                boolean success = expressRepository.updateExpress(express);
                if (success) {
                    // Update list
                    expressList.set(position, express);
                    notifyItemChanged(position);

                    Toast.makeText(context, "✅ Updated successfully!", Toast.LENGTH_SHORT).show();

                    // Notify listener
                    if (listener != null) {
                        listener.onExpenseChanged();
                    }
                } else {
                    Toast.makeText(context, "❌ Update failed", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Show delete confirmation dialog
    private void showDeleteDialog(Express express, int position) {
        new AlertDialog.Builder(context)
                .setTitle("🗑️ Confirm Delete")
                .setMessage("Are you sure you want to delete  \"" + express.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete from database
                    boolean success = expressRepository.deleteExpress(express.getId());

                    if (success) {
                        // Remove from list
                        expressList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, expressList.size());

                        Toast.makeText(context, "✅ Deleted successfully!", Toast.LENGTH_SHORT).show();

                        // Notify listener
                        if (listener != null) {
                            listener.onExpenseChanged();
                        }
                    } else {
                        Toast.makeText(context, "❌ Delete failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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
        Button btnEdit, btnDelete;

        public ExpressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}