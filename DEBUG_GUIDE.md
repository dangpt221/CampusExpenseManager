# 🔧 HƯỚNG DẪN DEBUG: Lỗi Ghi Lại Thất Bại

## 📱 Cách Kiểm Tra Logcat

### **Bước 1: Mở Logcat trong Android Studio**
```
View → Tool Windows → Logcat
hoặc: Alt + 6
```

### **Bước 2: Filter log theo tag**
```
BudgetItemRepository
AddBudgetActivity
SQLite
Exception
```

### **Bước 3: Chạy app & tạo ngân sách mới**
1. Nhấn nút "Ghi lại" (Add Budget)
2. Nhập số tiền: **12000**
3. Nhấn "Ghi lại"
4. Xem Logcat

---

## ✅ KỲ VỌNG LOG KHI THÀNH CÔNG

```
D/BudgetRepository: ✓ Insert budget success - ID: 1, UserId: 1
D/AddBudgetActivity: Creating 9 budget items with amount: 1333.3333333333333 each
D/BudgetItemRepository: ✓ Insert budget item success - ID: 1, Category: Ăn uống, Amount: 1333.3333333333333
D/BudgetItemRepository: ✓ Insert budget item success - ID: 2, Category: Giao thông, Amount: 1333.3333333333333
D/BudgetItemRepository: ✓ Insert budget item success - ID: 3, Category: Mua sắm, Amount: 1333.3333333333333
D/BudgetItemRepository: ✓ Insert budget item success - ID: 4, Category: Giải trí, Amount: 1333.3333333333333
D/BudgetItemRepository: ✓ Insert budget item success - ID: 5, Category: Y tế, Amount: 1333.3333333333333
D/BudgetItemRepository: ✓ Insert budget item success - ID: 6, Category: Giáo dục, Amount: 1333.3333333333333
D/BudgetItemRepository: ✓ Insert budget item success - ID: 7, Category: Nhà ở, Amount: 1333.3333333333333
D/BudgetItemRepository: ✓ Insert budget item success - ID: 8, Category: Utilities, Amount: 1333.3333333333333
D/BudgetItemRepository: ✓ Insert budget item success - ID: 9, Category: Khác, Amount: 1333.3333333333333
```

---

## ❌ KỲ VỌNG LOG KHI CÓ LỖI

### **Lỗi 1: Budget ID không hợp lệ**
```
E/BudgetItemRepository: Budget ID is invalid: 0
E/BudgetItemRepository: ✗ Failed to create budget item: Ăn uống
```

### **Lỗi 2: Category name là null**
```
E/BudgetItemRepository: Category name is null or empty
E/BudgetItemRepository: ✗ Failed to create budget item: Ăn uống
```

### **Lỗi 3: Database error (Foreign Key constraint)**
```
E/BudgetItemRepository: ✗ Error adding budget item: UNIQUE constraint failed
E/BudgetItemRepository: Item details - BudgetId: 1, Category: Ăn uống, Amount: 1333.33
```

### **Lỗi 4: Database không tồn tại (rare)**
```
E/BudgetItemRepository: ✗ Error adding budget item: no such table: budget_items
```

---

## 🗄️ Cách Kiểm Tra Database Trực Tiếp

### **Bước 1: Kết nối Database via ADB**
```bash
# Lấy ID app
adb shell pm list packages | grep campus

# Kết nối database
adb shell
run-as com.example.campusexpensesmanagermer
cd /data/data/com.example.campusexpensesmanagermer/databases
sqlite3 campus_expense.db

# Hoặc nhanh hơn
adb shell "run-as com.example.campusexpensesmanagermer sqlite3 /data/data/com.example.campusexpensesmanagermer/databases/campus_expense.db"
```

### **Bước 2: Kiểm tra dữ liệu**
```sql
-- Kiểm tra budgets
SELECT * FROM budgets;

-- Kiểm tra budget_items
SELECT * FROM budget_items;

-- Chi tiết budget_items
SELECT b.id, b.category_id, b.allocated_amount, b.budget_id 
FROM budget_items b 
ORDER BY b.id DESC LIMIT 20;

-- Đếm số items
SELECT COUNT(*) FROM budget_items;

-- Kiểm tra constraints
PRAGMA foreign_key_list(budget_items);

-- Thoát
.quit
```

