package com.example.campusexpensesmanagermer.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.campusexpensesmanagermer.Models.Express;
import com.example.campusexpensesmanagermer.R;
import com.example.campusexpensesmanagermer.Repositories.ExpressRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExpressFragment extends Fragment {

    private EditText edtExpenseName, edtAmount;
    private Button btnAddExpense, btnClear;
    private LinearLayout categoryContainer;
    private TextView tvSelectedCategory, tvDate;
    private ImageView imgCategory;

    private ExpressRepository expressRepository;
    private SharedPreferences prefs;
    private int userId;

    private String selectedCategory = "Ăn uống";
    private static final String PREFS_NAME = "CampusExpensesPrefs";

    private final String[] categories = {
            "Ăn uống", "Giao thông", "Mua sắm", "Giải trí",
            "Y tế", "Giáo dục", "Nhà ở", "Utilities", "Khác"
    };

    public ExpressFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        expressRepository = new ExpressRepository(getContext());
        prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        userId = prefs.getInt("ID_USER", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_express, container, false);

        // Initialize views
        edtExpenseName = view.findViewById(R.id.edtExpenseName);
        edtAmount = view.findViewById(R.id.edtAmount);
        btnAddExpense = view.findViewById(R.id.btnAddExpense);
        btnClear = view.findViewById(R.id.btnClear);
        categoryContainer = view.findViewById(R.id.categoryContainer);
        tvSelectedCategory = view.findViewById(R.id.tvSelectedCategory);
        tvDate = view.findViewById(R.id.tvDate);
        imgCategory = view.findViewById(R.id.imgCategory);

        updateDateDisplay();
        setupCategoryIcons();

        btnAddExpense.setOnClickListener(v -> saveExpense());
        btnClear.setOnClickListener(v -> clearForm());

        return view;
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
            tvSelectedCategory.setText("Loại chi: " + category);
            updateCategoryIconDisplay();
            setupCategoryIcons(); // refresh
        });

        return button;
    }

    private void setCategoryIcon(ImageView imageView, String category) {
        int iconResId, colorResId;
        switch (category) {
            case "Ăn uống": iconResId = android.R.drawable.ic_menu_info_details; colorResId = android.R.color.holo_red_dark; break;
            case "Giao thông": iconResId = android.R.drawable.ic_menu_compass; colorResId = android.R.color.holo_blue_dark; break;
            case "Mua sắm": iconResId = android.R.drawable.ic_menu_agenda; colorResId = android.R.color.holo_purple; break;
            case "Giải trí": iconResId = android.R.drawable.ic_menu_gallery; colorResId = android.R.color.holo_green_dark; break;
            case "Y tế": iconResId = android.R.drawable.ic_dialog_info; colorResId = android.R.color.holo_red_light; break;
            case "Giáo dục": iconResId = android.R.drawable.ic_menu_view; colorResId = android.R.color.holo_blue_light; break;
            case "Nhà ở": iconResId = android.R.drawable.ic_input_get; colorResId = android.R.color.darker_gray; break;
            case "Utilities": iconResId = android.R.drawable.ic_menu_manage; colorResId = android.R.color.holo_orange_dark; break;
            default: iconResId = android.R.drawable.ic_menu_more; colorResId = android.R.color.darker_gray;
        }
        imageView.setImageResource(iconResId);
        imageView.setColorFilter(ContextCompat.getColor(requireContext(), colorResId));
    }

    private void updateCategoryIconDisplay() {
        setCategoryIcon(imgCategory, selectedCategory);
    }

    private void updateDateDisplay() {
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText("Ngày: " + currentDate);
    }

    private void saveExpense() {
        String expenseName = edtExpenseName.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();

        if (TextUtils.isEmpty(expenseName)) {
            edtExpenseName.setError("Vui lòng nhập tên chi tiêu");
            edtExpenseName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(amountStr)) {
            edtAmount.setError("Vui lòng nhập số tiền");
            edtAmount.requestFocus();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                edtAmount.setError("Số tiền phải lớn hơn 0");
                edtAmount.requestFocus();
                return;
            }
            if (userId == 0) {
                Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Express newExpense = new Express(expenseName, amount, selectedCategory, userId);
            long result = expressRepository.addExpress(newExpense);

            if (result > 0) {
                Toast.makeText(getContext(), "✅ Ghi lại thành công!", Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(getContext(), "Lỗi: Ghi lại thất bại", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            edtAmount.setError("Số tiền không hợp lệ");
            edtAmount.requestFocus();
        }
    }

    private void clearForm() {
        edtExpenseName.setText("");
        edtAmount.setText("");
        selectedCategory = "Ăn uống";
        tvSelectedCategory.setText("Loại chi: Ăn uống");
        updateCategoryIconDisplay();
        setupCategoryIcons();
        edtExpenseName.requestFocus();
    }
}
