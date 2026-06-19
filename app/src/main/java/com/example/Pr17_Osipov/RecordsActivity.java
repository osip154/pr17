package com.example.pr17_mirzakamilov_pr23103;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecordsActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        LinearLayout container = findViewById(R.id.recordsContainer);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("mytable", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int idColIndex    = c.getColumnIndex("id");
            int nameColIndex  = c.getColumnIndex("name");
            int emailColIndex = c.getColumnIndex("email");
            int ageColIndex   = c.getColumnIndex("age");
            int phoneColIndex = c.getColumnIndex("phone");

            do {
                String row = "ID: " + c.getInt(idColIndex)
                        + "\nИмя: " + c.getString(nameColIndex)
                        + "\nEmail: " + c.getString(emailColIndex)
                        + "\nВозраст: " + c.getInt(ageColIndex)
                        + "\nТелефон: " + c.getLong(phoneColIndex);

                Log.d(LOG_TAG, row);

                TextView tv = new TextView(this);
                tv.setText(row);
                tv.setTextSize(15);
                tv.setPadding(20, 20, 20, 20);
                tv.setBackgroundColor(0xFFFFFFFF);
                tv.setTextColor(0xFF333333);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 12);
                tv.setLayoutParams(params);

                container.addView(tv);

            } while (c.moveToNext());

        } else {
            TextView tv = new TextView(this);
            tv.setText("Записей нет!");
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER);
            container.addView(tv);
        }

        c.close();
        dbHelper.close();
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "email text,"
                    + "age integer,"
                    + "phone integer"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }
}