package com.example.pr17_mirzakamilov_pr23103;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";

    EditText etName, etEmail, etAge, etPhone, etId;
    Button btnAdd, btnRead, btnUpd, btnDel, btnClear;

    TextView tvResult;

    DBHelper dbHelper;

    Button btnShowAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAge = findViewById(R.id.etAge);
        etPhone = findViewById(R.id.etPhone);
        etId = findViewById(R.id.etId);
        tvResult = findViewById(R.id.tvResult);

        btnAdd = findViewById(R.id.btnAdd);
        btnRead = findViewById(R.id.btnRead);
        btnUpd = findViewById(R.id.btnUpd);
        btnDel = findViewById(R.id.btnDel);
        btnClear = findViewById(R.id.btnClear);
        btnShowAll = findViewById(R.id.btnShowAll);

        dbHelper = new DBHelper(this);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addRecord(); }
        });

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { readRecords(); }
        });

        btnUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { updateRecord(); }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { deleteRecord(); }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { clearTable(); }
        });

        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
                startActivity(intent);
            }
        });
    }

    void addRecord() {
        String name  = etName.getText().toString();
        String email = etEmail.getText().toString();
        String age   = etAge.getText().toString();
        String phone = etPhone.getText().toString();

        if (name.isEmpty()) {
            etName.setError("Введите имя");
            return;
        }

        if (email.isEmpty() || !email.contains("@")) {
            etEmail.setError("Некорректный email");
            return;
        }

        if (!age.isEmpty()) {
            try {
                int ageValue = Integer.parseInt(age);
                if (ageValue < 0) {
                    etAge.setError("Возраст не может быть отрицательным");
                    return;
                }
            } catch (NumberFormatException e) {
                etAge.setError("Введите число");
                return;
            }
        }

        if (!phone.isEmpty() && phone.length() != 11) {
            etPhone.setError("Неправильный номер телефона");
            return;
        }

        ContentValues cv = new ContentValues();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        cv.put("name",  name);
        cv.put("email", email);
        cv.put("age",   age.isEmpty()   ? 0 : Integer.parseInt(age));
        cv.put("phone", phone.isEmpty() ? 0 : Long.parseLong(phone));

        long rowID = db.insert("mytable", null, cv);
        Log.d(LOG_TAG, "Запись добавлена, ID = " + rowID);

        tvResult.setText("Запись добавлена, ID = " + rowID);
        Toast.makeText(this, "Добавлено!", Toast.LENGTH_SHORT).show();
        dbHelper.close();
    }

    void readRecords() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.d(LOG_TAG, "Все записи в mytable:");

        Cursor c = db.query("mytable", null, null, null, null, null, null);

        StringBuilder sb = new StringBuilder();

        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int emailColIndex = c.getColumnIndex("email");
            int ageColIndex = c.getColumnIndex("age");
            int phoneColIndex = c.getColumnIndex("phone");

            do {
                String row = "ID=" + c.getInt(idColIndex)
                        + ", Имя=" + c.getString(nameColIndex)
                        + ", Email=" + c.getString(emailColIndex)
                        + ", Возраст=" + c.getInt(ageColIndex)
                        + ", Телефон=" + c.getLong(phoneColIndex);

                Log.d(LOG_TAG, row);
                sb.append(row).append("\n");

            } while (c.moveToNext());

        } else {
            Log.d(LOG_TAG, "0 rows");
            sb.append("Таблица пуста");
        }

        c.close();
        dbHelper.close();

        tvResult.setText(sb.toString());
    }

    void updateRecord() {
        String id = etId.getText().toString();

        if (id.equalsIgnoreCase("")) {
            Toast.makeText(this, "Введите ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String age = etAge.getText().toString();
        String phone = etPhone.getText().toString();

        if (name.isEmpty()) {
            etName.setError("Введите имя");
            return;
        }

        if (email.isEmpty() || !email.contains("@")) {
            etEmail.setError("Некорректный email");
            return;
        }

        if (!age.isEmpty()) {
            try {
                int ageValue = Integer.parseInt(age);
                if (ageValue < 0) {
                    etAge.setError("Возраст не может быть отрицательным");
                    return;
                }
            } catch (NumberFormatException e) {
                etAge.setError("Введите число");
                return;
            }
        }

        if (!phone.isEmpty() && phone.length() < 5) {
            etPhone.setError("Слишком короткий номер");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("name",  name);
        cv.put("email", email);
        cv.put("age",   age.isEmpty()   ? 0 : Integer.parseInt(age));
        cv.put("phone", phone.isEmpty() ? 0 : Long.parseLong(phone));

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int updCount = db.update("mytable", cv, "id = ?", new String[]{id});
        Log.d(LOG_TAG, "Обновлено записей: " + updCount);

        tvResult.setText("Обновлено записей: " + updCount);
        dbHelper.close();
    }

    void deleteRecord() {
        String id = etId.getText().toString();

        if (id.equalsIgnoreCase("")) {
            Toast.makeText(this, "Введите ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int delCount = db.delete("mytable", "id = " + id, null);
        Log.d(LOG_TAG, "Удалено записей: " + delCount);

        tvResult.setText("Удалено записей: " + delCount);
        dbHelper.close();
    }

    void clearTable() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int clearCount = db.delete("mytable", null, null);
        Log.d(LOG_TAG, "Удалено всех записей: " + clearCount);

        tvResult.setText("Таблица очищена. Удалено записей: " + clearCount);
        Toast.makeText(this, "Таблица очищена!", Toast.LENGTH_SHORT).show();
        dbHelper.close();
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "onCreate database");

            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "email text,"
                    + "age integer,"
                    + "phone integer"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

    }
}