---

## 🐛 Các Lỗi Thường Gặp & Cách Fix

### **Lỗi 1: "FOREIGN KEY constraint failed"**
**Nguyên nhân:** `budget_id` trong insert không tồn tại ở bảng `budgets`
**Fix:**
```java
// Trong BudgetRepository.addBudget()
// Verify budgetId được trả về > 0
if (id == -1) {
    Log.e(TAG, "Failed to insert budget!");
    return -1;
}
```

### **Lỗi 2: "UNIQUE constraint failed"**
**Nguyên nhân:** Đang insert record trùng lặp
**Fix:** Xóa dữ liệu cũ hoặc thêm check trùng

### **Lỗi 3: "no such column"**
**Nguyên nhân:** Tên cột sai hoặc database schema cũ
**Fix:** Increment DB_VERSION để trigger `onUpgrade()`

### **Lỗi 4: "database is locked"**
**Nguyên nhân:** Không đóng `db` sau khi xong
**Fix:** Luôn dùng `finally { if (db != null) db.close(); }`

---

## 📊 Chi Tiết Schema Kiểm Tra

```sql
-- Kiểm tra structure bảng
.schema budget_items

-- Output mong đợi:
CREATE TABLE IF NOT EXISTS budget_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT, 
    budget_id INTEGER NOT NULL, 
    category_id TEXT, 
    allocated_amount REAL NOT NULL, 
    created_at DATETIME DEFAULT (datetime('now')), 
    FOREIGN KEY(budget_id) REFERENCES budgets(id) ON DELETE CASCADE
);

-- Kiểm tra indexes
.indexes budget_items
```

---

## 🚀 Automated Debug Script

**Tạo file: `debug_database.sh`**
```bash
#!/bin/bash
APP_PACKAGE="com.example.campusexpensesmanagermer"
DB_PATH="/data/data/${APP_PACKAGE}/databases/campus_expense.db"

echo "=== Checking Database ==="
adb shell "run-as ${APP_PACKAGE} sqlite3 ${DB_PATH} '.schema budget_items'"

echo -e "\n=== Budgets Records ==="
adb shell "run-as ${APP_PACKAGE} sqlite3 ${DB_PATH} 'SELECT COUNT(*) as total_budgets FROM budgets;'"

echo -e "\n=== Budget Items Records ==="
adb shell "run-as ${APP_PACKAGE} sqlite3 ${DB_PATH} 'SELECT COUNT(*) as total_items FROM budget_items;'"

echo -e "\n=== Recent Budget Items ==="
adb shell "run-as ${APP_PACKAGE} sqlite3 ${DB_PATH} 'SELECT * FROM budget_items ORDER BY id DESC LIMIT 10;'"

echo -e "\n=== Foreign Key Check ==="
adb shell "run-as ${APP_PACKAGE} sqlite3 ${DB_PATH} 'PRAGMA foreign_key_list(budget_items);'"
```

**Chạy script:**
```bash
chmod +x debug_database.sh
./debug_database.sh
```

---

## 📋 Checklist Debug

- [ ] Logcat có log "✓ Insert budget item success" cho 9 items?
- [ ] Toast thông báo "✅ Thêm ngân sách thành công! (9 danh mục)"?
- [ ] Database có 9 records mới trong `budget_items`?
- [ ] Foreign key constraint không bị vi phạm?
- [ ] `allocated_amount` có giá trị hợp lệ (>0)?
- [ ] `category_id` không phải null?

---

## ✅ Khi Nào Fix Thành Công

1. ✅ App không crash khi thêm budget
2. ✅ Toast hiển thị "✅ Thêm ngân sách thành công!"
3. ✅ Logcat không có error
4. ✅ Database có 9 items mới
5. ✅ Quay lại màn hình chính, nhấn vào budget → thấy 9 danh mục với số tiền đã phân bổ

---

**Happy Debugging! 🎉**

