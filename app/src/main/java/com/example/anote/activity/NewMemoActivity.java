package com.example.anote.activity;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.anote.R;
import com.example.anote.adapter.item.item.Memo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewMemoActivity extends AppCompatActivity {

    Button mBtnSetTextColor;
    Button mBtnSaveMemo;
    Button mBtnLoadImage;

    EditText mMemoTitle;
    EditText mMemoContents;

    String requestCode;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_memo);

        mBtnSetTextColor = findViewById(R.id.btnSetTextColor);
        mBtnSaveMemo = findViewById(R.id.btnSaveMemo);
        mBtnLoadImage = findViewById(R.id.btnLoadImage);
        mMemoTitle = findViewById(R.id.memoTitle);
        mMemoContents = findViewById(R.id.memoContents);

        Intent intent = getIntent();
        requestCode = intent.getStringExtra("requestCode");
        mMemoTitle.setText(intent.getStringExtra("title"));
        mMemoContents.setText(intent.getStringExtra("memoTextContents"));
        id = intent.getStringExtra("id");

        mBtnSaveMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                String date;
                String title;
                String textContents;
                String ImageContents;

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = df.format(calendar.getTime());
                Log.i("ddd",date);
                title = mMemoTitle.getText().toString();
                textContents = mMemoContents.getText().toString(); // +image


                Intent newMemo = new Intent();
                newMemo.putExtra("date", date);
                newMemo.putExtra("title", title);
                newMemo.putExtra("textContents", textContents);
                if(requestCode.equals("1")){
                    setResult(1, newMemo);
                }
                else if(requestCode.equals("2")){
                    newMemo.putExtra("id", id);
                    setResult(2, newMemo);
                }
                finish();
            }
        });

    }
}