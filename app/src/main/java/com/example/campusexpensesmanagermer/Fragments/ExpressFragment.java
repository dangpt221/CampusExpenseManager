package com.example.campusexpensesmanagermer.Fragments;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensesmanagermer.Adapters.ExpressAdapter;
import com.example.campusexpensesmanagermer.Data.SQLiteDbHelper;
import com.example.campusexpensesmanagermer.Models.Express;
import com.example.campusexpensesmanagermer.R;
import com.example.campusexpensesmanagermer.Repositories.ExpressRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpressFragment extends Fragment {

    private EditText edtExpenseName, edtAmount;
    private Button btnAddExpense, btnClear;
    private LinearLayout categoryContainer, layoutDatePicker;
    private TextView tvSelectedCategory, tvDate, tvEmptyState;
    private ImageView imgCategory;
    private RecyclerView rvExpenses;

    private ExpressRepository expressRepository;
    private SharedPreferences prefs;
    private int userId;

    private ExpressAdapter adapter;
    private List<Express> expressList;

    private String selectedCategory = "Food";
    private Calendar selectedCalendar = Calendar.getInstance(); // ✨ NEW: Store selected date

    private static final String PREFS_NAME = "CampusExpensesPrefs";
    private static final String TAG = "ExpressFragment";

    private final String[] categories = {
            "Food", "Transport", "Shopping", "Entertainment",
            "Health", "Education", "Housing", "Utilities", "Other"
    };

    public ExpressFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            expressRepository = new ExpressRepository(getContext());
            prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
            userId = prefs.getInt("ID_USER", 0);
            Log.d(TAG, "✓ onCreate - userId: " + userId);
        } catch (Exception e) {
            Log.e(TAG, "✗ Error in onCreate: " + e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_express, container, false);

        try {
            // Input fields
            edtExpenseName = view.findViewById(R.id.edtExpenseName);
            edtAmount = view.findViewById(R.id.edtAmount);
            btnAddExpense = view.findViewById(R.id.btnAddExpense);
            btnClear = view.findViewById(R.id.btnClear);
            categoryContainer = view.findViewById(R.id.categoryContainer);
            tvSelectedCategory = view.findViewById(R.id.tvSelectedCategory);
            tvDate = view.findViewById(R.id.tvDate);
            imgCategory = view.findViewById(R.id.imgCategory);
            layoutDatePicker = view.findViewById(R.id.layoutDatePicker); // ✨ NEW

            // RecyclerView for list
            rvExpenses = view.findViewById(R.id.rvExpenses);
            tvEmptyState = view.findViewById(R.id.tvEmptyState);

            updateDateDisplay();
            setupCategoryIcons();
            setupRecyclerView();
            loadExpenses();
            setupDatePicker(); // ✨ NEW

            btnAddExpense.setOnClickListener(v -> saveExpense());
            btnClear.setOnClickListener(v -> clearForm());

            Log.d(TAG, "✓ onCreateView - Setup complete");
        } catch (Exception e) {
            Log.e(TAG, "✗ Error in onCreateView: " + e.getMessage());
            e.printStackTrace();
        }

        return view;
    }

    // ✨ NEW: Setup date picker functionality
    private void setupDatePicker() {
        layoutDatePicker.setOnClickListener(v -> showDatePickerDialog());
    }

    // ✨ NEW: Show date picker dialog
    private void showDatePickerDialog() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Update selected calendar
                    selectedCalendar.set(Calendar.YEAR, selectedYear);
                    selectedCalendar.set(Calendar.MONTH, selectedMonth);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                    // Update display
                    updateDateDisplay();

                    Toast.makeText(getContext(),
                            "📅 Date selected: " + formatDate(selectedCalendar.getTime()),
                            Toast.LENGTH_SHORT).show();
                },
                year, month, day
        );

        // Optional: Set max date to today (can't select future dates)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    // ✨ MODIFIED: Update date display to show selected date
    private void updateDateDisplay() {
        String currentDate = formatDate(selectedCalendar.getTime());
        tvDate.setText(currentDate);
    }

    // ✨ NEW: Format date helper
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    // ✨ NEW: Get formatted date for database (yyyy-MM-dd HH:mm:ss)
    private String getFormattedDateForDatabase() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(selectedCalendar.getTime());
    }

    private void setupRecyclerView() {
        expressList = new ArrayList<>();
        adapter = new ExpressAdapter(getContext(), expressList);

        adapter.setOnExpenseChangeListener(new ExpressAdapter.OnExpenseChangeListener() {
            @Override
            public void onExpenseChanged() {
                loadExpenses();
            }
        });

        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExpenses.setAdapter(adapter);

        Log.d(TAG, "✓ RecyclerView setup with edit/delete functionality");
    }

    private void loadExpenses() {
        if (userId == 0) {
            Log.e(TAG, "Cannot load expenses - userId is 0");
            return;
        }

        try {
            List<Express> list = expressRepository.getAllExpressByUser(userId);
            expressList.clear();
            expressList.addAll(list);
            adapter.notifyDataSetChanged();

            if (expressList.isEmpty()) {
                rvExpenses.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
                Log.d(TAG, "No expenses found");
            } else {
                rvExpenses.setVisibility(View.VISIBLE);
                tvEmptyState.setVisibility(View.GONE);
                Log.d(TAG, "✓ Loaded " + expressList.size() + " expenses");
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error loading expenses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupCategoryIcons() {
        categoryContainer.removeAllViews();
        for (String category : categories) {
            categoryContainer.addView(createCategoryButton(category));
        }
    }

    private View createCategoryButton(String category) {
        LinearLayout button = new LinearLayout(getContext());
        button.setOrientation(LinearLayout.VERTICAL);
        button.setGravity(Gravity.CENTER);
        button.setPadding(8, 8, 8, 8);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(4, 4, 4, 4);
        button.setLayoutParams(params);

        ImageView icon = new ImageView(getContext());
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(48, 48);
        iconParams.gravity = Gravity.CENTER_HORIZONTAL;
        icon.setLayoutParams(iconParams);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        setCategoryIcon(icon, category);

        TextView categoryName = new TextView(getContext());
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        nameParams.topMargin = 4;
        nameParams.gravity = Gravity.CENTER_HORIZONTAL;
        categoryName.setLayoutParams(nameParams);
        categoryName.setText(category);
        categoryName.setTextSize(10);
        categoryName.setGravity(Gravity.CENTER);

        button.addView(icon);
        button.addView(categoryName);

        int bgColor = category.equals(selectedCategory) ?
                ContextCompat.getColor(requireContext(), android.R.color.darker_gray) :
                ContextCompat.getColor(requireContext(), android.R.color.transparent);
        button.setBackgroundColor(bgColor);

        button.setOnClickListener(v -> {
            selectedCategory = category;
            tvSelectedCategory.setText("Category: " + category);
            updateCategoryIconDisplay();
            setupCategoryIcons();
        });

        return button;
    }

    private void setCategoryIcon(ImageView imageView, String category) {
        int iconResId, colorResId;
        switch (category) {
            case "Food":
                iconResId = android.R.drawable.ic_menu_info_details;
                colorResId = android.R.color.holo_red_dark;
                break;
            case "Transport":
                iconResId = android.R.drawable.ic_menu_compass;
                colorResId = android.R.color.holo_blue_dark;
                break;
            case "Shopping":
                iconResId = android.R.drawable.ic_menu_agenda;
                colorResId = android.R.color.holo_purple;
                break;
            case "Entertainment":
                iconResId = android.R.drawable.ic_menu_gallery;
                colorResId = android.R.color.holo_green_dark;
                break;
            case "Health":
                iconResId = android.R.drawable.ic_dialog_info;
                colorResId = android.R.color.holo_red_light;
                break;
            case "Education":
                iconResId = android.R.drawable.ic_menu_view;
                colorResId = android.R.color.holo_blue_light;
                break;
            case "Housing":
                iconResId = android.R.drawable.ic_input_get;
                colorResId = android.R.color.darker_gray;
                break;
            case "Utilities":
                iconResId = android.R.drawable.ic_menu_manage;
                colorResId = android.R.color.holo_orange_dark;
                break;
            default:
                iconResId = android.R.drawable.ic_menu_more;
                colorResId = android.R.color.darker_gray;
        }
        imageView.setImageResource(iconResId);
        imageView.setColorFilter(ContextCompat.getColor(requireContext(), colorResId));
    }

    private void updateCategoryIconDisplay() {
        setCategoryIcon(imgCategory, selectedCategory);
    }

    // ✨ MODIFIED: Save expense with selected date
    private void saveExpense() {
        Log.d(TAG, ">>> saveExpense clicked");

        String expenseName = edtExpenseName.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();

        if (userId == 0) {
            Toast.makeText(getContext(), "❌ Error: User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(expenseName)) {
            edtExpenseName.setError("Please enter expense name");
            edtExpenseName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(amountStr)) {
            edtAmount.setError("Please enter amount");
            edtAmount.requestFocus();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                edtAmount.setError("Amount must be greater than 0");
                edtAmount.requestFocus();
                return;
            }

            // ✨ MODIFIED: Create Express with selected date
            String selectedDate = getFormattedDateForDatabase();
            Express newExpense = new Express(expenseName, amount, selectedCategory, userId, selectedDate);

            long result = expressRepository.addExpress(newExpense);

            if (result > 0) {
                Toast.makeText(getContext(),
                        "✅ Expense saved successfully on " + formatDate(selectedCalendar.getTime()),
                        Toast.LENGTH_SHORT).show();
                clearForm();
                loadExpenses();
            } else {
                Toast.makeText(getContext(), "❌ Error: Failed to save expense", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            edtAmount.setError("Invalid amount");
            edtAmount.requestFocus();
        } catch (Exception e) {
            Toast.makeText(getContext(), "❌ Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    // ✨ MODIFIED: Clear form and reset date to today
    private void clearForm() {
        edtExpenseName.setText("");
        edtAmount.setText("");
        selectedCategory = "Food";
        tvSelectedCategory.setText("Category: Food");

        // ✨ Reset date to today
        selectedCalendar = Calendar.getInstance();
        updateDateDisplay();

        updateCategoryIconDisplay();
        setupCategoryIcons();
        edtExpenseName.requestFocus();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExpenses();
    }
}